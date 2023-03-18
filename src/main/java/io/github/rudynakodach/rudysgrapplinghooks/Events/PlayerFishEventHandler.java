package io.github.rudynakodach.rudysgrapplinghooks.Events;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.github.rudynakodach.rudysgrapplinghooks.Modules.GrapplingHookUsage;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PlayerFishEventHandler implements Listener {

    JavaPlugin plugin;
    HashMap<String, GrapplingHookUsage> delayMap = new HashMap<>();

    public PlayerFishEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        //get item's data
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey grappleKey = new NamespacedKey(plugin, "isgrapple");

        if(!dataContainer.has(grappleKey)) {
            return;
        }

        //pull the player towards the fishing bobber
        if(e.getState() == PlayerFishEvent.State.REEL_IN) {

            int grappleTier = meta.getCustomModelData();
            int delay = plugin.getConfig().getInt("hooks." + grappleTier + ".delay");

            //check if the player is still on a cooldown
            //if the user is not on the delay map, add them and allow te use of the hook
            if(!delayMap.containsKey(e.getPlayer().getName())) {
                delayMap.put(e.getPlayer().getName(), new GrapplingHookUsage(delay, System.currentTimeMillis()));
            } else {
                long oldUseTime = delayMap.get(e.getPlayer().getName()).getUseTime();
                int oldDelay = delayMap.get(e.getPlayer().getName()).getDelay();
                //still on a cooldown
                if((oldUseTime + oldDelay) - System.currentTimeMillis() > 0) {
                    long timeRemaining = (oldUseTime+oldDelay)-System.currentTimeMillis();
                    long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
                    long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeRemaining);
                    String timeLeft = String.format("%02d:%02d", minutesLeft, secondsLeft);
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes(
                            '&',
                            "&cYou need to wait &l" + timeLeft + "&r&c until you can use the grappling hook again."
                    ));
                    return;
                }
            }

            //"damage" the grappling hook
            int currentDurability = Objects.requireNonNull(dataContainer.get(grappleKey, PersistentDataType.INTEGER));
            meta.getPersistentDataContainer().set(grappleKey, PersistentDataType.INTEGER, currentDurability - 1);

            //update its lore to match the durability remaining
            meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&', "&c" + (currentDurability - 1) + " &rusages left.")));

            //durability ran out
            if(currentDurability - 1 <= 0) {
                e.getPlayer().getInventory().remove(item);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour grappling hook broke."));
            }

            //update item's meta
            item.setItemMeta(meta);

            //now pull the player towards the hook
            Entity bobber = e.getHook();
            Location hookLocation = bobber.getLocation();
            Location playerLocation = e.getPlayer().getLocation();
            Vector direction = hookLocation.toVector().subtract(playerLocation.toVector()).normalize();
            double speed = plugin.getConfig().getDouble("hooks." + meta.getCustomModelData() + ".power"); // Set the speed at which the player is pulled towards the hook

            e.getPlayer().setVelocity(direction.multiply(speed));
            delayMap.put(e.getPlayer().getName(), new GrapplingHookUsage(delay, System.currentTimeMillis()));
        }
    }
}
