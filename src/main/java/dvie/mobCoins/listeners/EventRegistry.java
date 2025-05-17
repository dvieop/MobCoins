package dvie.mobCoins.listeners;

import dvie.mobCoins.listeners.builtin.MobKillEvent;
import org.bukkit.plugin.Plugin;

public class EventRegistry {
    private EventRegistry() {
        throw new UnsupportedOperationException();
    }

    public static void initialise(final Plugin plugin) {
        final var pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(MobKillEvent.INSTANCE, plugin);
    }
}
