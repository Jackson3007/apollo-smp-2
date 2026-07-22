package com.apollosmp.gui;

import com.apollosmp.ApolloSMP;
import com.apollosmp.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Base class for all Apollo menus. Being the {@link InventoryHolder} of its own
 * inventory lets the click listener identify Apollo menus reliably.
 */
public abstract class Gui implements InventoryHolder {

    protected final ApolloSMP plugin;
    protected final Player viewer;
    protected final Inventory inventory;

    protected Gui(ApolloSMP plugin, Player viewer, int rows, String titleMini) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.inventory = Bukkit.createInventory(this, Math.max(9, rows * 9), Msg.mm(titleMini));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        redraw();
        viewer.openInventory(inventory);
    }

    public void redraw() {
        inventory.clear();
        build();
    }

    /** Populate the inventory. Called on open and on redraw. */
    protected abstract void build();

    /** Handle a click on a top-inventory slot. */
    public abstract void onClick(Player player, int slot, ItemStack clicked, ClickType click);

    protected void fillEmpty(ItemStack filler) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, filler);
        }
    }
}
