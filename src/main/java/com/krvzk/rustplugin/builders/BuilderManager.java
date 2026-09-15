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
        player.sendMessage("§aSelected structure: §6" + structureType.getDisplayName());
    }

    public void placeStructure(Player player, Structure structure) {
        // Validate placement
        if (!blockValidator.canPlaceStructure(structure)) {
            player.sendMessage("§cCannot place structure here!");
            return;
        }

        // Place blocks in the world
        blockValidator.placeStructureBlocks(structure);

        // Save to database
        structureManager.saveStructure(structure);
        player.sendMessage("§aStructure placed successfully!");
    }

    public StructureType getSelectedStructure(UUID playerUUID) {
        return playerSelectedStructures.get(playerUUID);
    }

    public boolean hasStructureSelected(UUID playerUUID) {
        return playerSelectedStructures.containsKey(playerUUID);
    }
}
