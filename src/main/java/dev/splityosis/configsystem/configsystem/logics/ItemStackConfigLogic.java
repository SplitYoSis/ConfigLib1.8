package dev.splityosis.configsystem.configsystem.logics;

import dev.splityosis.configsystem.configsystem.ConfigTypeLogic;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ItemStackConfigLogic extends ConfigTypeLogic<ItemStack> {

    @Override
    public ItemStack getFromConfig(ConfigurationSection config, String path) {
        Material material = Material.valueOf(config.getString(path + ".material"));
        int amount = config.getInt(path + ".amount");
        String name = colorize(config.getString(path + ".custom-name"));
        if (name == null && config.isSet(path + ".custom-name"))
            name = "";
        List<String> lore = colorize(config.getStringList(path + ".custom-lore"));
        Map<Enchantment, Integer> enchants = new HashMap<>();
        ConfigurationSection enchantsSection = config.getConfigurationSection(path + ".enchants");
        if (enchantsSection != null) {
            for (String key : enchantsSection.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(key.toLowerCase()));
                if (enchantment != null){
                    enchants.put(enchantment, enchantsSection.getInt(key));
                }
            }
        }

        ItemStack item;
        if(material == Material.PLAYER_HEAD) {
            item = new ItemStack(material, amount);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwner(config.getString(path + ".owner"));
            item.setItemMeta(meta);
        } else if(material.name().contains("POTION")) {
            item = new ItemStack(material, amount);
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.valueOf(config.getString(path + ".potion-type"))));
            item.setItemMeta(meta);
        } else if(material.name().startsWith("LEATHER_")) {
            item = new ItemStack(material, amount);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            String color = config.getString(path + ".color");
            meta.setColor(Color.fromRGB(Integer.valueOf(color.substring(0, 2), 16), Integer.valueOf(color.substring(2, 4), 16), Integer.valueOf(color.substring(4, 6), 16)));
            item.setItemMeta(meta);
        } else {
            item = new ItemStack(material, amount);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        item.addUnsafeEnchantments(enchants);

        return item;
    }

    @Override
    public void setInConfig(ItemStack instance, ConfigurationSection config, String path) {
        config.set(path + ".material", instance.getType().toString());
        config.set(path + ".amount", instance.getAmount());

        ItemMeta meta = instance.getItemMeta();
        if(meta != null) {
            config.set(path + ".custom-name", reverseColorize(meta.getDisplayName()));
            config.set(path + ".custom-lore", reverseColorize(meta.getLore()));

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.hasOwner()) {
                    config.set(path + ".owner", skullMeta.getOwningPlayer() == null ? null : skullMeta.getOwningPlayer().getName());
                }
            } else if (meta instanceof PotionMeta) {
                PotionMeta potionMeta = (PotionMeta) meta;
                config.set(path + ".potion-type", potionMeta.getBasePotionData().getType().toString());
            } else if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
                String rgb = String.format("%02x%02x%02x", armorMeta.getColor().getRed(), armorMeta.getColor().getGreen(), armorMeta.getColor().getBlue());
                config.set(path + ".color", rgb);
            }
        }

        if (!instance.getEnchantments().isEmpty()) {
            ConfigurationSection enchantsSection = config.createSection(path + ".enchants");
            instance.getEnchantments().forEach((enchantment, integer) -> enchantsSection.set(enchantment.getKey().getKey(), integer));
        }
    }
}