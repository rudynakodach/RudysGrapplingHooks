package io.github.rudynakodach.rudysgrapplinghooks.Modules;

import org.bukkit.entity.*;

public class BowHookUsage {

    private final Bat bat;
    private final Arrow arrow;
    private final LeashHitch leash;

    public BowHookUsage(Bat bat, LeashHitch leash, Arrow arrow, Player player) {
        this.bat = bat;
        this.arrow = arrow;
        this.leash = leash;

        leash.setInvulnerable(true);
        leash.teleport(player.getLocation());

        bat.setCollidable(false);
        bat.setAI(false);
        bat.setGravity(false);
        bat.setAwake(false);
        bat.setInvulnerable(true);
        bat.setInvisible(true);
        bat.setAware(false);

        arrow.addPassenger(bat);
        bat.setLeashHolder(player);
    }

    public void removeLeash() {
        leash.remove();
    }

    public void removeBat() {
        bat.remove();
    }

    public void removeArrow() {
        arrow.remove();
    }
}
