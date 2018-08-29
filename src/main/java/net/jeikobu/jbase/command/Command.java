package net.jeikobu.jbase.command;

import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    int argsLength() default 0;
    Permissions[] permissions() default Permissions.SEND_MESSAGES;
}
