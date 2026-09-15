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
import org.bukkit.util.RayTraceResult;
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

        // Ray trace to find where player is looking
        RayTraceResult rayTrace = player.rayTraceBlocks(MAX_DISTANCE);

        if (rayTrace == null || rayTrace.getHitBlock() == null) {
            clearPreview(player);
            return;
        }

        Block targetBlock = rayTrace.getHitBlock();
        double distance = player.getLocation().distance(targetBlock.getLocation());

        // Check if target is within range
        if (distance < MIN_DISTANCE || distance > MAX_DISTANCE) {
            clearPreview(player);
            return;
        }

        Location previewLocation = targetBlock.getLocation().add(0, 1, 0);
        Location lastLocation = playerLastPreviewLocation.get(playerUUID);

        // Only update if location changed
        if (lastLocation != null && lastLocation.equals(previewLocation)) {
            return;
        }

        // Clear previous preview
        clearPreview(player);

        // Send block change packets for preview
        sendBlockPreview(player, previewLocation, structureType, rayTrace.getHitBlockFace());
        playerLastPreviewLocation.put(playerUUID, previewLocation);
    }

    private void sendBlockPreview(Player player, Location baseLocation, StructureType type, org.bukkit.block.BlockFace face) {
        UUID playerUUID = player.getUniqueId();
        Set<Location> previewLocations = new HashSet<>();
        BlockValidator validator = new BlockValidator(
                plugin.getStructureManager(),
                plugin.getDatabaseManager()
        );

        if (type == StructureType.SCIANA) {
            // Draw wall in the direction player is looking
            for (int x = 0; x < type.getWidth(); x++) {
                for (int y = 0; y < type.getHeight(); y++) {
                    Location blockLocation = baseLocation.clone().add(x, y, 0);
                    Block block = blockLocation.getBlock();

                    boolean canPlace = block.getType() == Material.AIR;
                    Material previewMaterial = canPlace ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS;
                    sendBlockChangePacket(player, blockLocation, previewMaterial);
                    previewLocations.add(blockLocation);
                }
            }
        } else {
            // For fundament and sufit (horizontal structures)
            for (int x = 0; x < type.getWidth(); x++) {
                for (int y = 0; y < type.getHeight(); y++) {
                    for (int z = 0; z < type.getDepth(); z++) {
                        Location blockLocation = baseLocation.clone().add(x, y, z);
                        Block block = blockLocation.getBlock();

                        boolean canPlace = block.getType() == Material.AIR;
                        Material previewMaterial = canPlace ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS;
                        sendBlockChangePacket(player, blockLocation, previewMaterial);
                        previewLocations.add(blockLocation);
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
    }
}
