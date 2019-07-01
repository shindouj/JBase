package net.jeikobu.jbase.config

import net.dv8tion.jda.core.entities.Guild
import java.util.*
import kotlin.reflect.KClass

abstract class AbstractGuildConfig(guild: Guild) {
    abstract fun getCommandPrefix(): String?
    abstract fun setCommandPrefix(prefix: String)

    abstract fun getGuildLocale(): Locale?
    abstract fun setGuildLocale(locale: Locale)
    abstract fun setValue(key: String, value: String)

    inline fun <reified T : Any> getValue(key: String, defaultValue: String? = null): T? {
        return getValue(key, defaultValue, T::class)
    }

    abstract fun <T : Any> getValue(key: String, defaultValue: String? = null, valueType: KClass<T>): T?
}