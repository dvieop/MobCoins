package dvie.mobCoins.api;

import dvie.mobCoins.MobCoins;
import dvie.mobCoins.api.MobCoinsAPI;
import dvie.mobCoins.config.MobCoinConfig;
import dvie.mobCoins.data.Datafile;
import dvie.mobCoins.data.database.MobCoinDAO;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MobCoinsAPIImpl implements MobCoinsAPI {

    private final MobCoins plugin;
    @Getter
    private final MobCoinConfig mobCoinConfig;

    public MobCoinsAPIImpl(MobCoins plugin) {
        this.plugin = plugin;
        this.mobCoinConfig = MobCoins.mobCoinConfig;
    }

    @Override
    public CompletableFuture<Void> giveCoins(Player player, double amount) {
        if (player == null || amount <= 0) {
            return CompletableFuture.completedFuture(null);
        }
        UUID uuid = player.getUniqueId();
        return mobCoinConfig.useDatabase
                ? plugin.getMobCoinDAO().addCoins(uuid, amount)
                : plugin.getDatafile().addCoins(uuid, amount);
    }

    @Override
    public CompletableFuture<Void> takeCoins(Player player, double amount) {
        if (player == null || amount <= 0) {
            return CompletableFuture.completedFuture(null);
        }
        UUID uuid = player.getUniqueId();
        CompletableFuture<Double> balanceFuture = mobCoinConfig.useDatabase
                ? plugin.getMobCoinDAO().getCoins(uuid)
                : plugin.getDatafile().getCoins(uuid);
        return balanceFuture.thenCompose(balance -> {
            if (balance < amount) {
                return CompletableFuture.completedFuture(null);
            }
            return mobCoinConfig.useDatabase
                    ? plugin.getMobCoinDAO().addCoins(uuid, -amount)
                    : plugin.getDatafile().addCoins(uuid, -amount);
        });
    }

    @Override
    public CompletableFuture<Void> setCoins(Player player, double amount) {
        if (player == null || amount < 0) {
            return CompletableFuture.completedFuture(null);
        }
        UUID uuid = player.getUniqueId();
        return mobCoinConfig.useDatabase
                ? plugin.getMobCoinDAO().setCoins(uuid, amount)
                : plugin.getDatafile().setCoins(uuid, amount);
    }

    @Override
    public CompletableFuture<Double> getBalance(Player player) {
        if (player == null) {
            return CompletableFuture.completedFuture(0.0);
        }
        UUID uuid = player.getUniqueId();
        return mobCoinConfig.useDatabase
                ? plugin.getMobCoinDAO().getCoins(uuid)
                : plugin.getDatafile().getCoins(uuid);
    }

    @Override
    public CompletableFuture<Void> resetCoins(Player player) {
        if (player == null) {
            return CompletableFuture.completedFuture(null);
        }
        UUID uuid = player.getUniqueId();
        return mobCoinConfig.useDatabase
                ? plugin.getMobCoinDAO().setCoins(uuid, 0.0)
                : plugin.getDatafile().setCoins(uuid, 0.0);
    }
}
