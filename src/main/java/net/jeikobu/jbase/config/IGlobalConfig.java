package net.jeikobu.jbase.config;

import java.util.Locale;

public interface IGlobalConfig {
    String defaultCommandPrefix();
    void   globalLocale(Locale locale);
}
