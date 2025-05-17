package dvie.mobCoins.data.database;

import dvie.mobCoins.MobCoins;
import dvie.mobCoins.objects.PlayerCoins;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MobCoinDAO {

    private final DatabaseManager db;

    @SneakyThrows
    public CompletableFuture<Void> createTableIfNotExists() {
        return CompletableFuture.runAsync(() -> {
                    String sql = "CREATE TABLE IF NOT EXISTS mobcoins (" +
                            "uuid VARCHAR(36) PRIMARY KEY, " +
                            "coins DOUBLE NOT NULL DEFAULT 0" +
                            ")";
                    try (Connection conn = db.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to create mobcoins table", e);
                    }
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Database operation timed out while creating table: " + throwable.getMessage());
                    return null;
                });
    }

    public CompletableFuture<Double> getCoins(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
                    String sql = "SELECT coins FROM mobcoins WHERE uuid = ?";
                    try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, uuid.toString());
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) return rs.getDouble("coins");
                        }
                    } catch (SQLException e) {
                        MobCoins.getInstance().getLogger().severe("Failed to retrieve coins for UUID: " + uuid + ": " + e.getMessage());
                    }
                    return 0.0;
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Database operation timed out for UUID: " + uuid + ": " + throwable.getMessage());
                    return 0.0;
                });
    }

    public CompletableFuture<Void> setCoins(UUID uuid, double amount) {
        return CompletableFuture.runAsync(() -> {
                    String sql = "INSERT INTO mobcoins (uuid, coins) VALUES (?, ?) ON DUPLICATE KEY UPDATE coins = ?";
                    try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, uuid.toString());
                        stmt.setDouble(2, amount);
                        stmt.setDouble(3, amount);
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        MobCoins.getInstance().getLogger().severe("Failed to set coins for UUID: " + uuid + ": " + e.getMessage());
                    }
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Database operation timed out for UUID: " + uuid + ": " + throwable.getMessage());
                    return null;
                });
    }

    public CompletableFuture<Void> addCoins(UUID uuid, double amount) {
        return getCoins(uuid).thenCompose(current -> setCoins(uuid, current + amount));
    }

    public CompletableFuture<List<PlayerCoins>> getTopPlayers(int limit) {
        return CompletableFuture.supplyAsync(() -> {
                    List<PlayerCoins> topPlayers = new ArrayList<>();
                    String sql = "SELECT uuid, coins FROM mobcoins ORDER BY coins DESC LIMIT ?";
                    try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, limit);
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                topPlayers.add(new PlayerCoins(UUID.fromString(rs.getString("uuid")), rs.getDouble("coins")));
                            }
                        }
                    } catch (SQLException e) {
                        MobCoins.getInstance().getLogger().severe("Failed to retrieve top players: " + e.getMessage());
                    }
                    return topPlayers;
                }, MobCoins.getInstance().getExecutorService())
                .orTimeout(5, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    MobCoins.getInstance().getLogger().severe("Database operation timed out for top players: " + throwable.getMessage());
                    return new ArrayList<>();
                });
    }
}
