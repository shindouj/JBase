package net.jeikobu.jbase

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.jeikobu.jbase.command.CommandManager
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.impl.commands.ChangeLocaleCommand
import net.jeikobu.jbase.impl.commands.ChangePrefixCommand
import java.util.*

abstract class AbstractBot(clientBuilder: JDABuilder, configManager: AbstractConfigManager) {
    private val defaultCommands = Arrays.asList(ChangeLocaleCommand::class, ChangePrefixCommand::class)
    private val commandManager = CommandManager(configManager)
    private val client: JDA

    init {
        if (configManager.globalConfig.useDefaultCommands) {
            registerDefaultCommands()
        }

        clientBuilder.setToken(configManager.globalConfig.token)
        clientBuilder.addEventListener(commandManager)

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