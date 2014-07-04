package com.michaelelin.WGCompassFlag;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

public class WGCompassFlagListener implements Listener {
    
    private WGCompassFlagPlugin plugin;
    private WorldGuardPlugin worldguard;
    private WorldEditPlugin worldedit;
    
    public WGCompassFlagListener(WGCompassFlagPlugin plugin, WorldGuardPlugin worldguard, WorldEditPlugin worldedit) {
        this.plugin = plugin;
        this.worldguard = worldguard;
        this.worldedit = worldedit;
    }
    
    // WE checks this at a NORMAL priority, so we'll intercept it beforehand.
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getTypeId() == worldedit.getWorldEdit().getConfiguration().navigationWand) {
            plugin.expectTeleport(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/jumpto") || event.getMessage().equalsIgnoreCase("/thru")) {
            plugin.expectTeleport(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (plugin.hasCompassed(event.getPlayer())) {
            ApplicableRegionSet setAtLocation = worldguard.getGlobalRegionManager().get(event.getFrom().getWorld()).getApplicableRegions(event.getFrom());
            ApplicableRegionSet setAtTeleport = worldguard.getGlobalRegionManager().get(event.getTo().getWorld()).getApplicableRegions(event.getTo());
            LocalPlayer player = worldguard.wrapPlayer(event.getPlayer());
            if (!worldguard.getGlobalRegionManager().hasBypass(player, event.getPlayer().getWorld()) && (!setAtLocation.canBuild(player) && !setAtLocation.allows(plugin.COMPASS, player) || !setAtTeleport.canBuild(player) && !setAtTeleport.allows(plugin.COMPASS, player))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to use that in this area.");
            }
        }
    }
}
