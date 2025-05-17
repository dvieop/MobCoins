package dvie.mobCoins.config.components;

import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dvie.mobCoins.objects.Mobs;
import org.bukkit.entity.EntityType;

public class MobsMapper implements ConfigMapper<Mobs> {

    @Field @FieldInlineComment("Mob name from org.bukkit.entity.EntityType") public EntityType entityType;
    @Field @FieldInlineComment("Minimum amount of coins gained from the mob") public int minAmount;
    @Field @FieldInlineComment("Maximum amount of coins gained from the mob") public int maxAmount;
    @Field @FieldInlineComment("Percentage change of getting the coins from the mob") public double chance;

    @Override
    public Mobs compile(ConfigManager configManager, ConfigurationSection configurationSection, String s) {
        return new Mobs(entityType, minAmount, maxAmount, chance);
    }

    @Override
    public void decompile(ConfigManager configManager, Mobs mobs, ConfigurationSection configurationSection, String s) {
        entityType = mobs.entityType();
        minAmount = mobs.minAmount();
        maxAmount = mobs.maxAmount();
        chance = mobs.chance();
    }
}
