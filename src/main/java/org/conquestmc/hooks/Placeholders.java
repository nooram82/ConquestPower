package org.conquestmc.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.conquestmc.power.PowerController;
import org.jetbrains.annotations.NotNull;

import static org.conquestmc.util.MessageUtils.powerToColor;

public class Placeholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "conquest";
    }

    @Override
    public @NotNull String getAuthor() {
        return "maroon28";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        String[] args = params.split("_");
        double power = PowerController.getPower(player.getPlayer());
        if (args[0].contains("power")) {
            return String.valueOf(power);
        }
        // %conquest_power-color% = Returns the color of your current level
        if (args[0].contains("color")) {
            return powerToColor(power);
        }

        return null; // Placeholder not recognized
    }

}
