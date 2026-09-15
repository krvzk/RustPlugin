package com.krvzk.rustplugin.utils;

import com.krvzk.rustplugin.database.DatabaseManager;
import com.krvzk.rustplugin.structures.Structure;
import com.krvzk.rustplugin.structures.StructureManager;
import com.krvzk.rustplugin.structures.StructureType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class BlockValidator {

    private final StructureManager structureManager;
    private final DatabaseManager databaseManager;

    public BlockValidator(StructureManager structureManager, DatabaseManager databaseManager) {
        this.structureManager = structureManager;
        this.databaseManager = databaseManager;
    }

    public boolean canPlaceStructure(Structure structure) {
        StructureType type = structure.getType();
        Location baseLocation = structure.getLocation();

        // Check if blocks are available (except for edge blocks that can be replaced by edges)
        for (int x = 0; x < type.getWidth(); x++) {
            for (int y = 0; y < type.getHeight(); y++) {
                for (int z = 0; z < type.getDepth(); z++) {
                    Location checkLocation = baseLocation.clone().add(x, y, z);
                    Block block = checkLocation.getBlock();

                    // Check if block is air
                    if (block.getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }

        // Check if there are existing structures in the area
        List<Structure> nearbyStructures = structureManager.getStructuresInArea(
                baseLocation,
                Math.max(type.getWidth(), type.getDepth()) + 2,
                baseLocation.getWorld().getName()
        );

        for (Structure existing : nearbyStructures) {
            // Don't allow fundament on fundament
            if (type == StructureType.FUNDAMENT && existing.getType() == StructureType.FUNDAMENT) {
                if (isDirectlyAboveOrBelow(structure, existing)) {
                    return false;
                }
            }
            
            // Check for overlapping interiors (not edges)
            if (structuresInteriorOverlap(structure, existing)) {
                return false;
            }
        }

        return true;
    }

    private boolean isDirectlyAboveOrBelow(Structure s1, Structure s2) {
        Location loc1 = s1.getLocation();
        Location loc2 = s2.getLocation();

        // Check if they're directly above/below each other (same X and Z, different Y)
        int x1_min = loc1.getBlockX();
        int x1_max = x1_min + s1.getType().getWidth();
        int z1_min = loc1.getBlockZ();
        int z1_max = z1_min + s1.getType().getDepth();

        int x2_min = loc2.getBlockX();
        int x2_max = x2_min + s2.getType().getWidth();
        int z2_min = loc2.getBlockZ();
        int z2_max = z2_min + s2.getType().getDepth();

        // Check if X and Z ranges overlap or are adjacent
        return !(x1_max < x2_min || x1_min > x2_max || z1_max < z2_min || z1_min > z2_max);
    }

    private boolean structuresInteriorOverlap(Structure s1, Structure s2) {
        Location loc1 = s1.getLocation();
        Location loc2 = s2.getLocation();
        StructureType type1 = s1.getType();
        StructureType type2 = s2.getType();

        // Get interior bounds (excluding edges)
        int x1_min = loc1.getBlockX() + 1;
        int x1_max = x1_min + type1.getWidth() - 2;
        int y1_min = loc1.getBlockY();
        int y1_max = y1_min + type1.getHeight();
        int z1_min = loc1.getBlockZ() + 1;
        int z1_max = z1_min + type1.getDepth() - 2;

        int x2_min = loc2.getBlockX() + 1;
        int x2_max = x2_min + type2.getWidth() - 2;
        int y2_min = loc2.getBlockY();
        int y2_max = y2_min + type2.getHeight();
        int z2_min = loc2.getBlockZ() + 1;
        int z2_max = z2_min + type2.getDepth() - 2;

        return x1_min < x2_max && x1_max > x2_min &&
               y1_min < y2_max && y1_max > y2_min &&
               z1_min < z2_max && z1_max > z2_min;
    }

    public void placeStructureBlocks(Structure structure, float yaw) {
        StructureType type = structure.getType();
        Location baseLocation = structure.getLocation();

        // Normalize yaw to 0-360
        float normalizedYaw = ((yaw + 180) % 360);
        if (normalizedYaw < 0) normalizedYaw += 360;

        if (type == StructureType.FUNDAMENT) {
            // 5x5 fundament with 3x3 planks in center and wood around edges
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    Location blockLocation = baseLocation.clone().add(x, 0, z);
                    Block block = blockLocation.getBlock();

                    // 3x3 planks in center (positions 1-3 in both axes)
                    if (x >= 1 && x <= 3 && z >= 1 && z <= 3) {
                        block.setType(Material.OAK_PLANKS);
                    } else {
                        // Wood on edges
                        block.setType(Material.OAK_LOG);
                    }
                }
            }
        } else if (type == StructureType.SCIANA) {
            // 5x5x1 wall with rotation based on yaw
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

                    blockLocation.getBlock().setType(Material.OAK_LOG);
                }
            }
        } else if (type == StructureType.SUFIT) {
            // 5x5 ceiling with 3x3 planks in center and wood around edges
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    Location blockLocation = baseLocation.clone().add(x, 0, z);
                    Block block = blockLocation.getBlock();

                    // 3x3 planks in center (positions 1-3 in both axes)
                    if (x >= 1 && x <= 3 && z >= 1 && z <= 3) {
                        block.setType(Material.OAK_PLANKS);
                    } else {
                        // Wood on edges
                        block.setType(Material.OAK_LOG);
                    }
                }
            }
        }
    }
}
