package net.jeikobu.jbase.config;

import sx.blah.discord.handle.obj.IGuild;

import java.util.Locale;
import java.util.Optional;

public abstract class AbstractGuildConfig {
    public AbstractGuildConfig(IGuild guild) {

    }

    public abstract Optional<String> getCommandPrefix();
    public abstract void             setCommandPrefix(String prefix);

    public abstract Optional<Locale> getGuildLocale();
    public abstract void             setGuildLocale(Locale locale);

    public abstract <T> Optional<T>  getValue(String key, String defaultValue, Class<T> valueType);
    public abstract void             setValue(String key, String value);

    public <T> Optional<T> getValue(String key, Class<T> valueType) {
        return getValue(key, null, valueType);
    }
}
