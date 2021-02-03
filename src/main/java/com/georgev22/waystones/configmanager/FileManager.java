package com.georgev22.waystones.configmanager;

import org.bukkit.plugin.java.JavaPlugin;

public final class FileManager {

    private static FileManager instance;

    public static FileManager getInstance() {
        return instance == null ? instance = new FileManager() : instance;
    }

    private CFG config;
    private CFG messages;

    private FileManager() {
    }

    public void loadFiles(final JavaPlugin plugin) {
        this.messages = new CFG(plugin, "messages", false);
        this.config = new CFG(plugin, "config", true);
    }

    public CFG getMessages() {
        return messages;
    }

    public CFG getConfig() {
        return config;
    }
}
