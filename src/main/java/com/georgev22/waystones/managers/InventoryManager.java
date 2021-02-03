package com.georgev22.waystones.managers;

import com.cryptomorin.xseries.XMaterial;
import com.georgev22.waystones.Waystones;
import com.georgev22.waystones.utilities.ObjectMap;
import com.georgev22.waystones.utilities.Options;
import com.georgev22.waystones.utilities.Utils;
import com.georgev22.waystones.utilities.waystones.Waystone;
import com.georgev22.waystones.utilities.waystones.WaystoneManager;
import com.google.common.collect.Lists;
import com.tchristofferson.pagedinventories.IPagedInventory;
import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryClickHandler;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCustomNavigationHandler;
import com.tchristofferson.pagedinventories.navigationitems.CloseNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.CustomNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NextNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.PreviousNavigationItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class InventoryManager {

    public void inventory(Player player, String inventoryName) {
        CustomNavigationItem navigationItem = new CustomNavigationItem(new ItemStack(Material.BOOK), 0) {
            @Override
            public void handleClick(PagedInventoryCustomNavigationHandler handler) {
            }
        };

        NextNavigationItem nextNavigationItem = new NextNavigationItem(new ItemStack(Material.PAPER));

        PreviousNavigationItem previousNavigationItem = new PreviousNavigationItem(new ItemStack(Material.ARROW));

        CloseNavigationItem closeNavigationItem = new CloseNavigationItem(new ItemStack(Material.ANVIL));

        NavigationRow navigationRow = new NavigationRow(nextNavigationItem, previousNavigationItem, closeNavigationItem, navigationItem);
        IPagedInventory pagedInventory = Waystones.getInstance().getInventoryAPI().createPagedInventory(navigationRow);
        List<Inventory> inventoryList = Lists.newArrayList();
        Inventory inventory = null;
        int i = 0;
        for (Map.Entry<String, Waystone> entry : WaystoneManager.getWaystoneMap().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(inventoryName)) {
                continue;
            }
            if (inventoryList.isEmpty() | inventory == null) {
                inventory = Bukkit.createInventory(null, 54, "Waystones " + inventoryName);
                inventoryList.add(inventory);
            }
            if (i > 45) {
                i = 0;
                inventory = Bukkit.createInventory(null, 54, "Waystones " + inventoryName);
                inventoryList.add(inventory);
            }
            ItemStack itemStack = new ItemStack(XMaterial.matchXMaterial(entry.getValue().getLocation().getBlock().getType()).parseMaterial());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(
                    Utils.colorize(
                            Utils.placeHolder(
                                    String.valueOf(Options.WAYSTONE_DISPLAY_NAME.getValue()),
                                    ObjectMap.newObjectMap()
                                            .append("%name%", entry.getKey()),
                                    true)));
            Location location = entry.getValue().getLocation();
            itemMeta.setLore(
                    Utils.colorize(
                            Utils.placeHolder(
                                    Options.WAYSTONES_LORES.getStringList(),
                                    ObjectMap.newObjectMap()
                                            .append("%x%", String.valueOf(location.getX()))
                                            .append("%y%", String.valueOf(location.getY()))
                                            .append("%z%", String.valueOf(location.getZ()))
                                            .append("%type%", entry.getValue().getString("priceType"))
                                            .append("%price%", String.valueOf(entry.getValue().getDouble("price"))),
                                    true)));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
            i++;
        }
        inventoryList.forEach(pagedInventory::addPage);
        pagedInventory.open(player);
        pagedInventory.addHandler(new PagedInventoryClickHandler() {
            @Override
            public void handle(PagedInventoryClickHandler.Handler handler) {
                ItemStack itemStack = handler.getCurrentItem();
                if (itemStack == null | itemStack.getType().equals(Material.AIR)) {
                    return;
                }
                if (!itemStack.hasItemMeta()) {
                    return;
                }
                WaystoneManager waystoneManager = WaystoneManager.getWaystone(Utils.uncolorize(itemStack.getItemMeta().getDisplayName()));
                if (!waystoneManager.waystoneExists()) {
                    return;
                }
                player.closeInventory();
                Location location = waystoneManager.getWaystone().getLocation().clone();
                player.teleport(location.add(0.5, 1, 0.5));
            }
        });
    }
}
