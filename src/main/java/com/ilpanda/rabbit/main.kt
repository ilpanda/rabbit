@file:JvmName("Main")

package com.ilpanda.rabbit

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class NoOpCommand(name: String = "rabbit") : CliktCommand(name = name) {
    override fun run() {

    }
}

class AdbCommand(
    name: String = "adb",
    override val commandHelp: String = "android adb command"
) : CliktCommand(name = name) {

    private val logCurrentActivity by option(
        "--current",
        "-c",
        help = "print current activity name"
    ).flag(default = false)

    private val logAllActivity by option(
        "--all",
        "-a",
        help = "print all activities name"
    ).flag(default = false)

    private val logAllFragment by option(
        "--fragment",
        "-f",
        help = "print specific package fragments"
    ).flag(default = false)

    private val logSpecificPackageActivity by option(
        "--print",
        "-p",
        metavar = "packageName",
        help = "print specific package activities"
    )

    override fun run() {
        val res = """adb shell dumpsys activity activities | grep  mResumedActivity| awk '{print $4}'""".exec()
        val packageName = res.split("/")[0]

        if (logCurrentActivity) {
            println(res)
        }

        if (logAllActivity) {
            println("""adb shell dumpsys activity activities | grep 'Hist #'""".exec())
        }

        if (logAllFragment) {
            println(("""adb shell dumpsys activity $packageName |grep  -E '^\s*#\d'| grep -v -E  'ReportFragment|plan'""").exec())
        }

        if (!logSpecificPackageActivity.isNullOrEmpty()) {
            println(("""adb shell dumpsys activity activities | grep 'Hist #' | grep $logSpecificPackageActivity""").exec())
        }

    }
}

fun main(args: Array<String>) {
    NoOpCommand().subcommands(AdbCommand()).main(args)
}