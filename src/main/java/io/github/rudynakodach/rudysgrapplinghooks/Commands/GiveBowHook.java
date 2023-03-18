package io.github.rudynakodach.rudysgrapplinghooks.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class GiveBowHook implements CommandExecutor {
    final JavaPlugin plugin;
    public GiveBowHook(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("givebh")) {

            ItemStack bowItem = new ItemStack(Material.BOW, 1);
            ItemMeta bowMeta = bowItem.getItemMeta();

            bowMeta.setCustomModelData(10);
            bowMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&l&cBow&eHook"));

            bowItem.setItemMeta(bowMeta);

            NamespacedKey bowHookKey = new NamespacedKey(plugin, "isbowhook");
            bowMeta.getPersistentDataContainer().set(bowHookKey, PersistentDataType.INTEGER, 1);

            bowItem.setItemMeta(bowMeta);

            Player player = (Player) sender;
            player.getInventory().addItem(bowItem);

            return true;
        }
        return false;
    }
}
