package com.krvzk.rustplugin.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.krvzk.rustplugin.RustPlugin;
import com.krvzk.rustplugin.builders.BuilderManager;
import com.krvzk.rustplugin.structures.StructureType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PreviewRenderer {

    private final RustPlugin plugin;
    private final BuilderManager builderManager;
    private final ProtocolManager protocolManager;
    private final Map<UUID, Set<Location>> playerPreviewBlocks = new HashMap<>();
    private final Map<UUID, Location> playerLastPreviewLocation = new HashMap<>();
    private final int MIN_DISTANCE = 5;
    private final int MAX_DISTANCE = 10;

    public PreviewRenderer(RustPlugin plugin, BuilderManager builderManager) {
        this.plugin = plugin;
        this.builderManager = builderManager;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void updatePreview(Player player, StructureType structureType) {
        UUID playerUUID = player.getUniqueId();

        // Get direction player is looking (center of crosshair)
        Vector direction = player.getLocation().getDirection().normalize();
        Location previewLocation = player.getEyeLocation().add(direction.multiply(7));
        
        // Snap to grid for alignment (round to nearest block)
        previewLocation.setX(Math.floor(previewLocation.getX()));
        previewLocation.setY(Math.floor(previewLocation.getY()));
        previewLocation.setZ(Math.floor(previewLocation.getZ()));

        Location lastLocation = playerLastPreviewLocation.get(playerUUID);

        // Only update if location changed
        if (lastLocation != null && lastLocation.equals(previewLocation)) {
            return;
        }

        // Clear previous preview
        clearPreview(player);

        // Send block change packets for preview
        sendBlockPreview(player, previewLocation, structureType, player.getLocation().getYaw());
        playerLastPreviewLocation.put(playerUUID, previewLocation);
    }

    private void sendBlockPreview(Player player, Location baseLocation, StructureType type, float yaw) {
        UUID playerUUID = player.getUniqueId();
        Set<Location> previewLocations = new HashSet<>();

        // Normalize yaw to 0-360
        float normalizedYaw = ((yaw + 180) % 360);
        if (normalizedYaw < 0) normalizedYaw += 360;

        if (type == StructureType.SCIANA) {
            // Draw wall based on player direction
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    Location blockLocation = baseLocation.clone();

                    // Determine direction and rotate accordingly
                    if (normalizedYaw >= 315 || normalizedYaw < 45) {
                        // Facing South (positive Z)
                        blockLocation.add(x, y, 0);
                    } else if (normalizedYaw >= 45 && normalizedYaw < 135) {
                        // Facing West (negative X)
                        blockLocation.add(0, y, x);
                    } else if (normalizedYaw >= 135 && normalizedYaw < 225) {
                        // Facing North (negative Z)
                        blockLocation.add(4 - x, y, 4);
                    } else {
                        // Facing East (positive X)
                        blockLocation.add(4, y, 4 - x);
                    }

                    Block block = blockLocation.getBlock();
                    boolean canPlace = block.getType() == Material.AIR;
                    Material previewMaterial = canPlace ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS;
                    sendBlockChangePacket(player, blockLocation, previewMaterial);
                    previewLocations.add(blockLocation);
                }
            }
        } else {
            // For fundament and sufit (horizontal structures)
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < type.getHeight(); y++) {
                    for (int z = 0; z < 5; z++) {
                        Location blockLocation = baseLocation.clone().add(x, y, z);
                        Block block = blockLocation.getBlock();

                        // Only show preview for air blocks, skip existing blocks
                        if (block.getType() == Material.AIR) {
                            Material previewMaterial = Material.LIME_STAINED_GLASS;
                            sendBlockChangePacket(player, blockLocation, previewMaterial);
                            previewLocations.add(blockLocation);
                        }
                    }
                }
            }
        }

        playerPreviewBlocks.put(playerUUID, previewLocations);
    }

    private void clearPreview(Player player) {
        UUID playerUUID = player.getUniqueId();
        Set<Location> previewLocations = playerPreviewBlocks.get(playerUUID);

        if (previewLocations == null) {
            return;
        }

        // Restore original block data for all preview blocks
        for (Location location : previewLocations) {
            Block block = location.getBlock();
            sendBlockChangePacket(player, location, block.getType());
        }

        playerPreviewBlocks.remove(playerUUID);
    }

    private void sendBlockChangePacket(Player player, Location location, Material material) {
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            packet.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            packet.getBlockData().write(0, WrappedBlockData.createData(material));
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to send block preview packet: " + e.getMessage());
        }
    }

    public void clearAllPreviews(Player player) {
        clearPreview(player);
        playerLastPreviewLocation.remove(player.getUniqueId());
    }
}
