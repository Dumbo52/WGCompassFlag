package com.michaelelin.WGCompassFlag;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WGCompassFlagPlugin extends JavaPlugin {
    
    private WGCompassFlagListener listener;
    private Player nextTP;
    private long timestamp;

    public StateFlag COMPASS;

    @Override
    public void onEnable() {
        WGCustomFlagsPlugin wgCustomFlagsPlugin = getPlugin("WGCustomFlags", WGCustomFlagsPlugin.class);
        WorldGuardPlugin worldGuardPlugin = getPlugin("WorldGuard", WorldGuardPlugin.class);
        WorldEditPlugin worldEditPlugin = getPlugin("WorldEdit", WorldEditPlugin.class);

        if (wgCustomFlagsPlugin != null && worldGuardPlugin != null && worldEditPlugin != null) {
            listener = new WGCompassFlagListener(this, worldGuardPlugin, worldEditPlugin);
            getServer().getPluginManager().registerEvents(listener, this);
            
            COMPASS = new StateFlag("compass", true);
            wgCustomFlagsPlugin.addCustomFlag(COMPASS);
        }
    }

    private <T extends Plugin> T getPlugin(String name, Class<T> mainClass) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        if (plugin == null || !mainClass.isInstance(plugin)) {
            getLogger().warning("[" + getName() + "] " + name + " is required for this plugin to work; disabling.");
            getServer().getPluginManager().disablePlugin(this);
        }
        return mainClass.cast(plugin);
    }
    
    public void expectTeleport(Player player) {
        this.nextTP = player;
        this.timestamp = player.getPlayerTime();
    }
    
    public boolean hasCompassed(Player player) {
        return this.nextTP == player && this.timestamp == player.getPlayerTime();
    }

}
