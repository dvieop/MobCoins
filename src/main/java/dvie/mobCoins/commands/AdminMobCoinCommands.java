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
                                        MobCoins.messageConfig.actions_coinsGiven.execute(
                                                commandSender, "%coins%", String.valueOf(amount), "%player%", player.getName()
                                        );
                                        MobCoins.messageConfig.actions_coinsReceived.execute(
                                                player, "%coins%", String.valueOf(amount), "%player%", commandSender.getName()
                                        );
                                    }, MobCoins.getInstance().getExecutorService())
                                    .exceptionally(throwable -> {
                                        commandSender.sendMessage(ColorUtil.colorize("&cFailed to give coins: " + throwable.getMessage()));
                                        return null;
                                    });
                        }),

                new Command("take")
                        .arguments(new PlayerArgument("player"), new DoubleArgument("amount"))
                        .permission("mobcoins.admin.take")
                        .executes((commandSender, commandContext) -> {
                            Player player = (Player) commandContext.getArg("player");
                            double amount = (double) commandContext.getArg("amount");

                            if (amount <= 0) {
                                commandSender.sendMessage(ColorUtil.colorize("&cAmount must be greater than 0!"));
                                return;
                            }

                            CompletableFuture<Double> balanceFuture;
                            if (MobCoins.mobCoinConfig.useDatabase) {
                                balanceFuture = MobCoins.getInstance().getMobCoinDAO().getCoins(player.getUniqueId());
                            } else {
                                balanceFuture = MobCoins.getInstance().getDatafile().getCoins(player.getUniqueId());
                            }

                            balanceFuture.thenCompose(balance -> {
                                        if (balance == null || balance < amount) {
                                            MobCoins.messageConfig.actions_insufficientFunds.execute(
                                                    commandSender, "%player%", player.getName(), "%coins%", String.valueOf(amount)
                                            );

                                            return CompletableFuture.completedFuture(null);
                                        }
                                        if (MobCoins.mobCoinConfig.useDatabase) {
                                            return MobCoins.getInstance().getMobCoinDAO().addCoins(player.getUniqueId(), -amount);
                                        } else {
                                            return MobCoins.getInstance().getDatafile().addCoins(player.getUniqueId(), -amount);
                                        }
                                    }).thenRunAsync(() -> {
                                        MobCoins.messageConfig.actions_coinsTaken.execute(
                                                commandSender, "%coins%", String.valueOf(amount), "%player%", player.getName()
                                        );
                                        MobCoins.messageConfig.actions_coinsRemoved.execute(
                                                player, "%coins%", String.valueOf(amount), "%player%", commandSender.getName()
                                        );
                                    }, MobCoins.getInstance().getExecutorService())
                                    .exceptionally(throwable -> {
                                        commandSender.sendMessage(ColorUtil.colorize("&cFailed to take coins: " + throwable.getMessage()));
                                        return null;
                                    });
                        }),

                new Command("set")
                        .arguments(new PlayerArgument("player"), new DoubleArgument("amount"))
                        .permission("mobcoins.admin.set")
                        .executes((commandSender, commandContext) -> {
                            Player player = (Player) commandContext.getArg("player");
                            double amount = (double) commandContext.getArg("amount");

                            if (amount < 0) {
                                commandSender.sendMessage(ColorUtil.colorize("&cAmount cannot be negative!"));
                                return;
                            }

                            CompletableFuture<Void> future;
                            if (MobCoins.mobCoinConfig.useDatabase) {
                                future = MobCoins.getInstance().getMobCoinDAO().setCoins(player.getUniqueId(), amount);
                            } else {
                                future = MobCoins.getInstance().getDatafile().setCoins(player.getUniqueId(), amount);
                            }

                            future.thenRunAsync(() -> {
                                        MobCoins.messageConfig.actions_coinsSet.execute(
                                                commandSender, "%coins%", String.valueOf(amount), "%player%", player.getName()
                                        );
                                        MobCoins.messageConfig.actions_coinsUpdated.execute(
                                                player, "%coins%", String.valueOf(amount), "%player%", commandSender.getName()
                                        );
                                    }, MobCoins.getInstance().getExecutorService())
                                    .exceptionally(throwable -> {
                                        commandSender.sendMessage(ColorUtil.colorize("&cFailed to set coins: " + throwable.getMessage()));
                                        return null;
                                    });
                        }),

                new Command("reset")
                        .arguments(new PlayerArgument("player"))
                        .permission("mobcoins.admin.reset")
                        .executes((commandSender, commandContext) -> {
                            Player player = (Player) commandContext.getArg("player");

                            CompletableFuture<Void> future;
                            if (MobCoins.mobCoinConfig.useDatabase) {
                                future = MobCoins.getInstance().getMobCoinDAO().setCoins(player.getUniqueId(), 0.0);
                            } else {
                                future = MobCoins.getInstance().getDatafile().setCoins(player.getUniqueId(), 0.0);
                            }

                            future.thenRunAsync(() -> {
                                        MobCoins.messageConfig.actions_coinsReset.execute(
                                                commandSender, "%player%", player.getName()
                                        );
                                        MobCoins.messageConfig.actions_coinsResetReceived.execute(
                                                player, "%player%", commandSender.getName()
                                        );
                                    }, MobCoins.getInstance().getExecutorService())
                                    .exceptionally(throwable -> {
                                        commandSender.sendMessage(ColorUtil.colorize("&cFailed to reset coins: " + throwable.getMessage()));
                                        return null;
                                    });
                        })
        );
    }
}

