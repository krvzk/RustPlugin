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

        // Check if blocks are available
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
                Math.max(type.getWidth(), type.getDepth()) + 1,
                baseLocation.getWorld().getName()
        );

        for (Structure existing : nearbyStructures) {
            if (structuresOverlap(structure, existing)) {
                return false;
            }
        }

        return true;
    }

    private boolean structuresOverlap(Structure s1, Structure s2) {
        Location loc1 = s1.getLocation();
        Location loc2 = s2.getLocation();
        StructureType type1 = s1.getType();
        StructureType type2 = s2.getType();

        // Simple AABB collision detection
        int x1_min = loc1.getBlockX();
        int x1_max = x1_min + type1.getWidth();
        int y1_min = loc1.getBlockY();
        int y1_max = y1_min + type1.getHeight();
        int z1_min = loc1.getBlockZ();
        int z1_max = z1_min + type1.getDepth();

        int x2_min = loc2.getBlockX();
        int x2_max = x2_min + type2.getWidth();
        int y2_min = loc2.getBlockY();
        int y2_max = y2_min + type2.getHeight();
        int z2_min = loc2.getBlockZ();
        int z2_max = z2_min + type2.getDepth();

        return x1_min < x2_max && x1_max > x2_min &&
               y1_min < y2_max && y1_max > y2_min &&
               z1_min < z2_max && z1_max > z2_min;
    }
}
