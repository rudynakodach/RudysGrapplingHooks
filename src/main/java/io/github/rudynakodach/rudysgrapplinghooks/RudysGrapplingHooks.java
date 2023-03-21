package io.github.rudynakodach.rudysgrapplinghooks;

import io.github.rudynakodach.rudysgrapplinghooks.Commands.GiveBowHook;
import io.github.rudynakodach.rudysgrapplinghooks.Commands.GiveGrapplingHook;
import io.github.rudynakodach.rudysgrapplinghooks.Events.PlayerFishEventHandler;
import io.github.rudynakodach.rudysgrapplinghooks.Events.ProjectileEventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RudysGrapplingHooks extends JavaPlugin {

    // Plugin startup logic
    @Override
    public void onEnable() {

        //save default configuration
        saveDefaultConfig();

        //register events and command handlers
        GiveGrapplingHook giveGrapplingHookHandler = new GiveGrapplingHook(this);
        Objects.requireNonNull(getCommand("givegh")).setExecutor(giveGrapplingHookHandler);

        GiveBowHook giveBowHookHandler = new GiveBowHook(this);
        Objects.requireNonNull(getCommand("givebh")).setExecutor(giveBowHookHandler);

        PlayerFishEventHandler playerFishEventHandler = new PlayerFishEventHandler(this);
        getServer().getPluginManager().registerEvents(playerFishEventHandler, this);

        ProjectileEventHandler projectileEventHandler = new ProjectileEventHandler(this);
        getServer().getPluginManager().registerEvents(projectileEventHandler, this);
    }

    // Plugin shutdown logic
    @Override
    public void onDisable() {}
}
