package com.ilpanda.rabbit

import com.ilpanda.rabbit.command.log
import okio.buffer
import okio.source
import kotlin.system.exitProcess

fun String.exec(
    ignoreError: Boolean = false,
    exitWhen: ((errorMsg: String) -> Boolean)? = null
): String {
    return Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", this)).let { it ->

        val buffer = it.inputStream.source().buffer()
        var line: String?
        var result: String? = null
        while (buffer.readUtf8Line().also { line = it } != null) {
            if (result == null) {
                result = line
            } else {
                result += "\n" + line
            }
        }
        result?.let {
            return result
        }

        val errorOutput = it.errorStream.source().buffer().readUtf8()
        if (errorOutput.isNotEmpty()) {

            val interceptorRes = exitWhen?.let {
                exitWhen(errorOutput)
            }

            if (interceptorRes == true || !ignoreError) {
                log(errorOutput)
                exitProcess(1)
            }
        }
        errorOutput
    }
}

fun String.multiLine() = split("\\r?\\n|\\r".toRegex())
