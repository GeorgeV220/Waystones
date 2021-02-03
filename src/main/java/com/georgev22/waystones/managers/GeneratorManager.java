package com.georgev22.waystones.managers;

import com.georgev22.waystones.Waystones;
import com.georgev22.waystones.utilities.LocationUtils;
import com.georgev22.waystones.utilities.ObjectMap;
import com.georgev22.waystones.utilities.Options;
import com.georgev22.waystones.utilities.Utils;
import com.georgev22.waystones.utilities.waystones.PriceType;
import com.georgev22.waystones.utilities.waystones.WaystoneManager;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.georgev22.waystones.utilities.Utils.debug;
import static com.georgev22.waystones.utilities.Utils.Circle;

public class GeneratorManager extends BukkitRunnable {

    private static final ObjectMap<Player, Long> objectMap = new ObjectMap<>();

    public static ObjectMap<Player, Long> getObjectMap() {
        return objectMap;
    }

    private final int minutes;

    public GeneratorManager(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!objectMap.containsKey(player)) {
                objectMap.append(player, System.currentTimeMillis());
            }
        }
        for (Map.Entry<Player, Long> entry : objectMap.entrySet()) {
            Player player = entry.getKey();
            Long reminderTimer = entry.getValue();
            if (System.currentTimeMillis() >= reminderTimer) {
                List<Location> locationList = Lists.newArrayList();
                WaystoneManager.getWaystoneMap().forEach((s, waystone) -> locationList.add(waystone.getLocation()));

                int i = 0;

                for (Location location : locationList) {
                    Circle circle1 = new Circle(location.getBlockX(), location.getBlockZ(), 500);
                    Circle circle2 = new Circle(player.getLocation().getBlockX(), player.getLocation().getBlockZ(), 500);
                    if (Utils.areTheseCirclesTouchEachOtherOrIntersect(circle1, circle2)) {
                        i = 1;
                        break;
                    } else {
                        if (Options.DEBUG_USELESS_INFO.isEnabled())
                            debug(Waystones.getInstance(),
                                    "The circles with coordinates:\n" +
                                            " A:\n" +
                                            "  X: " + circle1.getX() + "\n" +
                                            "  Z: " + circle1.getZ() + "\n" +
                                            "  R: " + circle1.getR() + "\n" +
                                            " B:\n" +
                                            "  X: " + circle2.getX() + "\n" +
                                            "  Z: " + circle2.getZ() + "\n" +
                                            "  R: " + circle2.getR() + "\n" +
                                            "do not intersect");
                    }
                }

                if (i == 0) {
                    Location location2 = LocationUtils.randomLocationInCircle(player.getLocation(), 500);
                    location2.setX(location2.getBlockX());
                    location2.setZ(location2.getBlockZ());
                    location2.setY(location2.getWorld().getHighestBlockYAt(location2.getBlockX(), location2.getBlockZ()));
                    WaystoneManager
                            .getWaystone("waystone" + Utils.generateAlphaNumericString(6))
                            .setupWaystone(PriceType.EXP, 100, location2, false);
                }
            }

            objectMap.replace(player, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutes));
        }
    }
}
