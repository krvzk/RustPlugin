package com.krvzk.rustplugin;

import com.krvzk.rustplugin.database.DatabaseManager;
import com.krvzk.rustplugin.builders.BuilderManager;
import com.krvzk.rustplugin.structures.StructureManager;
import com.krvzk.rustplugin.listeners.PlayerInteractListener;
import com.krvzk.rustplugin.listeners.PreviewListener;
import com.krvzk.rustplugin.utils.PreviewRenderer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class RustPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private StructureManager structureManager;
    private BuilderManager builderManager;
    private PreviewRenderer previewRenderer;

    @Override
    public void onEnable() {
        getLogger().info("RustPlugin enabled!");

        // Initialize database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        // Initialize managers
        structureManager = new StructureManager(databaseManager);
        builderManager = new BuilderManager(this, structureManager, databaseManager);
        previewRenderer = new PreviewRenderer(this, builderManager);

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this, builderManager), this);
        getServer().getPluginManager().registerEvents(new PreviewListener(this, builderManager), this);

        // Register commands
        getCommand("buduj").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            Player player = (Player) sender;
            giveBlueprintItem(player);
            return true;
        });
    }

    @Override
    public void onDisable() {
        getLogger().info("RustPlugin disabled!");
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    private void giveBlueprintItem(Player player) {
        ItemStack blueprint = new ItemStack(Material.PAPER);
        ItemMeta meta = blueprint.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Plan Budowy");
            blueprint.setItemMeta(meta);
        }
        player.getInventory().addItem(blueprint);
        player.sendMessage("§aOtrzymałeś Plan Budowy! PPM - Menu, LPM - Buduj");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public BuilderManager getBuilderManager() {
        return builderManager;
    }

    public PreviewRenderer getPreviewRenderer() {
        return previewRenderer;
    }
}
