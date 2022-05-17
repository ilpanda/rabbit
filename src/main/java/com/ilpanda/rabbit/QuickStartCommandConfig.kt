package com.ilpanda.rabbit



sealed class Action(open val action: String) {

    class LocaleAction() : Action("android.settings.LOCALE_SETTINGS")

}
