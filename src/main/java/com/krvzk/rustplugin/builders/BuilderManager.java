package com.krvzk.rustplugin.builders;

import com.krvzk.rustplugin.RustPlugin;
import com.krvzk.rustplugin.database.DatabaseManager;
import com.krvzk.rustplugin.structures.Structure;
import com.krvzk.rustplugin.structures.StructureManager;
import com.krvzk.rustplugin.structures.StructureType;
import com.krvzk.rustplugin.gui.BuildMenuGUI;
import com.krvzk.rustplugin.utils.BlockValidator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuilderManager {

    private final RustPlugin plugin;
    private final StructureManager structureManager;
    private final DatabaseManager databaseManager;
    private final Map<UUID, StructureType> playerSelectedStructures = new HashMap<>();
    private final BlockValidator blockValidator;

    public BuilderManager(RustPlugin plugin, StructureManager structureManager, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.structureManager = structureManager;
        this.databaseManager = databaseManager;
        this.blockValidator = new BlockValidator(structureManager, databaseManager);
    }

    public void openBuildMenu(Player player) {
        BuildMenuGUI.openMenu(player, this);
    }

    public void selectStructure(Player player, StructureType structureType) {
        UUID playerUUID = player.getUniqueId();
        playerSelectedStructures.put(playerUUID, structureType);
        player.sendMessage("\u00a7aSelected structure: \u00a76" + structureType.getDisplayName());
    }

    public void deselectStructure(UUID playerUUID) {
        playerSelectedStructures.remove(playerUUID);
    }

    public void placeStructure(Player player, Structure structure, float yaw) {
        // Validate placement
        if (!blockValidator.canPlaceStructure(structure)) {
            player.sendMessage("\u00a7cCannot place structure here!");
            return;
        }

        // Place blocks in the world with rotation
        blockValidator.placeStructureBlocks(structure, yaw);

        // Save to database
        structureManager.saveStructure(structure);
        player.sendMessage("\u00a7aStructure placed successfully!");
    }

    public StructureType getSelectedStructure(UUID playerUUID) {
        return playerSelectedStructures.get(playerUUID);
    }

    public boolean hasStructureSelected(UUID playerUUID) {
        return playerSelectedStructures.containsKey(playerUUID);
    }
}
