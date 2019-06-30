package net.jeikobu.jbase.impl.config

import net.dv8tion.jda.core.entities.Guild

class VolatileStorage {
    private val map = HashMap<String, String>()

    fun get(guild: Guild, key: String): String? {
        return map[guild.idLong.toString() + "_" + key]
    }

    fun set(guild: Guild, key: String, value: String) {
        map[guild.idLong.toString() + "_" + key] = value
    }
}
