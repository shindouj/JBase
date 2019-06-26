package net.jeikobu.jbase.impl.config

import sx.blah.discord.handle.obj.IGuild

class VolatileStorage {
    private val map = HashMap<String, String>()

    fun get(guild: IGuild, key: String): String? {
        return map[guild.longID.toString() + "_" + key]
    }

    fun set(guild: IGuild, key: String, value: String) {
        map[guild.longID.toString() + "_" + key] = value
    }
}
