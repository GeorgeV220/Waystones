package com.georgev22.waystones.utilities.waystones;

import com.georgev22.waystones.utilities.ObjectMap;
import org.bukkit.Location;

import java.util.Map;

public class Waystone extends ObjectMap<String, Object> {

    private final String name;

    /**
     * Creates an Waystone instance.
     *
     * @param name Waystone name
     */
    public Waystone(String name) {
        this.name = name;
        put("name", name);
    }

    /**
     * Creates a Waystone instance initialized with the given map.
     * <p>
     * You must add name to your map
     *
     * @param name Waystone name
     * @param map  initial map
     * @see Waystone#Waystone(String)
     */
    public Waystone(String name, final Map<String, Object> map) {
        super(map);
        this.name = name;
    }

    /**
     * Returns Waystone name
     *
     * @return Waystone name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns Waystone Location
     *
     * @return Waystone Location
     */
    public Location getLocation() {
        return getLocation("location");
    }

}
