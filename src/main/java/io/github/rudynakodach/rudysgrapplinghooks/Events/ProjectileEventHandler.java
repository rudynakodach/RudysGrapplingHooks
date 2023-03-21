package io.github.rudynakodach.rudysgrapplinghooks.Events;

import io.github.rudynakodach.rudysgrapplinghooks.Modules.BowHookUsage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ProjectileEventHandler implements Listener {

    private final JavaPlugin plugin;
    public ProjectileEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private HashMap<UUID, BowHookUsage> arrowMap = new HashMap<>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {

        //not an arrow
        if (e.getEntityType() != EntityType.ARROW) {
            return;
        }

        Arrow arrow = (Arrow) e.getEntity();

        //not shot by a player
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) arrow.getShooter();

        //if the projectile source is NOT a bow, return
        if(!(shooter.getInventory().getItemInMainHand().getType() == Material.BOW)) {
            return;
        }

        NamespacedKey bowHookKey = new NamespacedKey(plugin, "isbowhook");
        //doesn't contain the namespaced key used by bow hooks
        if(!shooter.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(bowHookKey)) {
            return;
        }

        Bat bat = (Bat) e.getEntity().getWorld().spawnEntity(new Location(e.getEntity().getWorld(), 0,0,0), EntityType.BAT);
        LeashHitch rope = shooter.getWorld().spawn(new Location(e.getEntity().getWorld(), 0,0,0), LeashHitch.class);

        arrow.addPassenger(bat);
        arrowMap.put(arrow.getUniqueId(), new BowHookUsage(
                bat,
                rope,
                arrow,
                shooter
        ));
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent e) {
        if(!arrowMap.containsKey(e.getEntity().getUniqueId())) {
            return;
        }
        Arrow arrow = (Arrow) e.getEntity();
        Player shooter = (Player)arrow.getShooter();
        List<Entity> passengers = arrow.getPassengers();

        BowHookUsage usage = arrowMap.get(e.getEntity().getUniqueId());
        usage.removeLeash();
        usage.removeBat();
        usage.removeArrow();

        Location arrowLocation = arrow.getLocation();
        Location playerLocation = shooter.getLocation();
        Vector direction = arrowLocation.toVector().subtract(playerLocation.toVector()).normalize();
        double speed = 1.75; // Set the speed at which the player is pulled towards the hook

        shooter.setVelocity(direction.multiply(speed));
    }
}
