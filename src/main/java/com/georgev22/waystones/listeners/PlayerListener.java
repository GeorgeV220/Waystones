package com.georgev22.waystones.listeners;

import com.georgev22.waystones.Waystones;
import com.georgev22.waystones.managers.GeneratorManager;
import com.georgev22.waystones.managers.InventoryManager;
import com.georgev22.waystones.utilities.MessagesUtil;
import com.georgev22.waystones.utilities.ObjectMap;
import com.georgev22.waystones.utilities.Utils;
import com.georgev22.waystones.utilities.waystones.PriceType;
import com.georgev22.waystones.utilities.waystones.Waystone;
import com.georgev22.waystones.utilities.waystones.WaystoneManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class PlayerListener implements Listener {

    private final Waystones waystonePlugin = Waystones.getInstance();

    @EventHandler
    public void onBlockInteraction(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        ItemStack itemStack = event.getItem();
        if (!itemStack.hasItemMeta()) {
            return;
        }

        ObjectMap<String, String> objectMap = ObjectMap.newObjectMap();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.getDisplayName().equalsIgnoreCase(Utils.colorize("&8» &e&lWaystone &8«"))) {
            for (String s : itemMeta.getLore()) {
                String[] b = s.split(": ");
                objectMap.append(ChatColor.stripColor(b[0]), ChatColor.stripColor(b[1]));
            }

            WaystoneManager waystoneManager = WaystoneManager.getWaystone(objectMap.getString("Name"));
            if (waystoneManager.setupWaystone(PriceType.valueOf(objectMap.get("Type")), Double.parseDouble(objectMap.get("Price")), event.getClickedBlock().getLocation().add(0, 1, 0), Boolean.parseBoolean(objectMap.get("Global")))) {
                event.getClickedBlock().getRelative(BlockFace.UP).setType(itemStack.getType());
                MessagesUtil.WAYSTONE_CREATE.msg(event.getPlayer(), ObjectMap.newObjectMap().append("%name%", objectMap.getString("Name")).append("%location%", event.getClickedBlock().getLocation().toString()), true);
            } else {
                MessagesUtil.WAYSTONE_EXISTS.msg(event.getPlayer(), ObjectMap.newObjectMap().append("%name%", objectMap.getString("Name")), true);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockInteraction2(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) | event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Location blockLocation = event.getClickedBlock().getLocation();
            for (Map.Entry<String, Waystone> entry : WaystoneManager.getWaystoneMap().entrySet()) {
                WaystoneManager waystoneManager = WaystoneManager.getWaystone(entry.getKey());
                if (blockLocation.equals(waystoneManager.getWaystone().getLocation())) {
                    if (event.getPlayer().isSneaking() && event.getPlayer().hasPermission("waystones.destroy")) {
                        waystoneManager.delete();
                        return;
                    }
                    new InventoryManager().inventory(event.getPlayer(), waystoneManager.getWaystone().getName());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        GeneratorManager.getObjectMap().remove(event.getPlayer());
    }

}
