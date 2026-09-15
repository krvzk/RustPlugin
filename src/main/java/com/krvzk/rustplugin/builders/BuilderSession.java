package com.krvzk.rustplugin.builders;

import com.krvzk.rustplugin.structures.StructureType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BuilderSession {

    private final UUID playerUUID;
    private final Player player;
    private StructureType selectedStructure;
    private ItemStack blueprintItem;

    public BuilderSession(Player player, ItemStack blueprintItem) {
        this.playerUUID = player.getUniqueId();
        this.player = player;
        this.blueprintItem = blueprintItem;
        this.selectedStructure = null;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Player getPlayer() {
        return player;
    }

    public StructureType getSelectedStructure() {
        return selectedStructure;
    }

    public void setSelectedStructure(StructureType structureType) {
        this.selectedStructure = structureType;
    }

    public ItemStack getBlueprintItem() {
        return blueprintItem;
    }

    public boolean hasStructureSelected() {
        return selectedStructure != null;
    }
}
