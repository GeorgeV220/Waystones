package com.georgev22.waystones.utilities;

import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class LocationUtils {

    public boolean locationIsInRegion(Location firstPoint, Location secondPoint, Location loc) {
        return loc.getWorld().getUID().equals(firstPoint.getWorld().getUID())
                && loc.getX() >= Math.min(firstPoint.getX(), secondPoint.getX())
                && loc.getX() <= Math.max(firstPoint.getX(), secondPoint.getX())
                && loc.getY() >= Math.min(firstPoint.getY(), secondPoint.getY())
                && loc.getY() <= Math.max(firstPoint.getY(), secondPoint.getY())
                && loc.getZ() >= Math.min(firstPoint.getZ(), secondPoint.getZ())
                && loc.getZ() <= Math.max(firstPoint.getZ(), secondPoint.getZ());
    }

    public static boolean isInRadius(Location check, Location start, double radius) {
        return Math.abs(check.getX() - start.getX()) <= radius &&
                Math.abs(check.getY() - start.getY()) <= radius &&
                Math.abs(check.getZ() - start.getZ()) <= radius;
    }

    public static Location getNearest(List<Location> locations, Location center, Double range) {
        Location closest = null;
        for (Location loc : locations) {
            if (closest == null)
                closest = loc;
            else if (loc.distanceSquared(center) < closest.distanceSquared(center))
                closest = loc;
        }
        return closest;
    }


    public static Location randomLocationInCircle(Location centre, double radius) {
        Random rand = new Random(); //Get random
        double x = radius * 2 * (rand.nextFloat() - 0.5);
        double z = Math.sqrt(radius * radius - x * x);
        return centre.clone().add(x, 0, z);
    }

}