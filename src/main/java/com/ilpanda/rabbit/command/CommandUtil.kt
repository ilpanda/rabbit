package com.ilpanda.rabbit.command

import com.ilpanda.rabbit.exec


fun getCurrentPackageAndActivityName(): String {
    val result = """ adb shell dumpsys activity activities | grep  mResumedActivity |awk '{print ${'$'}4}'""".exec()
    if (result.isEmpty()) {
        return """adb shell dumpsys activity activities| grep  ResumedActivity| grep -v top| awk '{print $4}'""".exec()
            .trimEnd('}')
    }
    return result
}

fun getActivityListStringFromTopToBottom(): String {
    return """adb shell dumpsys activity activities | grep -e 'Hist #' -e '* Hist' """.exec()
}

fun log(msg: String) {
    println(msg)
}