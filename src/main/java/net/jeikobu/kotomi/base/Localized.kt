package net.jeikobu.kotomi.base

import java.util.*
import java.util.regex.Pattern

abstract class Localized {
    fun getLocalized(locale: Locale, key: String): String {
        val bundle = ResourceBundle.getBundle(this.javaClass.simpleName, locale)
        return bundle.getString(key)
    }

    fun getLocalized(locale: Locale, key: String, vararg elements: Any): String {
        var localized = getLocalized(locale, key)

        val p = Pattern.compile("\\{\\}")
        for (element in elements) {
            localized = p.matcher(localized).replaceFirst(element.toString())
        }

        return localized
    }
}