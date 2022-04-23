package com.ilpanda.rabbit

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class LogCommandConfig : OptionGroup(
    name = "Print Options",
) {

    val logCurrentActivity by option(
        "--current",
        "-c",
        help = "print current activity name"
    ).flag(default = false)

    val logAllActivity by option(
        "--all",
        "-a",
        help = "print all activities name"
    ).flag(default = false)

    val logAllFragment by option(
        "--fragment",
        "-f",
        help = "print specific package fragments"
    ).flag(default = false)

    val logSpecificPackageActivity by option(
        "--print",
        "-p",
        metavar = "packageName",
        help = "print specific package activities"
    )

}


