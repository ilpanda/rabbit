package com.ilpanda.rabbit

val hashMap = hashMapOf<String, String>()

fun getVersionBuild(key: String): String? {
    if (hashMap.size == 0) {
        hashMap["21"] = "Android 5.0，Lollipop，API 21"
        hashMap["22"] = "Android 5.1，Lollipop，API 22"
        hashMap["23"] = "Android 6.0，Marshmallow，API 12"
        hashMap["24"] = "Android 7.0，Nougat，API 24"
        hashMap["25"] = "Android 7.1，Nougat，API 25"
        hashMap["26"] = "Android 8.0，Oreo，API 26"
        hashMap["27"] = "Android 8.1，Oreo，API 27"
        hashMap["28"] = "Android 9.0，Pie，API 28"
        hashMap["29"] = "Android 10.0，Q，API 29"
        hashMap["30"] = "Android 11.0，R，API 30"
        hashMap["31"] = "Android 12.0，S，API 31"
        hashMap["32"] = "Android 12.1，S，API 32"
        hashMap["33"] = "Android 13.0，T，API 33"
    }
    return hashMap[key]

}
