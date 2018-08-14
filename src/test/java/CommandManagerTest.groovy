import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.CommandManager
import net.jeikobu.jbase.config.AbstractConfigManager
import spock.lang.Specification
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent

class CommandManagerTest extends Specification {
    AbstractConfigManager configManager = Mock()
    AbstractCommand command = Mock()
    MessageEvent event = Mock()

    def "Registering a command"() {
        configManager.globalConfig = Mock()
        configManager.globalConfig.defaultCommandPrefix = "!"
        configManager.globalConfig.globalLocale = new Locale("pl", "PL")

        event.author = Mock()
        event.message = Mock()
        event.message.author = Mock()
        event.message.content = "!lol limewire"
        event.messageID = 1L

        CommandManager mgr = new CommandManager(configManager)
        mgr.registerCommand(command.class)

        when:
        mgr.onMessage(event)

        then:
        2 * command.run
    }
}
