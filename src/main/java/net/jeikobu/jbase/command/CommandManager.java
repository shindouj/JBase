package net.jeikobu.jbase.command;

import io.rincl.Rincled;
import net.jeikobu.jbase.config.AbstractConfigManager;
import net.jeikobu.jbase.config.AbstractGuildConfig;
import org.pmw.tinylog.Logger;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import javax.management.ReflectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandManager implements Rincled {
    private final AbstractConfigManager configManager;
    private final List<Class<? extends AbstractCommand>> registeredCommands;

    public CommandManager(AbstractConfigManager configManager) {
        this.configManager = configManager;
        registeredCommands = new ArrayList<>();
    }

    private static Command getCommandAnnotation(Class<? extends AbstractCommand> clazz) {
        Command c = clazz.getAnnotation(Command.class);
        if (c == null) throw new IllegalArgumentException("This class is not correctly annotated!");
        else return c;
    }

    private static AbstractCommand createCommandInstance(Class<? extends AbstractCommand> clazz, IGuild destGuild,
                                                         IChannel destChannel, IUser sender, AbstractConfigManager configManager) throws ReflectionException {
        try {
            Constructor<? extends AbstractCommand> constructor = clazz.getDeclaredConstructor(CommandData.class);

            return constructor.newInstance(new CommandData(destGuild, destChannel, sender, configManager));
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    public final void registerCommand(Class<? extends AbstractCommand> clazz) {
        registeredCommands.add(clazz);
    }

    public final void deregisterCommand(Class<? extends AbstractCommand> clazz) {
        registeredCommands.remove(clazz);
    }

    @EventSubscriber
    public void onMessage(MessageEvent event) {
        IUser sender = event.getAuthor();
        IChannel destChannel = event.getChannel();
        IGuild destGuild = event.getGuild();
        AbstractGuildConfig destGuildConfig = configManager.getGuildConfig(destGuild);

        String commandPrefix;
        Locale locale;

        if (destGuildConfig.getCommandPrefix().isPresent()) {
            commandPrefix = destGuildConfig.getCommandPrefix().get();
        } else {
            commandPrefix = configManager.getGlobalConfig().getDefaultCommandPrefix();
        }

        if (destGuildConfig.getGuildLocale().isPresent()) {
            locale = destGuildConfig.getGuildLocale().get();
        } else {
            locale = configManager.getGlobalConfig().getGlobalLocale();
        }

        String messageString = event.getMessage().getContent();
        IMessage message = event.getMessage();

        if (messageString.length() == 0 || !messageString.startsWith(commandPrefix)) {
            return;
        }

        List<String> args = Arrays.asList(messageString.split(" "));

        if (args.size() < 2) return;
        String suppliedCommandName = args.get(1);

        for (Class<? extends AbstractCommand> clazz : registeredCommands) {
            Command commandAnnotation = getCommandAnnotation(clazz);
            if (commandAnnotation.name().equals(suppliedCommandName)) {
                AbstractCommand command;

                try {
                    command = createCommandInstance(clazz, destGuild, destChannel, sender, configManager);
                } catch (ReflectionException e) {
                    Logger.error(e);
                    destChannel.sendMessage(getResources(locale).getString("fatalError",
                            getResources(locale).getString("authorDiscordName"), "ReflectionException"));
                    return;
                }

                if (commandAnnotation.argsLength() + 2 < args.size()) {
                    try {
                        destChannel.sendMessage(command.usageMessage());
                    } catch (IllegalAccessException e) {
                        destChannel.sendMessage(getResources(locale).getString("notEnoughArgs"));
                    }
                    return;
                } else {
                    command.run(message);
                }
            }
        }

    }
}
