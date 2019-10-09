package net.jeikobu.kotomi.base

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.utils.cache.CacheFlag
import net.jeikobu.kotomi.base.command.AbstractCommand
import net.jeikobu.kotomi.base.command.CommandManager
import net.jeikobu.kotomi.base.config.AbstractConfigManager
import net.jeikobu.kotomi.base.impl.commands.ChangeLocaleCommand
import net.jeikobu.kotomi.base.impl.commands.ChangePrefixCommand
import java.util.*
import kotlin.reflect.KClass

abstract class AbstractBot(configManager: AbstractConfigManager) {
    protected val defaultCommands: MutableList<KClass<out AbstractCommand>> = Arrays.asList(ChangeLocaleCommand::class, ChangePrefixCommand::class)
    protected val commandManager = CommandManager(configManager)
    private val clientBuilder = JDABuilder()

    val client: JDA

    init {
        if (configManager.globalConfig.useDefaultCommands) {
            registerDefaultCommands()
        }

        clientBuilder.setToken(configManager.globalConfig.token)
        clientBuilder.addEventListener(commandManager)
        clientBuilder.setEnabledCacheFlags(EnumSet.of(CacheFlag.EMOTE))

        client = clientBuilder.build()
        client.awaitReady()
    }

    fun registerDefaultCommands() {
        commandManager.registerAll(defaultCommands)
    }

    fun deregisterDefaultCommands() {
        commandManager.deregisterAll(defaultCommands)
    }
}