package net.jeikobu.jbase.command;

import net.jeikobu.jbase.config.AbstractConfigManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

class CommandData {
    final IGuild destinationGuild;
    final IChannel destinationChannel;
    final IUser sendingUser;
    final AbstractConfigManager configManager;

    CommandData(IGuild destinationGuild, IChannel destinationChannel, IUser sendingUser, AbstractConfigManager configManager) {
        this.destinationGuild = destinationGuild;
        this.destinationChannel = destinationChannel;
        this.sendingUser = sendingUser;
        this.configManager = configManager;
    }
}
