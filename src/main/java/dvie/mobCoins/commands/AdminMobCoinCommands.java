package dvie.mobCoins.commands;

import dev.splityosis.sysengine.commandlib.arguments.DoubleArgument;
import dev.splityosis.sysengine.commandlib.arguments.PlayerArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.utils.ColorUtil;
import dvie.mobCoins.MobCoins;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class AdminMobCoinCommands extends Command {
    public AdminMobCoinCommands() {
        super("adminmobcoins", "amobcoins");

        addSubCommands(
                new Command("reload")
                .permission("mobcoins.admin.reload")
                .executes((commandSender, commandContext) -> {
                    MobCoins.getInstance().reloadPlugin();
                    commandSender.sendMessage(ColorUtil.colorize("&aReloaded all configs!"));
                }),
                new Command("give")
                        .arguments(new PlayerArgument("player"), new DoubleArgument("amount"))
                        .permission("mobcoins.admin.give")
                        .executes((commandSender, commandContext) -> {
                            Player player = (Player) commandContext.getArg("player");
                            double amount = (double) commandContext.getArg("amount");

                            if (amount <= 0) {
                                commandSender.sendMessage(ColorUtil.colorize("&cAmount must be greater than 0!"));
                                return;
                            }

                            CompletableFuture<Void> future;
                            if (MobCoins.mobCoinConfig.useDatabase) {
                                future = MobCoins.getInstance().getMobCoinDAO().addCoins(player.getUniqueId(), amount);
                            } else {
                                future = MobCoins.getInstance().getDatafile().addCoins(player.getUniqueId(), amount);
                            }

                            future.thenRunAsync(() -> {
                                commandSender.sendMessage(ColorUtil.colorize("&aGave " + amount + " coins to " + player.getName()));
                                player.sendMessage(ColorUtil.colorize("&aYou have been given " + amount + " coins!"));
                            }, MobCoins.getInstance().getExecutorService())
                                    .exceptionally(throwable -> {
                                        commandSender.sendMessage(ColorUtil.colorize("&cFailed to give coins: " + throwable.getMessage()));
                                        return null;
                                    });
                        }));
    }
}
