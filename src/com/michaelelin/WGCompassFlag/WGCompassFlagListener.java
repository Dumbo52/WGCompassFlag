package com.michaelelin.WGCompassFlag;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
            Player player = event.getPlayer();
            if (!worldguard.getGlobalRegionManager().hasBypass(player, player.getWorld())) {
                if ((event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) && event.getPlayer().hasPermission("worldedit.navigation.jumpto.tool")
                        || (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getPlayer().hasPermission("worldedit.navigation.thru.tool")) {
                    ApplicableRegionSet setAtPlayer = worldguard.getGlobalRegionManager().get(player.getWorld()).getApplicableRegions(player.getLocation());
                    LocalPlayer localPlayer = worldguard.wrapPlayer(player);
                    if (setAtPlayer.canBuild(localPlayer) || setAtPlayer.allows(plugin.COMPASS, localPlayer)) {
                        WorldVector vec = worldedit.wrapPlayer(player).getSolidBlockTrace(worldedit.getWorldEdit().getConfiguration().navigationWandMaxDistance);
                        if (vec != null) {
                            ApplicableRegionSet setAtClicked = worldguard.getGlobalRegionManager().get(player.getWorld()).getApplicableRegions(vec);
                            if (!setAtClicked.canBuild(localPlayer) && !setAtClicked.allows(plugin.COMPASS, localPlayer)) {
                                cancelEvent(event);
                            }
                        }
                    }
                    else {
                        cancelEvent(event);
                    }
                }
            }
        }
    }
    
    private void cancelEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.setUseItemInHand(Result.DENY);
        event.getPlayer().sendMessage(ChatColor.DARK_RED + "You don't have permission to use that in this area.");
    }
}
