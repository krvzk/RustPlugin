package com.krvzk.rustplugin.listeners;

import com.krvzk.rustplugin.RustPlugin;
import com.krvzk.rustplugin.builders.BuilderManager;
import com.krvzk.rustplugin.builders.BuilderSession;
import com.krvzk.rustplugin.utils.PreviewRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PreviewListener implements Listener {

    private final RustPlugin plugin;
    private final BuilderManager builderManager;
    private final PreviewRenderer previewRenderer;

    public PreviewListener(RustPlugin plugin, BuilderManager builderManager) {
        this.plugin = plugin;
        this.builderManager = builderManager;
        this.previewRenderer = new PreviewRenderer(plugin, builderManager);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        BuilderSession session = builderManager.getSession(playerUUID);

        if (session == null || !session.hasStructureSelected()) {
            return;
        }

        // Update preview for the player
        previewRenderer.updatePreview(player, session.getSelectedStructure());
    }
}
