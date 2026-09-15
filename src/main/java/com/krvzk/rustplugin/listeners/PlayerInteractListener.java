package com.krvzk.rustplugin.listeners;

import com.krvzk.rustplugin.RustPlugin;
import com.krvzk.rustplugin.builders.BuilderManager;
import com.krvzk.rustplugin.structures.Structure;
import com.krvzk.rustplugin.structures.StructureType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private final RustPlugin plugin;
    private final BuilderManager builderManager;

    public PlayerInteractListener(RustPlugin plugin, BuilderManager builderManager) {
        this.plugin = plugin;
        this.builderManager = builderManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if player is holding the blueprint
        if (player.getInventory().getItemInMainHand().getItemMeta() == null ||
                !player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("\u00a76Plan Budowy")) {
            return;
        }

        // Right-click on blueprint to open menu
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            builderManager.openBuildMenu(player);
            return;
        }

        // Left-click to place structure
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);

            if (!builderManager.hasStructureSelected(playerUUID)) {
                player.sendMessage("\u00a7cSelect a structure first!");
                return;
            }

            // Get direction player is looking (center of crosshair)
            Vector direction = player.getLocation().getDirection().normalize();
            Location placeLocation = player.getEyeLocation().add(direction.multiply(7));
            
            // Snap to grid for alignment
            placeLocation.setX(Math.floor(placeLocation.getX()));
            placeLocation.setY(Math.floor(placeLocation.getY()));
            placeLocation.setZ(Math.floor(placeLocation.getZ()));

            StructureType selectedStructure = builderManager.getSelectedStructure(playerUUID);
            float yaw = player.getLocation().getYaw();

            Structure structure = new Structure(
                    playerUUID,
                    selectedStructure,
                    placeLocation,
                    null  // We'll handle rotation via yaw
            );

            // Store yaw for rotation handling
            builderManager.placeStructure(player, structure, yaw);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if player is no longer holding the blueprint
        if (player.getInventory().getItem(event.getNewSlot()) == null ||
                player.getInventory().getItem(event.getNewSlot()).getItemMeta() == null ||
                !player.getInventory().getItem(event.getNewSlot()).getItemMeta().getDisplayName().equals("\u00a76Plan Budowy")) {
            
            // Clear preview and reset structure selection
            plugin.getPreviewRenderer().clearAllPreviews(player);
            builderManager.deselectStructure(playerUUID);
        }
    }
}
