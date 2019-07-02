package net.jeikobu.jbase.config

import net.dv8tion.jda.core.entities.Guild
import org.joda.convert.StringConvert
import java.util.*
import kotlin.reflect.KClass

abstract class AbstractGuildConfig(val guild: Guild) {
    abstract var commandPrefix: String?
    abstract var guildLocale: Locale?

    inline fun <reified T : Any> setValue(key: String, value: T?) {
        if (value == null) {
            setValue(key, null)
        } else {
            setValue(key, StringConvert.INSTANCE.convertToString(value))
        }
    }

    abstract fun setValue(key: String, value: String?)

    inline fun <reified T : Any> getValue(key: String, defaultValue: String? = null): T? {
        return getValue(key, defaultValue, T::class)
    }

    abstract fun <T : Any> getValue(key: String, defaultValue: String? = null, valueType: KClass<T>): T?
}