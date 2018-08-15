package net.jeikobu.jbase.config;

import sx.blah.discord.handle.obj.IGuild;

public abstract class AbstractConfigManager {
    public abstract AbstractGlobalConfig getGlobalConfig();
    public abstract AbstractGuildConfig getGuildConfig(IGuild guild);
}
