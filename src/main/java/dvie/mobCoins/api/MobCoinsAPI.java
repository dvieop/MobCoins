package dvie.mobCoins.api;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface MobCoinsAPI {

    CompletableFuture<Void> giveCoins(Player player, double amount);

    CompletableFuture<Void> takeCoins(Player player, double amount);

    CompletableFuture<Void> setCoins(Player player, double amount);

    CompletableFuture<Double> getBalance(Player player);

    CompletableFuture<Void> resetCoins(Player player);
}
