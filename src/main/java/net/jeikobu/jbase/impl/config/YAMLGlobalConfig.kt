package net.jeikobu.jbase.impl.config

import net.jeikobu.jbase.config.IGlobalConfig
import org.cfg4j.provider.ConfigurationProvider
import org.cfg4j.provider.ConfigurationProviderBuilder
import org.cfg4j.source.context.environment.ImmutableEnvironment
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider
import org.cfg4j.source.files.FilesConfigurationSource
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

class YAMLGlobalConfig : IGlobalConfig {
    override val defaultCommandPrefix: String
        get() = config.getProperty("defaultCommandPrefix", String::class.java)
    override val token: String
        get() = config.getProperty("discordToken", String::class.java)
    override val useDefaultCommands: Boolean
        get() = config.getProperty("useDefaultCommands", Boolean::class.java) ?: true
    override val globalLocale: Locale
        get() = config.getProperty("locale", Locale::class.java)

    private val config: ConfigurationProvider

    init {
        val configFilesProvider = ConfigFilesProvider {mutableListOf(Paths.get("config/global.config.yml"))}
        val configSource = FilesConfigurationSource(configFilesProvider)
        val reloadStrategy = PeriodicalReloadStrategy(5, TimeUnit.SECONDS)
        val environment = ImmutableEnvironment("./")

        config = ConfigurationProviderBuilder()
                .withConfigurationSource(configSource)
                .withReloadStrategy(reloadStrategy)
                .withEnvironment(environment)
                .build()
    }

    override fun <T : Any?> getValue(key: String, valueType: Class<T>): T {
        return config.getProperty(key, valueType)
    }
}