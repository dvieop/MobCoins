package dvie.mobCoins.objects;

import org.bukkit.entity.EntityType;

public record Mobs(EntityType entityType, int minAmount, int maxAmount, double chance) {}
