package dvie.mobCoins.config;

import dev.splityosis.sysengine.configlib.configuration.Configuration;
import dvie.mobCoins.objects.Mobs;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class MobsConfig implements Configuration {

    @Field
    @FieldComment("List of mobs that can drop coins")
    public Map<String, Mobs> mobs = getDefaultMobs();

    public static Map<String, Mobs> getDefaultMobs() {
        Map<String, Mobs> mobsMap = new HashMap<>();

        EntityType entityType = EntityType.SILVERFISH;
        int minAmount = 1;
        int maxAmount = 5;
        double chance = 1.5;

        mobsMap.put(entityType.name(), new Mobs(entityType, minAmount, maxAmount, chance));

        return mobsMap;
    }
}
