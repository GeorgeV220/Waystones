package com.georgev22.waystones.utilities;

import com.georgev22.waystones.configmanager.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Options {

    DEBUG_LOAD("debug.load"),

    DEBUG_SAVE("debug.save"),

    DEBUG_CREATE("debug.create"),

    DEBUG_USELESS_INFO("debug.useless info"),

    WAYSTONE_GENERATE("waystone.generate"),

    WAYSTONE_DISPLAY_NAME("waystone.display name"),

    WAYSTONES_LORES("waystone.lores"),

    ;
    private final String pathName;

    Options(final String pathName) {
        this.pathName = pathName;
    }

    public boolean isEnabled() {
        final FileConfiguration file = FileManager.getInstance().getConfig().getFileConfiguration();
        return file.getBoolean("Options." + this.pathName, true);
    }

    public Object getValue() {
        final FileConfiguration file = FileManager.getInstance().getConfig().getFileConfiguration();
        return file.get("Options." + this.pathName, 0);
    }

    public List<String> getStringList() {
        final FileConfiguration file = FileManager.getInstance().getConfig().getFileConfiguration();
        return file.getStringList("Options." + this.pathName);
    }
}
