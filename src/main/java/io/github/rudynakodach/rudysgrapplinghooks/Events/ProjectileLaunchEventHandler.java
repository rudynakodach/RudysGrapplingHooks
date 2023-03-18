package io.github.rudynakodach.rudysgrapplinghooks.Events;

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

public class ProjectileLaunchEventHandler implements Listener {

    private final JavaPlugin plugin;
    public ProjectileLaunchEventHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private HashMap<UUID, Entity> arrowMap = new HashMap<>();

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

        Bat bat = (Bat) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.BAT);
        bat.setAwake(false);
        bat.setGravity(false);
        bat.setAI(false);
        bat.setCollidable(false);
        bat.setInvulnerable(true);
        bat.setInvisible(true);
        bat.setAware(false);

        LeashHitch rope = shooter.getLocation().getWorld().spawn(shooter.getLocation(), LeashHitch.class);
        rope.setInvulnerable(true);
        bat.setLeashHolder(shooter);

        arrow.addPassenger(bat);
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent e) {
        if(!(e.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) e.getEntity();

        // the shooter was not a player
        if(!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player)arrow.getShooter();
        List<Entity> passengers = arrow.getPassengers();

        //no passengers on this arrow
        if(passengers.size() == 0) {
            return;
        }

        boolean hasBatPassengers = false;
        for (Entity entity : passengers) {
            if(!(entity instanceof Bat)) {
                continue;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            if(livingEntity.isLeashed()) {
                if(livingEntity.getLeashHolder() instanceof LeashHitch) {
                    livingEntity.getLeashHolder().remove();
                }
                hasBatPassengers = true;
                livingEntity.remove();
                break;
            }
        }

        //no bat passengers found - not a valid arrow
        if(!hasBatPassengers) {return;}

        Location arrowLocation = arrow.getLocation();
        Location playerLocation = shooter.getLocation();
        Vector direction = arrowLocation.toVector().subtract(playerLocation.toVector()).normalize();
        double speed = 1.75; // Set the speed at which the player is pulled towards the hook

        shooter.setVelocity(direction.multiply(speed));
    }
}
