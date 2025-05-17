package dvie.mobCoins;

import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dvie.mobCoins.config.MessageConfig;
import dvie.mobCoins.config.MobCoinConfig;
import dvie.mobCoins.config.MobsConfig;
import dvie.mobCoins.config.components.MobsMapper;
import dvie.mobCoins.data.Datafile;
import dvie.mobCoins.data.database.DatabaseManager;
import dvie.mobCoins.data.database.MobCoinDAO;
import dvie.mobCoins.listeners.EventRegistry;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class MobCoins extends JavaPlugin {

    @Getter private ConfigManager configManager;
    @Getter private static MobCoins instance;
    @Getter private DatabaseManager databaseManager;
    @Getter private ExecutorService executorService;
    @Getter private MobCoinDAO mobCoinDAO;
    @Getter private Datafile datafile;
    public static MobCoinConfig mobCoinConfig;
    public static MessageConfig messageConfig;
    public static MobsConfig mobsConfig;

    @Override
    public void onEnable() {
        instance = this;
        configManager = ConfigLib.createConfigManager(this);

        this.executorService = Executors.newFixedThreadPool(4, r -> {
            Thread thread = new Thread(r, "MobCoins-DB-Thread");
            thread.setDaemon(true);
            return thread;
        });

        initialiseConfigs();
        EventRegistry.initialise(this);

        if (mobCoinConfig.useDatabase) {
            this.databaseManager = new DatabaseManager(mobCoinConfig);
            this.databaseManager.connect();
            this.mobCoinDAO = new MobCoinDAO(databaseManager);
            this.mobCoinDAO.createTableIfNotExists();
        } else {
            this.datafile = new Datafile(new File(getDataFolder(), "data.yml"));
            this.datafile.load();
        }
    }

    @Override
    public void onDisable() {
        if (mobCoinConfig.useDatabase) {
            databaseManager.disconnect();
        } else {
            datafile.save();
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            getLogger().severe("Failed to shutdown database executor: " + e.getMessage());
        }
    }

    @SneakyThrows
    public void reloadPlugin() {
        configManager.reloadAll();
    }

    @SneakyThrows
    public void initialiseConfigs() {
        configManager.getMapperRegistry().registerMappers(
                new MobsMapper()
        );
        mobCoinConfig = new MobCoinConfig();
        messageConfig = new MessageConfig();
        mobsConfig = new MobsConfig();
        configManager.registerConfig(mobCoinConfig, new File(getDataFolder(), "mobcoins.yml"));
        configManager.registerConfig(messageConfig, new File(getDataFolder(), "messages.yml"));
        configManager.registerConfig(mobsConfig, new File(getDataFolder(), "mobs.yml"));
    }
}