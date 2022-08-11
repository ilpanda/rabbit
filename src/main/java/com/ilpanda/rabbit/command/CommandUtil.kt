package com.ilpanda.rabbit.command

import com.ilpanda.rabbit.exec


fun getCurrentPackageAndActivityName(): String {
    return """adb shell dumpsys activity activities| grep -v mResumedActivity| grep  ResumedActivity| grep -v top| awk '{print $4}'""".exec()
        .trimEnd('}')
}

fun getActivityListStringFromTopToBottom(): String {
    return """adb shell dumpsys activity activities | grep -e 'Hist #' -e '* Hist' """.exec()
}

fun log(msg: String) {
    println(msg)
}