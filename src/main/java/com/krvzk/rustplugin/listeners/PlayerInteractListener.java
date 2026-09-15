package com.krvzk.rustplugin.listeners;

import com.krvzk.rustplugin.RustPlugin;
import com.krvzk.rustplugin.builders.BuilderManager;
import com.krvzk.rustplugin.builders.BuilderSession;
import com.krvzk.rustplugin.structures.Structure;
import com.krvzk.rustplugin.structures.StructureType;
import com.krvzk.rustplugin.utils.BlockValidator;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
        BuilderSession session = builderManager.getSession(playerUUID);

        if (session == null) {
            return;
        }

        // Right-click on blueprint
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getInventory().getItemInMainHand().getItemMeta() != null &&
                    player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§6Plan Budowy")) {
                event.setCancelled(true);
                builderManager.openBuildMenu(player);
                return;
            }
        }

        // Left-click to place structure
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            if (!session.hasStructureSelected()) {
                player.sendMessage("§cSelect a structure first!");
                return;
            }

            event.setCancelled(true);

            // Get target location
            BlockFace facing = player.getTargetBlockFace(5);
            if (facing == null) {
                facing = BlockFace.NORTH;
            }

            // Calculate placement location based on where player is looking
            if (event.getClickedBlock() != null) {
                Structure structure = new Structure(
                        playerUUID,
                        session.getSelectedStructure(),
                        event.getClickedBlock().getLocation().add(0, 1, 0),
                        facing
                );

                builderManager.placeStructure(player, structure);
            }
        }
    }
}
