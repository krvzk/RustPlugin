package com.krvzk.rustplugin.structures;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import java.util.UUID;

public class Structure {

    private final UUID playerUUID;
    private final StructureType type;
    private final Location location;
    private final BlockFace facing;
    private long id;

    public Structure(UUID playerUUID, StructureType type, Location location, BlockFace facing) {
        this.playerUUID = playerUUID;
        this.type = type;
        this.location = location;
        this.facing = facing != null ? facing : BlockFace.NORTH; // Default to NORTH if null
    }

    public Structure(long id, UUID playerUUID, StructureType type, Location location, BlockFace facing) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.type = type;
        this.location = location;
        this.facing = facing != null ? facing : BlockFace.NORTH; // Default to NORTH if null
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public StructureType getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public BlockFace getFacing() {
        return facing;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRotation() {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
