package org.conquestmc.power;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.api.JobsLevelUpEvent;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.conquestmc.ConquestPlugin;
import org.conquestmc.util.MessageUtils;

import static org.conquestmc.power.PowerController.*;

public class PowerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            setPower(player, getStartingPower());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        removePower(event.getPlayer(), getPowerLoss());
        notifyPowerChange(event.getPlayer(), -1.0 * getPowerLoss());
    }

    @EventHandler
    public void onEntitySpawn(SpawnerSpawnEvent event) {
        event.getEntity().setMetadata("spawner-mob",
                new FixedMetadataValue(ConquestPlugin.getPlugin(), "spawner-mob"));
    }
    @EventHandler
    public void onJobLevelUp(JobsLevelUpEvent event) {
        Player player = event.getPlayer().getPlayer();
        String name = event.getJob().getName();
        int level = getJobsLevel(player, name);
        double amount = calculatePowerGain(level);
        if (addPower(player, amount))
            notifyPowerChange(player, amount);
    }

    private double calculatePowerGain(int level) {
        double basePower = 1.5;  // Initial power gain
        double powerMultiplier = 1.2;  // Multiplier for each level increase

        return Math.round(basePower * Math.pow(powerMultiplier, level - 1));
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        if (isSpawnerMob(event.getEntity())) {
            return;
        }

        Player killer = event.getEntity().getKiller();
        EntityType type = event.getEntityType();
        double powerGain = getPowerGain(type);
        if (addPower(killer, powerGain))
            notifyPowerChange(killer, powerGain);
    }

    private boolean isSpawnerMob(Entity entity) {
        return entity.hasMetadata("spawner-mob");
    }
    private int getJobsLevel(Player player, String jobName) {
        JobsPlayer jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player.getPlayer());
        if (jobsPlayer == null) {
            return 0;
        }
        for (JobProgression job : jobsPlayer.getJobProgression()) {
            if (job.getJob().getName().equals(jobName)) {
                return job.getLevel();
            }
        }
        return 0;
    }

    private void notifyPowerChange(Player player, double amount) {
        String messageName = amount < 0 ? "power-loss" : "power-gain";
        player.sendActionBar(MessageUtils.getMessage(messageName, Placeholder.component("power", Component.text(Math.abs(amount)))));
    }

}
