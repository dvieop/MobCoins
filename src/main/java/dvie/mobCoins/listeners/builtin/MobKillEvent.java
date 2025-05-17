package dvie.mobCoins.listeners.builtin;

import dvie.mobCoins.MobCoins;
import dvie.mobCoins.config.MobsConfig;
import dvie.mobCoins.data.Datafile;
import dvie.mobCoins.data.database.MobCoinDAO;
import dvie.mobCoins.objects.Mobs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MobKillEvent implements Listener {
    public static final Listener INSTANCE = new MobKillEvent();

    private MobKillEvent() {}

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        Player player = e.getEntity().getKiller();
        if (player == null) return;

        MobsConfig mobsConfig = MobCoins.mobsConfig;
        Mobs mob = mobsConfig.mobs.get(e.getEntityType().name());
        if (mob == null) return;

        double chance = mob.chance();
        if (ThreadLocalRandom.current().nextDouble(100) > chance) return;

        int minAmount = mob.minAmount();
        int maxAmount = mob.maxAmount();
        double coins = ThreadLocalRandom.current().nextInt(minAmount, maxAmount + 1);

        CompletableFuture<Void> future;
        if (MobCoins.mobCoinConfig.useDatabase) {
            MobCoinDAO dao = MobCoins.getInstance().getMobCoinDAO();
            future = dao.addCoins(player.getUniqueId(), coins);
        } else {
            Datafile datafile = MobCoins.getInstance().getDatafile();
            future = datafile.addCoins(player.getUniqueId(), coins);
        }

        future.thenRunAsync(() -> {
                    MobCoins.messageConfig.actions_coinDropped.execute(
                            player, "%coins%", String.valueOf(coins), "%mob%", e.getEntityType().name()
                    );
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe(
                            "Failed to add coins for player " + player.getName() + ": " + throwable.getMessage());
                    player.sendMessage("§cError: Could not add MobCoins. Please contact an admin.");
                    return null;
                });
    }
}
