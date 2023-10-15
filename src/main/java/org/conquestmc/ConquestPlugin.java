package org.conquestmc;

import org.bukkit.plugin.java.JavaPlugin;
import org.conquestmc.hooks.Placeholders;
import org.conquestmc.power.PowerCommand;
import org.conquestmc.power.PowerListener;

public final class ConquestPlugin extends JavaPlugin {
    private static ConquestPlugin PLUGIN;

    public ConquestPlugin() {
        PLUGIN = this;

    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PowerListener(), this);
        getPlugin().getCommand("power").setExecutor(new PowerCommand());
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders().register();
        }
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ConquestPlugin getPlugin() {
        return PLUGIN;
    }
}
