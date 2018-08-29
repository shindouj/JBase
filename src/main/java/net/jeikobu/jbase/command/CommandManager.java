package net.jeikobu.jbase.command;

import net.jeikobu.jbase.Localized;
import net.jeikobu.jbase.config.AbstractConfigManager;
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
import java.util.*;

public class CommandManager extends Localized {
    private final AbstractConfigManager configManager;
    private final List<Class<? extends AbstractCommand>> registeredCommands;

    public CommandManager(AbstractConfigManager configManager) {
        this.configManager = configManager;
        registeredCommands = new ArrayList<>();
    }

    private static Command getCommandAnnotation(Class<? extends AbstractCommand> clazz) {
        Command c = clazz.getAnnotation(Command.class);
        if (c == null) {
            throw new IllegalArgumentException("This class is not correctly annotated!");
        } else {
            return c;
        }
    }

    private static AbstractCommand createCommandInstance(Class<? extends AbstractCommand> clazz, IGuild destGuild,
                                                         IChannel destChannel, IUser sender,
                                                         AbstractConfigManager configManager,
                                                         List<String> args) throws ReflectionException {
        try {
            Constructor<? extends AbstractCommand> constructor = clazz.getDeclaredConstructor(CommandData.class);

            return constructor.newInstance(new CommandData(destGuild, destChannel, sender, configManager, args));
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

    public final void registerAll(Collection<Class<? extends AbstractCommand>> classCollection) {
        registeredCommands.addAll(classCollection);
    }

    public final void deregisterAll(Collection<Class<? extends AbstractCommand>> classCollection) {
        registeredCommands.removeAll(classCollection);
    }

    @EventSubscriber
    public void onMessage(MessageEvent event) {
        IUser sender = event.getAuthor();
        IChannel destChannel = event.getChannel();
        IGuild destGuild = event.getGuild();

        String commandPrefix = configManager.getCommandPrefix(destGuild);
        Locale locale = configManager.getLocale(destGuild);

        String messageString = event.getMessage().getContent();
        IMessage message = event.getMessage();

        if (messageString.length() == 0 || !messageString.startsWith(commandPrefix)) {
            return;
        }

        List<String> args = Arrays.asList(messageString.split(" "));

        if (args.size() < 2) {
            return;
        }
        String suppliedCommandName = args.get(1);

        for (Class<? extends AbstractCommand> clazz : registeredCommands) {
            Command commandAnnotation = getCommandAnnotation(clazz);
            if (commandAnnotation.name().equals(suppliedCommandName)) {

                if (!sender.getPermissionsForGuild(destGuild)
                           .containsAll(Arrays.asList(commandAnnotation.permissions()))) {
                    destChannel.sendMessage(getLocalized(locale, "insufficientPermissions"));
                    return;
                }

                AbstractCommand command;

                try {
                    command = createCommandInstance(clazz, destGuild, destChannel, sender, configManager,
                                                    args.subList(2, args.size()));
                } catch (ReflectionException e) {
                    Logger.error(e);
                    destChannel.sendMessage(getLocalized(locale, "fatalError"));
                    return;
                }

                if (commandAnnotation.argsLength() + 2 > args.size()) {
                    try {
                        destChannel.sendMessage(command.usageMessage());
                    } catch (IllegalAccessException e) {
                        destChannel.sendMessage(getLocalized(locale, "notEnoughArgs"));
                    }
                    return;
                } else {
                    command.run(message);
                }
            }
        }

    }
}
