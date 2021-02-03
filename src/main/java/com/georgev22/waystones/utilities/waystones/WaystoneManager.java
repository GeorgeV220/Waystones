package com.georgev22.waystones.utilities.waystones;

import com.georgev22.waystones.Waystones;
import com.georgev22.waystones.utilities.ObjectMap;
import com.georgev22.waystones.utilities.Options;
import com.georgev22.waystones.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class WaystoneManager {

    private static final ObjectMap<String, Waystone> waystoneMap = new ObjectMap<>();

    public static ObjectMap<String, Waystone> getWaystoneMap() {
        return waystoneMap;
    }

    public static void loadAllWaystones() {
        File[] files = new File(waystonePlugin.getDataFolder(), "waystones").listFiles((dir, name) -> !name.equals(".DS_Store"));

        if (files == null) {
            return;
        }

        for (File file : files) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (cfg.get("location") == null)
                continue;

            WaystoneManager waystoneManager = WaystoneManager.getWaystone(file.getName().replace(".yml", ""));
            waystoneManager.loadWaystone();
        }
    }

    /**
     * Returns a copy of this WaystoneManager class for a specific waystone.
     *
     * @param name Waystone name.
     * @return a copy of this WaystoneManager class for a specific waystone.
     */
    public static WaystoneManager getWaystone(String name) {
        if (waystoneMap.get(name) == null) {
            waystoneMap.append(name, new Waystone(name));
        }
        return new WaystoneManager(waystoneMap.get(name));
    }

    public static ObjectMap<Waystone, Location> getAllWaystones() {
        return null;
    }

    private final Waystone waystone;
    private final WaystonesData waystonesData;
    private static final Waystones waystonePlugin = Waystones.getInstance();

    private WaystoneManager(Waystone waystone) {
        this.waystone = waystone;
        this.waystonesData = new WaystonesData(waystone.getName());
    }

    public Waystone getWaystone() {
        return waystone;
    }

    public void loadWaystone() {
        waystonesData.setupWaystone();
    }

    public boolean setupWaystone(PriceType type, double price, Location location, boolean global) {
        if (waystoneExists()) {
            if (Options.DEBUG_CREATE.isEnabled()) {
                Utils.debug(waystonePlugin, "Waystone " + waystone.getName() + " already exists!");
            }
            return false;
        }
        return waystonesData.setPriceType(type).setPrice(price).setLocation(location).setupWaystone();
    }

    public boolean waystoneExists() {
        return waystonesData.waystoneExists();
    }

    public void delete() {
        waystonesData.delete();
        waystoneMap.remove(waystone.getName());
    }

    private static class WaystonesData {

        private WaystonesData(final String name) {

            new File(Waystones.getInstance().getDataFolder(),
                    "waystones").mkdirs();

            this.file = new File(Waystones.getInstance().getDataFolder(),
                    "waystones" + File.separator + name + ".yml");

            if (!this.file.exists()) {
                try {
                    if (this.file.createNewFile()) {
                        Bukkit.getLogger().info("DEBUG");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.reloadConfiguration();

            this.waystone = waystoneMap.get(name) == null ? WaystoneManager.getWaystone(name).getWaystone() : waystoneMap.get(name);
        }


        private final Waystone waystone;
        private final File file;
        private YamlConfiguration configuration = null;

        private void reloadConfiguration() {
            this.configuration = YamlConfiguration.loadConfiguration(file);
        }

        /**
         * Save the configuration
         */
        public void saveConfiguration() {
            this.configuration.set("location", waystone.get("location"));
            this.configuration.set("priceType", waystone.getString("priceType"));
            this.configuration.set("price", waystone.getDouble("price"));
            try {
                this.configuration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean setupWaystone() {
            this.configuration.set("name", waystone.getName());
            if (!waystoneExists()) {
                if (waystone.getLocation("location") == null) {
                    Utils.debug(waystonePlugin, "Waystone doesn't exists and is not possible to create a new one without location\n"
                            + "Please use waystone.setLocation(location).setupWaystone();");
                    return false;
                }
                if (waystone.getString("priceType") == null) {
                    Utils.debug(waystonePlugin, "Waystone doesn't exists and is not possible to create a new one without price type\n"
                            + "Please use waystone.setType(type).setupWaystone();");
                    return false;
                }
                if (waystone.getDouble("price") == null) {
                    Utils.debug(waystonePlugin, "Waystone doesn't exists and is not possible to create a new one without price\n"
                            + "Please use waystone.setPrice(price).setupWaystone();");
                    return false;
                }
                if (Options.DEBUG_SAVE.isEnabled())
                    Utils.debug(waystonePlugin,
                            "Creating new Waystone " + waystone);
                configuration.set("location", waystone.getLocation("location"));
                configuration.set("priceType", waystone.getString("priceType"));
                configuration.set("price", waystone.getDouble("price"));
                configuration.set("global", waystone.getBoolean("global"));
                saveConfiguration();
            } else {
                waystone.append("location", configuration.get("location"))
                        .append("priceType", configuration.get("priceType"))
                        .append("price", configuration.getDouble("price"))
                        .append("global", configuration.getBoolean("global"));
                if (Options.DEBUG_LOAD.isEnabled()) {
                    Utils.debug(waystonePlugin,
                            "Waystone " + waystone.getName() + " successfully loaded!");
                }
            }
            return true;
        }

        public void delete() {
            if (file.delete()) {
                Utils.debug(waystonePlugin, "File " + waystone.getName() + ".yml deleted");
            }
        }

        public WaystonesData setLocation(Location location) {
            waystone.append("location", location);
            return this;
        }

        public WaystonesData setPriceType(PriceType type) {
            waystone.append("priceType", type.name());
            return this;
        }

        public WaystonesData setPrice(double price) {
            waystone.append("price", price);
            return this;
        }

        public WaystonesData setGlobal(boolean global) {
            waystone.append("global", global);
            return this;
        }

        public Location getLocation() {
            return waystone.getLocation("location");
        }

        public String getPriceType() {
            return waystone.getString("priceType");
        }

        public double getPrice() {
            return waystone.getDouble("price");
        }

        public boolean isGlobal() {
            return waystone.getBoolean("global");
        }

        public boolean waystoneExists() {
            return configuration.get("location") != null;
        }

    }

}
