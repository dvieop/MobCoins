package dvie.mobCoins.config;

import dev.splityosis.sysengine.configlib.configuration.Configuration;

public class MobCoinConfig implements Configuration {

    @Section("database")
    @Field @FieldInlineComment("Use a database or a data file") public boolean useDatabase = true;
    @Field public String address = "localhost";
    @Field public int port = 3306;
    @Field public String database = "mobcoins";
    @Field public String username = "username";
    @Field public String password = "password";
    @Field public boolean realtime = true;

}
