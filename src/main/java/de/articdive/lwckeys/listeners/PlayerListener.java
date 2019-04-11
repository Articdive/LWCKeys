package de.articdive.lwckeys.listeners;

import com.griefcraft.model.Protection;
import de.articdive.enum_to_yaml.EnumConfiguration;
import de.articdive.lwckeys.LWCKeys;
import de.articdive.lwckeys.configuration.KeysConfiguration;
import de.articdive.lwckeys.implementations.TownyImplementation;
import de.articdive.lwckeys.objects.LWCKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    private LWCKeys lwcKeysMain = LWCKeys.getPlugin(LWCKeys.class);
    private EnumConfiguration keysConfiguration = lwcKeysMain.getKeysConfiguration();
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (event.hasItem() &&
                event.hasBlock() &&
                event.getHand() == EquipmentSlot.HAND &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getClickedBlock().getType() != Material.AIR &&
                event.getItem().getType() != Material.AIR && event.getPlayer().isSneaking()) {
            
            ItemStack item = event.getItem();
            
            String itemDisplayName = null;
            List<String> itemLore = new ArrayList<>();
            Material itemMaterial = item.getType();
            Map<Enchantment, Integer> itemEnchants = new HashMap<>();
            
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasDisplayName()) {
                    itemDisplayName = item.getItemMeta().getDisplayName();
                }
                if (item.getItemMeta().hasLore()) {
                    itemLore = item.getItemMeta().getLore();
                }
                if (item.getItemMeta().hasEnchants()) {
                    itemEnchants = item.getItemMeta().getEnchants();
                }
            }
            
            LWCKey key = matchKey(itemDisplayName, itemLore, itemMaterial, itemEnchants);
            if (key == null) {
                return;
            }
            
            Player player = event.getPlayer();
            if (key.isPermissionRequired() && !player.hasPermission("lwckeys.use." + key.getName().toLowerCase()) && !player.hasPermission("lwckeys.use.*") && !player.hasPermission("lwckeys.use")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NO_PERMISSION_MESSAGE)));
                return;
            }
            Protection protection = lwcKeysMain.getLWC().findProtection(event.getClickedBlock().getLocation());
            if (protection == null) {
                return;
            }
            if (protection.getOwner().equalsIgnoreCase(player.getUniqueId().toString())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.UNLOCK_OWN_CHEST_MESSAGE)));
                return;
            }
            OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(protection.getOwner()));
            if (owner.isOnline() && key.isSinceOwnerOnline()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.OWNER_ONLINE_MESSAGE).replace("{owner}", owner.getName())));
                return;
            }
            long lastTime;
            if (key.isSinceOwnerOnline()) {
                lastTime = owner.getLastPlayed();
            } else {
                lastTime = protection.getLastAccessed();
            }
            if (lwcKeysMain.isTownyEnabled()) {
                if (!key.isTownyClaimed() && !key.isTownyWilderness() && !key.isTownyClaimedOwn()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.TOWNY_NO_PERMISSION)));
                    return;
                }
                TownyImplementation implementation = new TownyImplementation();
                // !Wilderness && Wilderness
                if (!key.isTownyWilderness() && !implementation.isAreaClaimed(event.getClickedBlock().getLocation())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.TOWNY_NO_PERMISSION)));
                    return;
                }
                // !ClaimedAll, !ClaimedOwn && Claimed
                if (!key.isTownyClaimed() && !key.isTownyClaimedOwn() && implementation.isAreaClaimed(event.getClickedBlock().getLocation())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.TOWNY_NO_PERMISSION)));
                    return;
                }
                // !ClaimedAll, ClaimedOwn && Claimed
                if (!key.isTownyClaimed() && key.isTownyClaimedOwn() && implementation.isAreaClaimed(event.getClickedBlock().getLocation())) {
                    if (!implementation.isResidentOfTown(player, event.getClickedBlock().getLocation())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.TOWNY_NO_PERMISSION)));
                        return;
                    }
                }
                // ClaimedAll, !ClaimedOwn (makes no sense)
            }
            DateTime lastTimeStamp = new DateTime(lastTime);
            if ((lastTimeStamp.plus(key.getTimeRequirement())).isBeforeNow()) {
                if (key.isRemoveProtection()) {
                    protection.remove();
                } else {
                    protection.setOwner(player.getUniqueId().toString());
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.UNLOCK_MESSAGE).replace("{owner}", owner.getName())));
                if (player.getGameMode() == GameMode.CREATIVE) {
                    return;
                }
                if (player.getInventory().getItemInMainHand().isSimilar(LWCKey.createItemStack(key, 1))) {
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.TIME_REQUIRED_MESSAGE).replace("{time}", getRequiredTime(key, lastTimeStamp))));
            }
            
        }
    }
    
    private CharSequence getRequiredTime(LWCKey key, DateTime lastTimeStamp) {
        Period period = new Period(lastTimeStamp.plus(key.getTimeRequirement()), new DateTime());
        if (period.getSeconds() < 0) {
            period = period.minus(period).minus(period);
        }
        return period.getYears() + " years, " +
                period.getMonths() + " months, " +
                period.getWeeks() + " weeks, " +
                period.getDays() + " days, " +
                period.getHours() + " hours, " +
                period.getMinutes() + " minutes, " +
                period.getSeconds() + " seconds";
    }
    
    private LWCKey matchKey(String itemDisplayName, List<String> itemLore, Material itemMaterial, Map<Enchantment, Integer> itemEnchants) {
        for (LWCKey lwcKey : lwcKeysMain.getLwcKeys()) {
            String keyDisplayName = lwcKey.getDisplayName();
            List<String> keyLore = lwcKey.getLore();
            Material keyMaterial = lwcKey.getMaterial();
            Map<Enchantment, Integer> keysEnchantments = lwcKey.getEnchantments();
            boolean displayName;
            boolean lore;
            boolean material;
            boolean enchants;
            
            if (itemDisplayName == null && keyDisplayName == null) {
                displayName = true;
            } else {
                if (itemDisplayName != null && keyDisplayName != null) {
                    displayName = itemDisplayName.equals(keyDisplayName);
                } else {
                    continue;
                }
            }
            if (!displayName) {
                continue;
            }
            
            if (itemLore == null && keyDisplayName == null) {
                lore = true;
            } else {
                if (itemLore != null && keyDisplayName != null) {
                    lore = itemLore.equals(keyLore);
                } else {
                    continue;
                }
            }
            if (!lore) {
                continue;
            }
            
            if (itemMaterial == null && keyDisplayName == null) {
                material = true;
            } else {
                if (itemMaterial != null && keyDisplayName != null) {
                    material = itemMaterial.equals(keyMaterial);
                } else {
                    continue;
                }
            }
            if (!material) {
                continue;
            }
            
            if (itemEnchants == null && keysEnchantments == null) {
                enchants = true;
            } else {
                if (itemEnchants != null && keysEnchantments != null) {
                    enchants = itemEnchants.equals(keysEnchantments);
                } else {
                    continue;
                }
            }
            if (!enchants) {
                continue;
            }
            return lwcKey;
        }
        return null;
    }
}
