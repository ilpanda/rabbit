package com.ilpanda.rabbit.command

import com.ilpanda.rabbit.LogCommandConfig
import com.ilpanda.rabbit.exec
import com.ilpanda.rabbit.getVersionBuild
import com.ilpanda.rabbit.multiLine
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

interface LogCommandStrategy {

    fun canHandle(packageName: String, commandConfig: LogCommandConfig): Boolean

    fun run(packageName: String, commandConfig: LogCommandConfig)

}

interface AppCommandStrategy {


    val packageName: String?

    fun canHandle(): Boolean = !packageName.isNullOrEmpty()

    fun run(packageName: String)

}

class LogCurrentActivityCommandStrategy : LogCommandStrategy {

    override fun canHandle(packageName: String, commandConfig: LogCommandConfig): Boolean =
        commandConfig.logCurrentActivity

    override fun run(packageName: String, commandConfig: LogCommandConfig) {
        val res = """adb shell dumpsys activity activities | grep  mResumedActivity| awk '{print $4}'""".exec()
        println(res)
    }
}

class LogAllActivityCommandStrategy : LogCommandStrategy {

    override fun canHandle(packageName: String, commandConfig: LogCommandConfig): Boolean = commandConfig.logAllActivity


    override fun run(packageName: String, commandConfig: LogCommandConfig) {
        println("""adb shell dumpsys activity activities | grep 'Hist #'""".exec())
    }
}

class LogAllFragmentCommandStrategy : LogCommandStrategy {

    override fun canHandle(packageName: String, commandConfig: LogCommandConfig): Boolean = commandConfig.logAllFragment


    override fun run(packageName: String, commandConfig: LogCommandConfig) {
        println(("""adb shell dumpsys activity $packageName |grep  -E '^\s*#\d'| grep -v -E  'ReportFragment|plan'""").exec())
    }

}

class LogSpecificPackageActivityCommandStrategy : LogCommandStrategy {

    override fun canHandle(packageName: String, commandConfig: LogCommandConfig): Boolean =
        !commandConfig.logSpecificPackageActivity.isNullOrEmpty()

    override fun run(packageName: String, commandConfig: LogCommandConfig) {
        println(("""adb shell dumpsys activity activities | grep 'Hist #' | grep ${commandConfig.logSpecificPackageActivity}""").exec())
    }

}


/**
 * 清除指定包名 App 数据
 */
class ClearAppDataCommandStrategy(override val packageName: String?) : AppCommandStrategy {

    override fun run(packageName: String) {
        "adb shell pm clear $packageName".exec()
    }
}

/**
 * 杀死指定包名 App
 */
class KillCommand(override val packageName: String?) : AppCommandStrategy {

    override fun run(packageName: String) {
        "adb shell am force-stop $packageName".exec()
    }

}

/**
 * 授权指定包名 App 所有申请权限
 */
class GrantCommand(override val packageName: String?) : AppCommandStrategy {

    override fun run(packageName: String) {
        val multiLineStringList =
            "adb shell dumpsys package $packageName".exec().multiLine()
        val requestedPermissions = getRequestedPermissions(multiLineStringList)
        requestedPermissions.forEach { it ->
            "adb shell pm grant $packageName $it".exec(ignoreError = true, exitWhen = {
                it.contains("Neither user 2000 nor current process has android.permission.GRANT_RUNTIME_PERMISSIONS")
            })
        }
    }

    private fun getRequestedPermissions(list: List<String>): List<String> {
        var requestedPermissionsSection = false
        val requestPermissions: MutableList<String> = ArrayList()
        for (s in list) {
            if (!s.contains(".permission.")) {
                requestedPermissionsSection = false
            }
            if (s.contains("requested permissions:")) {
                requestedPermissionsSection = true
                continue
            }
            if (requestedPermissionsSection) {
                val permissionName = s.replace(":", "").trim { it <= ' ' }
                requestPermissions.add(permissionName)
            }
        }
        return requestPermissions
    }

}

/**
 * 撤销指定包名 App 所有申请权限
 */
class RevokeCommand(override val packageName: String?) : AppCommandStrategy {

