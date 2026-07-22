package com.apollosmp.sell;

import com.apollosmp.ApolloSMP;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public class SellManager {

    private final ApolloSMP plugin;
    private final Map<Material, Double> prices = new EnumMap<>(Material.class);

    public SellManager(ApolloSMP plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        prices.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("sell.prices");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            Material mat = Material.matchMaterial(key);
            if (mat == null) {
                plugin.getLogger().warning("Unknown sell material in config: " + key);
                continue;
            }
            prices.put(mat, sec.getDouble(key));
        }
    }

    public boolean isSellable(Material material) {
        return prices.containsKey(material);
    }

    public double priceOf(Material material) {
        return prices.getOrDefault(material, 0.0);
    }

    public Map<Material, Double> allPrices() {
        return new EnumMap<>(prices);
    }

    /** Result of a sell operation. */
    public record Result(int quantity, double earned) {
        public boolean soldAnything() { return quantity > 0; }
    }

    /** Sell the item currently in the main hand (whole stack). */
    public Result sellHand(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir() || !isSellable(hand.getType())) {
            return new Result(0, 0);
        }
        int qty = hand.getAmount();
        double earned = qty * priceOf(hand.getType());
        player.getInventory().setItemInMainHand(null);
        plugin.economy().deposit(player.getUniqueId(), earned);
        return new Result(qty, earned);
    }

    /** Sell every sellable item in the player's inventory. */
    public Result sellAll(Player player) {
        int totalQty = 0;
        double totalEarned = 0;
        ItemStack[] contents = player.getInventory().getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType().isAir()) continue;
            if (!isSellable(stack.getType())) continue;
            // Only sell "plain" items (no custom name / enchants) to avoid dumping gear.
            if (stack.hasItemMeta() && (stack.getItemMeta().hasDisplayName()
                    || stack.getItemMeta().hasEnchants())) {
                continue;
            }
            int qty = stack.getAmount();
            totalQty += qty;
            totalEarned += qty * priceOf(stack.getType());
            contents[i] = null;
        }
        if (totalQty > 0) {
            player.getInventory().setStorageContents(contents);
            plugin.economy().deposit(player.getUniqueId(), totalEarned);
        }
        return new Result(totalQty, totalEarned);
    }
}
