package net.jeikobu.kotomi.base.config

import java.util.*

interface IGlobalConfig {
    val defaultCommandPrefix: String
    val token: String
    val useDefaultCommands: Boolean
    val globalLocale: Locale

    fun <T> getValue(key: String, valueType: Class<T>): T
}