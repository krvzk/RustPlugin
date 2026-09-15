package com.krvzk.rustplugin.structures;

import com.krvzk.rustplugin.database.DatabaseManager;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StructureManager {

    private final DatabaseManager databaseManager;

    public StructureManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void saveStructure(Structure structure) {
        String query = "INSERT INTO structures (uuid, type, x, y, z, world, rotation) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = databaseManager.getConnection().prepareStatement(query)) {
            pstmt.setString(1, structure.getPlayerUUID().toString());
            pstmt.setString(2, structure.getType().name());
            pstmt.setInt(3, structure.getLocation().getBlockX());
            pstmt.setInt(4, structure.getLocation().getBlockY());
            pstmt.setInt(5, structure.getLocation().getBlockZ());
            pstmt.setString(6, structure.getLocation().getWorld().getName());
            pstmt.setInt(7, structure.getRotation());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Structure> loadStructures(String worldName) {
        List<Structure> structures = new ArrayList<>();
        String query = "SELECT * FROM structures WHERE world = ?";

        try (PreparedStatement pstmt = databaseManager.getConnection().prepareStatement(query)) {
            pstmt.setString(1, worldName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id");
                UUID playerUUID = UUID.fromString(rs.getString("uuid"));
                StructureType type = StructureType.valueOf(rs.getString("type"));
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                int rotation = rs.getInt("rotation");

                Location location = new Location(org.bukkit.Bukkit.getWorld(worldName), x, y, z);
                BlockFace facing = getBlockFaceFromRotation(rotation);

                Structure structure = new Structure(id, playerUUID, type, location, facing);
                structures.add(structure);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return structures;
    }

    public List<Structure> getStructuresInArea(Location location, int radius, String worldName) {
        List<Structure> allStructures = loadStructures(worldName);
        List<Structure> nearby = new ArrayList<>();

        for (Structure structure : allStructures) {
            if (structure.getLocation().distance(location) <= radius) {
                nearby.add(structure);
            }
        }

        return nearby;
    }

    private BlockFace getBlockFaceFromRotation(int rotation) {
        return switch (rotation) {
            case 0 -> BlockFace.NORTH;
            case 90 -> BlockFace.EAST;
            case 180 -> BlockFace.SOUTH;
            case 270 -> BlockFace.WEST;
            default -> BlockFace.NORTH;
        };
    }
}
