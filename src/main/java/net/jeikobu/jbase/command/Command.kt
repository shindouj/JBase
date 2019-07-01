package net.jeikobu.jbase.command

import net.dv8tion.jda.core.Permission

@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val name: String, val aliases: Array<String> = [],
                         val argsLength: Int = 0,  val permissions: Array<Permission> = [])