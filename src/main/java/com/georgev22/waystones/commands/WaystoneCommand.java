package com.georgev22.waystones.commands;

import com.cryptomorin.xseries.XMaterial;
import com.georgev22.waystones.managers.InventoryManager;
import com.georgev22.waystones.utilities.MessagesUtil;
import com.georgev22.waystones.utilities.ObjectMap;
import com.georgev22.waystones.utilities.Utils;
import com.georgev22.waystones.utilities.waystones.PriceType;
import com.georgev22.waystones.utilities.waystones.WaystoneManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class WaystoneCommand extends BukkitCommand {

    ObjectMap<String, String> placeholders = ObjectMap.newObjectMap();

    public WaystoneCommand() {
        super("waystone");
        this.description = "Waystone command";
        this.usageMessage = "/waystone";
        this.setAliases(Arrays.asList("ws", "ways", "wstone", "waystones"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            MessagesUtil.ONLY_PLAYER_COMMAND.msg(sender);
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            new InventoryManager().inventory(player, "All waystones");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("waystone.create")) {
                MessagesUtil.NO_PERMISSION.msg(player);
                return true;
            }

            if (args.length < 5) {
                Utils.msg(player, "&c&l(!) &c/waystones create <name> <price type> <price> <global> <block>");
                return true;
            }

            if (WaystoneManager.getWaystone(args[1]).waystoneExists()) {
                placeholders.append("%name%", args[1]);
                MessagesUtil.WAYSTONE_EXISTS.msg(player, placeholders, true);
                return true;
            }
            if (Arrays.stream(PriceType.values()).noneMatch(b -> b.name().equalsIgnoreCase(args[2]))) {
                placeholders.append("%type%", args[2]);
                MessagesUtil.WAYSTONE_COMMAND_TYPE_NOT_FOUND.msg(player, placeholders, true);
                return true;
            }
            if (!args[4].equals("true") || !args[4].equals("false")) {
                MessagesUtil.WAYSTONE_COMMAND_BOOLEAN.msg(player);
                return true;
            }
            ItemStack itemStack = new ItemStack(Objects.requireNonNull(XMaterial.matchXMaterial(args[5]).get().parseMaterial()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Utils.colorize("&8» &e&lWaystone &8«"));
            itemMeta.setLore(Utils.colorize(Arrays.asList("&dName: " + args[1], "&dType: " + args[2], "&dPrice: " + args[3], "&dGlobal: " + args[4])));
            itemStack.setItemMeta(itemMeta);
            itemStack.setAmount(1);
            player.getInventory().addItem(itemStack);
            MessagesUtil.WAYSTONE_COMMAND_CREATE_BLOCK.msg(player);
        }

        return true;
    }
}
