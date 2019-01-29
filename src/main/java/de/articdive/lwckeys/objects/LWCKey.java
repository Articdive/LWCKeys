package de.articdive.lwckeys.objects;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.Period;

import java.util.List;
import java.util.Map;

public class LWCKey {
    private final String name;
    private final String displayName;
    private final List<String> lore;
    private final Material material;
    private final Map<Enchantment, Integer> enchantments;
    private final Period timeRequirement;
    private final boolean permissionRequired;
    private final boolean sinceOwnerOnline;
    private final boolean removeProtection;

    public LWCKey(String name, String displayName, List<String> lore, Material material, Map<Enchantment, Integer> enchantments, Period timeRequirement, boolean permissionRequired, boolean sinceOwnerOnline, boolean removeProtection) {
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.material = material;
        this.enchantments = enchantments;
        this.timeRequirement = timeRequirement;
        this.permissionRequired = permissionRequired;
        this.sinceOwnerOnline = sinceOwnerOnline;
        this.removeProtection = removeProtection;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return lore;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public Period getTimeRequirement() {
        return timeRequirement;
    }

    public boolean isSinceOwnerOnline() {
        return sinceOwnerOnline;
    }

    public boolean isRemoveProtection() {
        return removeProtection;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public static ItemStack createItemStack(LWCKey lwcKey, int amount) {
        ItemStack key = new ItemStack(lwcKey.getMaterial(), amount);
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName(lwcKey.getDisplayName());
        keyMeta.setLore(lwcKey.getLore());
        for (Enchantment enchantment : lwcKey.getEnchantments().keySet()) {
            keyMeta.addEnchant(enchantment, lwcKey.getEnchantments().get(enchantment), true);
        }
        key.setItemMeta(keyMeta);
        return key;
    }
}
