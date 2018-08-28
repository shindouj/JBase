package net.jeikobu.jbase;

import org.pmw.tinylog.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class Localized {
    public String getLocalized(String key, Locale locale) {
        Logger.error(locale.toString());
        ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getSimpleName(), locale);
        return bundle.getString(key);
    }
}
