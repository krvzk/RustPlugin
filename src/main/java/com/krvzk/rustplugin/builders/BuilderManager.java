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
    private final Map<UUID, BuilderSession> activeSessions = new HashMap<>();
    private final BlockValidator blockValidator;

    public BuilderManager(RustPlugin plugin, StructureManager structureManager, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.structureManager = structureManager;
        this.databaseManager = databaseManager;
        this.blockValidator = new BlockValidator(structureManager, databaseManager);
    }

    public void startBuilding(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (activeSessions.containsKey(playerUUID)) {
            player.sendMessage("§cYou are already in building mode!");
            return;
        }

        // Create blueprint item
        ItemStack blueprint = new ItemStack(Material.PAPER);
        ItemMeta meta = blueprint.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Plan Budowy");
            blueprint.setItemMeta(meta);
        }

        // Add to player inventory
        player.getInventory().addItem(blueprint);

        // Create session
        BuilderSession session = new BuilderSession(player, blueprint);
        activeSessions.put(playerUUID, session);

        player.sendMessage("§aBuilding mode enabled! Right-click the Blueprint to select a structure.");
    }

    public void openBuildMenu(Player player) {
        BuildMenuGUI.openMenu(player, this);
    }

    public void selectStructure(Player player, StructureType structureType) {
        UUID playerUUID = player.getUniqueId();
        BuilderSession session = activeSessions.get(playerUUID);

        if (session != null) {
            session.setSelectedStructure(structureType);
            player.sendMessage("§aSelected structure: §6" + structureType.getDisplayName());
        }
    }

    public void placeStructure(Player player, Structure structure) {
        UUID playerUUID = player.getUniqueId();
        BuilderSession session = activeSessions.get(playerUUID);

        if (session == null) {
            return;
        }

        // Validate placement
        if (!blockValidator.canPlaceStructure(structure)) {
            player.sendMessage("§cCannot place structure here!");
            return;
        }

        // Save to database
        structureManager.saveStructure(structure);
        player.sendMessage("§aStructure placed successfully!");
    }

    public BuilderSession getSession(UUID playerUUID) {
        return activeSessions.get(playerUUID);
    }

    public void endSession(Player player) {
        UUID playerUUID = player.getUniqueId();
        activeSessions.remove(playerUUID);
        player.sendMessage("§cBuilding mode disabled.");
    }

    public boolean isInBuildingMode(UUID playerUUID) {
        return activeSessions.containsKey(playerUUID);
    }
}
