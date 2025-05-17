package dvie.mobCoins.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dvie.mobCoins.config.MobCoinConfig;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    @Getter private final MobCoinConfig config;

    @Getter private HikariDataSource dataSource;

    public DatabaseManager(MobCoinConfig config) {
        this.config = config;
    }

    public void connect() {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true",
                config.address, config.port, config.database);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.username);
        hikariConfig.setPassword(config.password);
        hikariConfig.setMaximumPoolSize(10);

        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void disconnect() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
