package com.krvzk.rustplugin;

import com.krvzk.rustplugin.database.DatabaseManager;
import com.krvzk.rustplugin.builders.BuilderManager;
import com.krvzk.rustplugin.structures.StructureManager;
import com.krvzk.rustplugin.listeners.PlayerInteractListener;
import com.krvzk.rustplugin.listeners.PreviewListener;
import com.krvzk.rustplugin.utils.PreviewRenderer;
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
            if (!(sender instanceof org.bukkit.entity.Player)) {
                sender.sendMessage("Only players can use this command!");
                return true;
            }

            org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
            builderManager.openBuildMenu(player);
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
