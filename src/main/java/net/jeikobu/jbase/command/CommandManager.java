package net.jeikobu.jbase.command;

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

public enum CommandManager {
    INSTANCE;

    private final List<Class<AbstractCommand>> registeredCommands;

    CommandManager() {
        registeredCommands = new ArrayList<>();
    }

    private static Command getCommandAnnotation(Class<AbstractCommand> clazz) {
        Command c = clazz.getAnnotation(Command.class);
        if (c == null) throw new IllegalArgumentException("This class is not correctly annotated!");
        else return c;
    }

    private static AbstractCommand createCommandInstance(Class<AbstractCommand> clazz, IGuild destGuild, IChannel destChannel, IUser sender) throws ReflectionException {
        try {
            Constructor<AbstractCommand> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance(destGuild, destChannel, sender);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    public final void registerCommand(Class<AbstractCommand> clazz) {
        registeredCommands.add(clazz);
    }

    public final void deregisterCommand(Class<AbstractCommand> clazz) {
        registeredCommands.remove(clazz);
    }

    @EventSubscriber
    public void onMessage(MessageEvent e) {
        IUser sender = e.getAuthor();
        IChannel destChannel = e.getChannel();
        IGuild destServer = e.getGuild();

        String messageString = e.getMessage().getContent();
        IMessage message = e.getMessage();

        if (messageString.length() == 0) return;
        List<String> args = Arrays.asList(messageString.split(" "));
        if (args.size() == 0) return;

        String suppliedCommandName = args.get(0);
        String suppliedPrefix = suppliedCommandName.substring(0, 1);
        suppliedCommandName = suppliedCommandName.substring(1, suppliedCommandName.length());

        for (Class<AbstractCommand> clazz: registeredCommands) {
            Command c = getCommandAnnotation(clazz);
            if (c.name().equals(suppliedCommandName)) {
                if (c.argsLength() < args.size()) {
                    destChannel.sendMessage("Not enough arguments were provided for this command!");
                    return;
                } else {
                    try {
                        createCommandInstance(clazz, destServer, destChannel, sender).run(message);
                    } catch (ReflectionException e1) {
                        e1.printStackTrace();
                        destChannel.sendMessage("I'm sorry! This should *never* happen. Please, contact my author for details!");
                    }
                }
            }
        }
    }
}
