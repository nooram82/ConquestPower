package org.conquestmc.power;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.conquestmc.ConquestPlugin;

public class PowerController {
    private static final NamespacedKey POWER_KEY = new NamespacedKey(ConquestPlugin.getPlugin(), "power");

    public static void removePower(Player player, double amount) {
        setPower(player, getPower(player) - amount);
    }

    public static boolean addPower(Player player, double amount) {
        if (getPower(player) + amount > 5000) {
            setPower(player, 5000);
            return false;
        }
        setPower(player, getPower(player) + amount);
        return true;
    }

    public static void setPower(Player player, double amount) {
        var container = player.getPersistentDataContainer();
        container.set(POWER_KEY, PersistentDataType.DOUBLE, Math.max(0, amount));
    }

    public static double getPower(Player player) {
        var container = player.getPersistentDataContainer();
        if (container.has(POWER_KEY)) {
            return container.get(POWER_KEY, PersistentDataType.DOUBLE);
        } else {
            setPower(player, 1);
            return 1;
        }
    }

    public static double getPowerGain(EntityType type) {
        var config = ConquestPlugin.getPlugin().getConfig();
        var gainSection = config.getConfigurationSection("power-gain-amounts");
        for (var entry: gainSection.getKeys(false)) {
            if (entry.equals(type.toString()))
                return gainSection.getDouble(entry);
        }
        return gainSection.getDouble("DEFAULT");
    }

    public static double getPowerLoss() {
        return ConquestPlugin.getPlugin().getConfig().getDouble("power-loss-on-death");
    }

    public static double getStartingPower() {
        return ConquestPlugin.getPlugin().getConfig().getDouble("starting-power");
    }
}
