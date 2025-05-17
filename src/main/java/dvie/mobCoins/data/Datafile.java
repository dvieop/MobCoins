package dvie.mobCoins.data;

import dvie.mobCoins.MobCoins;
import dvie.mobCoins.objects.PlayerCoins;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Datafile {

    private final File file;
    private final ConcurrentHashMap<UUID, Double> coinsCache;
    private YamlConfiguration config;

    public Datafile(File file) {
        this.file = file;
        this.coinsCache = new ConcurrentHashMap<>();
    }

    public CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
                    try {
                        if (!file.exists()) {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        }
                        config = YamlConfiguration.loadConfiguration(file);
                        coinsCache.clear();
                        if (config.contains("players")) {
                            for (String uuidStr : config.getConfigurationSection("players").getKeys(false)) {
                                try {
                                    UUID uuid = UUID.fromString(uuidStr);
                                    double coins = config.getDouble("players." + uuidStr);
                                    coinsCache.put(uuid, coins);
                                } catch (IllegalArgumentException e) {
                                    MobCoins.getInstance().getLogger().warning("Invalid UUID in data.yml: " + uuidStr);
                                }
                            }
                        }
                    } catch (IOException e) {
                        MobCoins.getInstance().getLogger().severe("Failed to load data.yml: " + e.getMessage());
                    }
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Failed to load data.yml due to timeout: " + throwable.getMessage());
                    return null;
                });
    }

    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
                    try {
                        config.set("players", null); // Clear existing data
                        for (var entry : coinsCache.entrySet()) {
                            config.set("players." + entry.getKey().toString(), entry.getValue());
                        }
                        config.save(file);
                    } catch (IOException e) {
                        MobCoins.getInstance().getLogger().severe("Failed to save data.yml: " + e.getMessage());
                    }
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Failed to save data.yml due to timeout: " + throwable.getMessage());
                    return null;
                });
    }

    public CompletableFuture<Double> getCoins(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> coinsCache.getOrDefault(uuid, 0.0),
                        MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Failed to get coins for UUID: " + uuid + ": " + throwable.getMessage());
                    return 0.0;
                });
    }

    public CompletableFuture<Void> setCoins(UUID uuid, double amount) {
        return CompletableFuture.runAsync(() -> {
                    coinsCache.put(uuid, amount);
                    if (MobCoins.mobCoinConfig.realtime) {
                        try {
                            config.set("players." + uuid.toString(), amount);
                            config.save(file);
                        } catch (IOException e) {
                            MobCoins.getInstance().getLogger().severe("Failed to save coins for UUID: " + uuid + ": " + e.getMessage());
                        }
                    }
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Failed to set coins for UUID: " + uuid + ": " + throwable.getMessage());
                    return null;
                });
    }

    public CompletableFuture<Void> addCoins(UUID uuid, double amount) {
        return getCoins(uuid).thenCompose(current -> setCoins(uuid, current + amount));
    }

    public CompletableFuture<List<PlayerCoins>> getTopPlayers(int limit) {
        return CompletableFuture.supplyAsync(() -> {
                    List<PlayerCoins> topPlayers = new ArrayList<>();
                    coinsCache.entrySet().stream()
                            .map(e -> new PlayerCoins(e.getKey(), e.getValue()))
                            .sorted(Comparator.comparingDouble(PlayerCoins::coins).reversed())
                            .limit(limit)
                            .forEach(topPlayers::add);
                    return topPlayers;
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Failed to retrieve top players: " + throwable.getMessage());
                    return new ArrayList<>();
                });
    }
}
