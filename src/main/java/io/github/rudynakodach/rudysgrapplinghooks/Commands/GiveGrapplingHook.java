package io.github.rudynakodach.rudysgrapplinghooks.Commands;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class GiveGrapplingHook implements CommandExecutor {
    JavaPlugin plugin;
    public GiveGrapplingHook(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    //command handler — granting grappling hooks
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("givegh")) {
            if(args.length < 1) {
                return false;
            }

            String tier = args[0];
            //check if hook of provided tier exists
            if(!plugin.getConfig().contains("hooks." + tier)) {
                commandSender.sendMessage("Grappling hook of tier \"" + tier + "\" does not exist.");
                return true;
            }

            //get hook's data
            ItemStack grappleStack = new ItemStack(Material.FISHING_ROD, 1);
            ItemMeta meta = grappleStack.getItemMeta();
            NamespacedKey grappleKey = new NamespacedKey(plugin, "isgrapple");

            meta.setCustomModelData(Integer.parseInt(tier));
            int maxDurability = plugin.getConfig().getInt("hooks." + tier + ".durability");

            //set hook's meta
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.getPersistentDataContainer().set(grappleKey, PersistentDataType.INTEGER, maxDurability);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lGrappler &r&7T" + tier));
            meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', "&c" + maxDurability + " &rusages left.")));

            grappleStack.setItemMeta(meta);

            Player player = (Player) commandSender;
            player.getInventory().addItem(grappleStack);

            commandSender.sendMessage("Grapple given.");
            return true;
        }
        return false;
    }
}
