package de.articdive.lwckeys.commands;

import de.articdive.enum_to_yaml.EnumConfiguration;
import de.articdive.lwckeys.LWCKeys;
import de.articdive.lwckeys.configuration.KeysConfiguration;
import de.articdive.lwckeys.objects.LWCKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LWCKeysCommand implements CommandExecutor, TabExecutor {
    private LWCKeys lwcKeysMain = LWCKeys.getPlugin(LWCKeys.class);
    private EnumConfiguration keysConfiguration = lwcKeysMain.getKeysConfiguration();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("lwckeys")) {
            if (args.length == 0) {
                sender.sendMessage(new String[]{
                        ChatColor.YELLOW + "=====   " + ChatColor.GOLD + "LWCKeys" + ChatColor.YELLOW + "   =====",
                        ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " give [Key] [Player] [Amount]",
                        ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " list",
                });
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (sender.hasPermission("lwckeys.list")) {
                        List<String> output = new ArrayList<>();
                        output.add(ChatColor.YELLOW + "=====   " + ChatColor.GOLD + "List of LWCKeys" + ChatColor.YELLOW + "   =====");
                        for (LWCKey lwcKey : lwcKeysMain.getLwcKeys()) {
                            output.add(lwcKey.getName());
                        }
                        sender.sendMessage(output.toArray(new String[0]));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NO_PERMISSION_CMD_MESSAGE)));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("lwckeys.give")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.SPECIFY_KEY_MESSAGE)));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NO_PERMISSION_CMD_MESSAGE)));
                        return true;
                    }
                } else {
                    sender.sendMessage(new String[]{
                            ChatColor.YELLOW + "=====   " + ChatColor.GOLD + "LWCKeys" + ChatColor.YELLOW + "   =====",
                            ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " give [Key] [Player] [Amount]",
                            ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " list",
                    });
                    return true;
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("lwckeys.give")) {
                        for (LWCKey lwcKey : lwcKeysMain.getLwcKeys()) {
                            if (lwcKey.getName().equalsIgnoreCase(args[1])) {
                                if (sender.hasPermission("lwckeys.give.*") || sender.hasPermission("lwckeys.give." + lwcKey.getName())) {
                                    if (sender instanceof Player) {
                                        Player player = (Player) sender;
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.GIVE_KEY_MESSAGE)
                                                .replaceAll(Pattern.quote("{player}"), "yourself")
                                                .replaceAll(Pattern.quote("{amount}"), "1")
                                                .replaceAll(Pattern.quote("{keyname}"), lwcKey.getName())));
                                        player.getInventory().addItem(LWCKey.createItemStack(lwcKey, 1));
                                        return true;
                                    } else {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.SPECIFY_PLAYER_MESSAGE)));
                                        return true;
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NO_PERMISSION_CMD_MESSAGE)));
                                    return true;
                                }
                            }
                        }
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.SPECIFY_VALID_KEY_MESSAGE).replaceAll(Pattern.quote("{input}"), args[1])));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NO_PERMISSION_CMD_MESSAGE)));
                        return true;
                    }
                } else {
                    sender.sendMessage(new String[]{
                            ChatColor.YELLOW + "=====   " + ChatColor.GOLD + "LWCKeys" + ChatColor.YELLOW + "   =====",
                            ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " give [Key] [Player] [Amount]",
                            ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " list",
                    });
                    return true;
                }
            } else {
                if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("lwckeys.give")) {
                        for (LWCKey lwcKey : lwcKeysMain.getLwcKeys()) {
                            if (lwcKey.getName().equalsIgnoreCase(args[1])) {
                                if (sender.hasPermission("lwckeys.give.*") || sender.hasPermission("lwckeys.give." + lwcKey.getName())) {
                                    Player receiver = Bukkit.getPlayer(args[2]);
                                    if (receiver == null) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.PLAYER_NOT_FOUND_MESSAGE)).replaceAll(Pattern.quote("{input}"), args[2]));
                                        return true;
                                    }
                                    int amount = 1;
                                    if (args.length == 4) {
                                        try {
                                            amount = Integer.parseInt(args[3]);
                                        } catch (NumberFormatException nfe) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NOT_A_VALID_INTEGER_MESSAGE)).replaceAll(Pattern.quote("{input}"), args[3]));
                                            return true;
                                        }
                                    }
                                    receiver.getInventory().addItem(LWCKey.createItemStack(lwcKey, amount));
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.GIVE_KEY_MESSAGE)
                                            .replaceAll(Pattern.quote("{player}"), receiver.getName())
                                            .replaceAll(Pattern.quote("{amount}"), Integer.toString(amount))
                                            .replaceAll(Pattern.quote("{keyname}"), lwcKey.getName())));
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NO_PERMISSION_CMD_MESSAGE)));
                                    return true;
                                }
                            }
                        }
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.SPECIFY_VALID_KEY_MESSAGE).replaceAll(Pattern.quote("{input}"), args[1])));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', keysConfiguration.getString(KeysConfiguration.NO_PERMISSION_CMD_MESSAGE)));
                        return true;
                    }
                } else {
                    sender.sendMessage(new String[]{
                            ChatColor.YELLOW + "=====   " + ChatColor.GOLD + "LWCKeys" + ChatColor.YELLOW + "   =====",
                            ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " give [Key] [Player] [Amount]",
                            ChatColor.YELLOW + "/" + label + ChatColor.GREEN + " list",
                    });
                    return true;
                }
            }
        } else {
            return true;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
