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
import java.util.Map;
import java.util.UUID;

public class PreviewRenderer {

    private final RustPlugin plugin;
    private final BuilderManager builderManager;
    private final ProtocolManager protocolManager;
    private final Map<UUID, Integer> lastPreviewTaskId = new HashMap<>();

    public PreviewRenderer(RustPlugin plugin, BuilderManager builderManager) {
        this.plugin = plugin;
        this.builderManager = builderManager;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void updatePreview(Player player, StructureType structureType) {
        // Ray trace to find where player is looking
        RayTraceResult rayTrace = player.rayTraceBlocks(5);

        if (rayTrace == null || rayTrace.getHitBlock() == null) {
            clearPreview(player);
            return;
        }

        Block targetBlock = rayTrace.getHitBlock();
        Location previewLocation = targetBlock.getLocation().add(0, 1, 0);

        // Send block change packets for preview
        sendBlockPreview(player, previewLocation, structureType);
    }

    private void sendBlockPreview(Player player, Location baseLocation, StructureType type) {
        BlockValidator validator = new BlockValidator(
                plugin.getStructureManager(),
                plugin.getDatabaseManager()
        );

        for (int x = 0; x < type.getWidth(); x++) {
            for (int y = 0; y < type.getHeight(); y++) {
                for (int z = 0; z < type.getDepth(); z++) {
                    Location blockLocation = baseLocation.clone().add(x, y, z);
                    Block block = blockLocation.getBlock();

                    // Determine if this block can be placed
                    boolean canPlace = block.getType() == Material.AIR;

                    // Send preview block
                    Material previewMaterial = canPlace ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS;
                    sendBlockChangePacket(player, blockLocation, previewMaterial);
                }
            }
        }
    }

    private void sendBlockChangePacket(Player player, Location location, Material material) {
        try {
            var packet = protocolManager.createPacketConstructor(PacketType.Play.Server.BLOCK_CHANGE)
                    .createPacket(
                            new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                            WrappedBlockData.createData(material)
                    );
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to send block preview packet: " + e.getMessage());
        }
    }

    public void clearPreview(Player player) {
        // Clear previews by sending actual block data
        // This would require storing what blocks were previously shown
    }
}
