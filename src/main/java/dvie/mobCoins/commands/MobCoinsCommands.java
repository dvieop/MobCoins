package dvie.mobCoins.commands;

import dev.splityosis.sysengine.commandlib.arguments.PlayerArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.utils.ColorUtil;
import dvie.mobCoins.MobCoins;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class MobCoinsCommands extends Command {
    public MobCoinsCommands() {
        super("mobcoins", "mc");

        addSubCommands(
                new Command("balance")
                        .optionalArguments(new PlayerArgument("player"))
                        .permission("mobcoins.admin.balance")
                        .executes((commandSender, commandContext) -> {
                            Player player = (Player) commandContext.getArgOrDefault("player", commandSender);

                            if (player == null) {
                                commandSender.sendMessage(ColorUtil.colorize("&cYou must specify a player!"));
                                return;
                            }

                            if (player == null) {
                                commandSender.sendMessage(ColorUtil.colorize("&cPlayer " + player.displayName() + " is not online!"));
                                return;
                            }

                            CompletableFuture<Double> future;
                            if (MobCoins.mobCoinConfig.useDatabase) {
                                future = MobCoins.getInstance().getMobCoinDAO().getCoins(player.getUniqueId());
                            } else {
                                future = MobCoins.getInstance().getDatafile().getCoins(player.getUniqueId());
                            }

                            future.thenAcceptAsync(amount -> {
                                        if (commandSender.getName().equals(player.getName())) {
                                            MobCoins.messageConfig.actions_balanceSelf.execute(
                                                    commandSender, "%coins%", String.valueOf(amount)
                                            );
                                        } else {
                                            MobCoins.messageConfig.actions_balanceOther.execute(
                                                    commandSender, "%player%", player.getName(), "%coins%", String.valueOf(amount)
                                            );
                                        }
                                    }, MobCoins.getInstance().getExecutorService())
                                    .exceptionally(throwable -> {
                                        commandSender.sendMessage(ColorUtil.colorize("&cFailed to check balance: " + throwable.getMessage()));
                                        return null;
                                    });
                        }));
    }
}
