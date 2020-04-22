package net.jeikobu.kotomi.base.command

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import net.jeikobu.kotomi.base.config.AbstractConfigManager

data class CommandData(val destinationGuild: Guild, val destinationChannel: TextChannel, val sendingUser: Member,
                       val configManager: AbstractConfigManager, val args: List<String>)