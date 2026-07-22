package com.apollosmp.gui.menus;

import com.apollosmp.ApolloSMP;
import com.apollosmp.gui.Gui;
import com.apollosmp.sell.SellManager;
import com.apollosmp.util.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SellMenu extends Gui {

    public SellMenu(ApolloSMP plugin, Player viewer) {
        super(plugin, viewer, 6, "<#ff4e50><bold>Sell to Server</bold>");
    }

    @Override
    protected void build() {
        int slot = 0;
        for (Map.Entry<Material, Double> entry : plugin.sell().allPrices().entrySet()) {
            if (slot >= 45) break;
            inventory.setItem(slot, Items.of(entry.getKey())
                    .name("<white>" + Items.pretty(entry.getKey()))
                    .lore("<gray>Sells for <#f9d423>" + plugin.msg().money(entry.getValue())
                            + "</#f9d423> <gray>each")
                    .hideAttributes().build());
            slot++;
        }

        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, Items.filler(Material.BLACK_STAINED_GLASS_PANE));
        }

        inventory.setItem(45, Items.of(Material.ARROW)
                .name("<gray>Back").build());

        inventory.setItem(48, Items.of(Material.HOPPER)
                .name("<#f9d423><bold>Sell Item in Hand</bold>")
                .lore("<gray>Sells the stack you're holding", "", "<yellow>Click to sell")
                .hideAttributes().build());

        inventory.setItem(50, Items.of(Material.CHEST)
                .name("<#ff4e50><bold>Sell All Loot</bold>")
                .lore("<gray>Sells every sellable item", "<gray>in your inventory",
                        "<dark_gray>(named/enchanted items are skipped)", "", "<yellow>Click to sell")
                .hideAttributes().build());
    }

    @Override
    public void onClick(Player player, int slot, ItemStack clicked, ClickType click) {
        switch (slot) {
            case 45 -> new MainMenu(plugin, player).open();
            case 48 -> {
                SellManager.Result r = plugin.sell().sellHand(player);
                if (!r.soldAnything()) {
                    plugin.msg().send(player, "<red>That item can't be sold to the server.");
                } else {
                    plugin.msg().send(player, "<green>Sold <white>" + r.quantity()
                            + "</white> for <#f9d423>" + plugin.msg().money(r.earned()) + "</#f9d423>.");
                }
            }
            case 50 -> {
                SellManager.Result r = plugin.sell().sellAll(player);
                if (!r.soldAnything()) {
                    plugin.msg().send(player, "<red>You have nothing sellable in your inventory.");
                } else {
                    plugin.msg().send(player, "<green>Sold <white>" + r.quantity()
                            + "</white> items for <#f9d423>" + plugin.msg().money(r.earned()) + "</#f9d423>.");
                }
            }
            default -> { /* info icons: no-op */ }
        }
    }
}
