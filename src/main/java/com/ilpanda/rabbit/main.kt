@file:JvmName("Main")

package com.ilpanda.rabbit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.ilpanda.rabbit.command.*

class NoOpCommand(name: String = "rabbit") : CliktCommand(name = name) {
    override fun run() {

    }
}

class AdbCommand(
    name: String = "adb",
    override val commandHelp: String = "android adb command"
) : CliktCommand(name = name) {

    val logConfig by LogCommandConfig()

    val appConfig by AppCommandConfig()

    val actionConfig by option("-ac", "--action", help = "android adb start system activity").choice(
        "locale" to "android.settings.LOCALE_SETTINGS",
        "developer" to "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
        "application" to "android.settings.APPLICATION_SETTINGS",
        "notification" to "android.settings.ALL_APPS_NOTIFICATION_SETTING"
    )

    override fun run() {
        val res = """adb shell dumpsys activity activities | grep  mResumedActivity| awk '{print $4}'""".exec()
        val packageName = res.split("/")[0]

        execute(packageName, logConfig)

        execute(appConfig)

        actionConfig?.also {
            executeQuickStart(it)
        }

    }

    private fun executeQuickStart(action: String?) {
        """adb shell am start -a $action""".exec()
    }

    private fun execute(packageName: String, config: LogCommandConfig) {
        listOf(
            LogCurrentActivityCommandStrategy(),
            LogAllActivityCommandStrategy(),
            LogAllFragmentCommandStrategy(),
            LogSpecificPackageActivityCommandStrategy(),
        ).forEach {
            if (it.canHandle(packageName, config)) {
                it.run(packageName, config)
            }
        }
    }

    private fun execute(config: AppCommandConfig) {
        listOf(
            ClearAppDataCommandStrategy(config.clearAppPackageName),
            KillCommand(config.killAppPackageName),
            GrantCommand(config.grantAppPermissionPackageName),
            RevokeCommand(config.revokeAppPermissionPackageName),
            StartActivityCommand(config.startAppPackageName),
            RestartAppCommand(config.restartPackageName),
        ).forEach {
            if (it.canHandle()) {
                it.run(it.packageName!!)
            }
        }
    }

}

fun main(args: Array<String>) {
    NoOpCommand().subcommands(AdbCommand().subcommands()).main(args)
}