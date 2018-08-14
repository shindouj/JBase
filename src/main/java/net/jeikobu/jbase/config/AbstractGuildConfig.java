package net.jeikobu.jbase.config;

import java.util.Locale;
import java.util.Optional;

public abstract class AbstractGuildConfig {
    public abstract Optional<String> getCommandPrefix();
    public abstract void             setCommandPrefix(String prefix);

    public abstract Optional<Locale> getGuildLocale();
    public abstract void             setGuildLocale(Locale locale);
}
