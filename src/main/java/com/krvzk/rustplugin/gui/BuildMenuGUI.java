package com.krvzk.rustplugin.gui;

import com.krvzk.rustplugin.builders.BuilderManager;
import com.krvzk.rustplugin.structures.StructureType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BuildMenuGUI implements Listener {

    private static final String GUI_TITLE = "§6Build Menu";

    public static void openMenu(Player player, BuilderManager builderManager) {
        Inventory inventory = Bukkit.createInventory(null, 27, GUI_TITLE);

        // Fundament
        ItemStack fundament = new ItemStack(Material.STONE);
        ItemMeta fundaMeta = fundament.getItemMeta();
        if (fundaMeta != null) {
            fundaMeta.setDisplayName("§6Fundament");
            fundament.setItemMeta(fundaMeta);
        }
        inventory.setItem(10, fundament);

        // Ściana
        ItemStack sciana = new ItemStack(Material.BRICKS);
        ItemMeta sciMeta = sciana.getItemMeta();
        if (sciMeta != null) {
            sciMeta.setDisplayName("§6Ściana");
            sciana.setItemMeta(sciMeta);
        }
        inventory.setItem(13, sciana);

        // Sufit
        ItemStack sufit = new ItemStack(Material.DARK_OAK_PLANKS);
        ItemMeta sufMeta = sufit.getItemMeta();
        if (sufMeta != null) {
            sufMeta.setDisplayName("§6Sufit");
            sufit.setItemMeta(sufMeta);
        }
        inventory.setItem(16, sufit);

        player.openInventory(inventory);
    }

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        BuilderManager builderManager = ((com.krvzk.rustplugin.RustPlugin) Bukkit.getPluginManager()
                .getPlugin("RustPlugin")).getBuilderManager();

        if (slot == 10) {
            builderManager.selectStructure(player, StructureType.FUNDAMENT);
        } else if (slot == 13) {
            builderManager.selectStructure(player, StructureType.SCIANA);
        } else if (slot == 16) {
            builderManager.selectStructure(player, StructureType.SUFIT);
        }

        player.closeInventory();
    }
}
