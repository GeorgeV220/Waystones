package com.georgev22.waystones;

import com.georgev22.waystones.commands.WaystoneCommand;
import com.georgev22.waystones.configmanager.FileManager;
import com.georgev22.waystones.listeners.PlayerListener;
import com.georgev22.waystones.managers.GeneratorManager;
import com.georgev22.waystones.utilities.MessagesUtil;
import com.georgev22.waystones.utilities.Options;
import com.georgev22.waystones.utilities.waystones.WaystoneManager;
import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static com.georgev22.waystones.utilities.Utils.*;

import java.lang.reflect.Field;
import java.util.HashMap;

public final class Waystones extends JavaPlugin {

    private static Waystones instance = null;

    private PagedInventoryAPI api;

    public static Waystones getInstance() {
        return instance == null ? instance = Waystones.getPlugin(Waystones.class) : instance;
    }

    private FileManager fileManager;

    @Override
    public void onEnable() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

        fileManager = FileManager.getInstance();
        fileManager.loadFiles(this);
        api = new PagedInventoryAPI(this);
        MessagesUtil.repairPaths(fileManager.getMessages());

        registerListeners(new PlayerListener());

        registerCommand("waystone", new WaystoneCommand());

        WaystoneManager.loadAllWaystones();

        if (Options.WAYSTONE_GENERATE.isEnabled()) {
            if (!isLegacy()) {
                GeneratorManager generatorManager = new GeneratorManager(2);
                generatorManager.runTaskTimerAsynchronously(this, 60, 60);
            } else {
                debug(this, "Waystone generation disabled. This feature only works on 1.13+");
            }
        }
    }

    @Override
    public void onDisable() {
        unRegisterCommand("waystone");
        Bukkit.getScheduler().getActiveWorkers().forEach(bukkitWorker -> Bukkit.getScheduler().cancelTask(bukkitWorker.getTaskId()));
    }

    /**
     * Register listeners
     *
     * @param listeners Class that implements Listener interface
     */
    private void registerListeners(Listener... listeners) {
        final PluginManager pm = Bukkit.getPluginManager();
        for (final Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }
    }

    /**
     * Register a command given an executor and a name.
     *
     * @param commandName The name of the command
     * @param command     The class that extends the BukkitCommand class
     */
    private void registerCommand(final String commandName, final Command command) {
        try {
            Field field = getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            Object result = field.get(getServer().getPluginManager());
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            commandMap.register(commandName, command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * unregister a command
     *
     * @param commandName The name of the command
     */
    private void unRegisterCommand(String commandName) {
        try {
            Field field1 = getServer().getPluginManager().getClass().getDeclaredField("commandMap");
            field1.setAccessible(true);
            Object result = field1.get(getServer().getPluginManager());
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Field field = isLegacy() ? commandMap.getClass().getDeclaredField("knownCommands") : commandMap.getClass().getSuperclass().getDeclaredField("knownCommands");
            field.setAccessible(true);
            Object map = field.get(commandMap);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            Command command = commandMap.getCommand(commandName);
            knownCommands.remove(command.getName());
            for (String alias : command.getAliases()) {
                knownCommands.remove(alias);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileManager getFileManager() {
        return fileManager;
    }


    public PagedInventoryAPI getInventoryAPI() {
        return api;
    }

    @Override
    @NotNull
    public FileConfiguration getConfig() {
        return fileManager.getConfig().getFileConfiguration();
    }
}
