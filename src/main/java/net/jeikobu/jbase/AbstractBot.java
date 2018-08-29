package net.jeikobu.jbase;

import net.jeikobu.jbase.command.AbstractCommand;
import net.jeikobu.jbase.command.CommandManager;
import net.jeikobu.jbase.config.AbstractConfigManager;
import net.jeikobu.jbase.impl.commands.ChangeLocaleCommand;
import net.jeikobu.jbase.impl.commands.ChangePrefixCommand;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractBot {
    private final List<Class<? extends AbstractCommand>> defaultCommands = Arrays
            .asList(ChangeLocaleCommand.class, ChangePrefixCommand.class);

    private final IDiscordClient client;
    private final EventDispatcher dispatcher;
    private final CommandManager commandManager;
    private final AbstractConfigManager configManager;

    public AbstractBot(ClientBuilder clientBuilder, AbstractConfigManager configManager) {
        commandManager = new CommandManager(configManager);
        this.configManager = configManager;

        if (configManager.getGlobalConfig().useDefaultCommands()) {
            registerDefaultCommands();
        }

        clientBuilder.withToken(configManager.getGlobalConfig().getToken());

        client = clientBuilder.build();
        dispatcher = client.getDispatcher();
        dispatcher.registerListener(commandManager);
    }

    public void registerDefaultCommands() {
        commandManager.registerAll(defaultCommands);
    }

    public void deregisterDefaultCommands() {
        commandManager.deregisterAll(defaultCommands);
    }

    public IDiscordClient getClient() {
        return client;
    }

    public EventDispatcher getDispatcher() {
        return dispatcher;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AbstractConfigManager getConfigManager() {
        return configManager;
    }
}
