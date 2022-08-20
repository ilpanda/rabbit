package com.ilpanda.rabbit

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.option

class AppCommandConfig : OptionGroup(
    name = "App Options"
) {

    val clearAppPackageName by option(
        "--clear",
        metavar = "packageName",
        help = "clear app data , use adb shell pm clear [packageName]",
    )
    val killAppPackageName by option(
        "--kill",
        metavar = "packageName",
        help = "force stop app , use adb shell am force-stop [packageName]",
    )

    val grantAppPermissionPackageName by option(
        "--grant",
        metavar = "packageName",
        help = "grant app all permission , use adb shell pm grant [packageName] [permission] ",
    )

    val revokeAppPermissionPackageName by option(
        "--revoke",
        metavar = "packageName",
        help = "revoke app all permission , use adb shell pm revoke [packageName] [permission] ",
    )

    val startAppPackageName by option(
        "--start",
        metavar = "packageName",
        help = "start app , use adb shell monkey -p  [packageName]  -c android.intent.category.LAUNCHER 1",
    )

    val restartPackageName by option(
        "--restart",
        metavar = "packageName",
        help = "restart app , use --kill and --start command",
    )


}