    override fun run(packageName: String) {
        val multiLineStringList =
            "adb shell dumpsys package $packageName".exec().multiLine()

        multiLineStringList.filter { it.contains("permission") }.filter { it.contains("granted=true") }
            .map { it.split(":".toRegex()).toTypedArray()[0].trim { it <= ' ' } }
            .forEach {
                "adb shell pm revoke $packageName $it".exec(ignoreError = true)
            }
    }
}

open class AppCommandList(override val packageName: String?, vararg appCommandStrategy: AppCommandStrategy) :
    AppCommandStrategy {

    private val commandList = listOf(*appCommandStrategy)

    override fun canHandle(): Boolean {
        commandList.forEach {
            if (!it.canHandle()) {
                return false
            }
        }
        return true
    }

    override fun run(packageName: String) {
        commandList.forEach {
            it.run(packageName)
        }
    }
}

class RestartAppCommand(packageName: String?) :
    AppCommandList(packageName, KillCommand(packageName), StartActivityCommand(packageName))


/**
 * 启动指定包名的 App
 */
class StartActivityCommand(override val packageName: String?) : AppCommandStrategy {

    override fun run(packageName: String) {
        "adb shell monkey -p  $packageName  -c android.intent.category.LAUNCHER 1".exec()
    }
}


interface DeviceInfoStrategy {

    fun run()

}

class DeviceInfo : DeviceInfoStrategy {
    override fun run() {
        val model = """adb shell getprop ro.product.model""".exec()
        val version = """adb shell getprop ro.build.version.release""".exec()
        val density = """adb shell wm density""".exec()
        val display = """adb shell dumpsys window displays """.exec()
        val androidId = """adb shell settings get secure android_id""".exec()
        val sdkVersion = """adb shell getprop ro.build.version.sdk""".exec()
        val ipAddress = """adb shell ifconfig | grep Mask""".exec(ignoreError = true)

        val displayRes = display.split("\\R".toRegex()).filter {
            it.contains("init=")
        }.first().trim()

        val permissionDeny = ipAddress.contains("Permission denied")
        val ipAddressRes = if (permissionDeny) {
            ""
        } else {
            "ipAddress: ${ipAddress.replace("\\R".toRegex(), "").trim()}"
        }

        val densityRes: String
        val densityScale: Float
        val overrideDensity: String?
        var overrideRes: String = ""
        if (!density.contains("Override density")) {
            densityRes = density.substring(density.indexOf(":") + 1).trim()
            densityScale = densityRes.toFloat() / 160
        } else {
            // 如 OPPO 简易模式可以修改屏幕像素密度。
            densityRes = density.split("\\R".toRegex())[0].substring(density.indexOf(":") + 1).trim()
            overrideDensity = density.split("\\R".toRegex())[1].substring(density.indexOf(":") + 1).trim()
            densityScale = overrideDensity.toFloat() / 160
            overrideRes = "Override density: ${overrideDensity}dpi"
        }

        val versionBuild = getVersionBuild(sdkVersion)
        val res = """
        model: $model   
        version: $versionBuild    
        display: ${displayRes.substring(0, displayRes.indexOf("rng"))}
        Physical density: ${densityRes}dpi  $overrideRes
        density scale: $densityScale
        android_id: $androidId
        $ipAddressRes
        """.trimIndent()
        println(res)

    }
}

class CpuInfo : DeviceInfoStrategy {
    override fun run() {
        println("""adb shell cat /proc/cpuinfo""".exec())
    }
}

class MemInfo : DeviceInfoStrategy {
    override fun run() {
        println("""adb shell cat /proc/meminfo""".exec())
    }
}

class BatteryInfo : DeviceInfoStrategy {
    override fun run() {
        println("""adb shell dumpsys battery""".exec())
    }
}


interface ScreenStrategy {
    fun run()
}

class ScreenshotStrategy : ScreenStrategy {
    override fun run() {
        val timestamp = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date())
        "adb exec-out screencap -p > ${timestamp}_screenshot.png".exec()
    }
}

class Mp4RecordStrategy : ScreenStrategy {

    override fun run() {
        val timestamp = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date())
        "scrcpy -Nr ${timestamp}_record.mp4".exec()
    }

}