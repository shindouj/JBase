package net.jeikobu.jbase.config;

import sx.blah.discord.handle.obj.IGuild;

import java.util.Locale;
import java.util.Optional;

public abstract class AbstractConfigManager {
    public abstract IGlobalConfig getGlobalConfig();
    public abstract AbstractGuildConfig getGuildConfig(IGuild guild);

    public Locale getLocale(IGuild guild) {
        return getGuildConfig(guild).getGuildLocale().orElse(getGlobalConfig().getGlobalLocale());
    }
}
