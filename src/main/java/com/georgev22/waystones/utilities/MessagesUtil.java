package com.georgev22.waystones.utilities;

import com.georgev22.waystones.configmanager.CFG;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;

public enum MessagesUtil {

    /*
     */

    NO_PERMISSION("Messages.No Permission", "&c&l(!) &cYou do not have the correct permissions to do this!"),

    ONLY_PLAYER_COMMAND("Messages.Only Player Command", "&c&l(!) &cOnly players can run this command!"),

    OFFLINE_PLAYER("Messages.Offline Player", "&c&l(!) &cThis player is offline!"),

    WAYSTONE_CREATE("Messages.Waystone Create", "&a&l(!) Waystone %name% created at %location%"),

    WAYSTONE_COMMAND_CREATE_BLOCK("Messages.Waystone Block Create", "&a&l(!) &aPlace the block to create waystone!"),

    WAYSTONE_COMMAND_BOOLEAN("Messages.Boolean", "&c&l(!) &cPlease use a boolean value (true/false)."),

    WAYSTONE_EXISTS("Messages.Waystone Exists", "&c&l(!) &cWaystone %name% already exists!"),

    WAYSTONE_COMMAND_TYPE_NOT_FOUND("Messages.Type Not Found", "&c&l(!) &cPrice type %type% doesn't exists!"),

    ;

    /**
     * @see #getMessages()
     */
    private String[] messages;
    private final String path;

    MessagesUtil(final String path, final String... messages) {
        this.messages = messages;
        this.path = path;
    }

    /**
     * @return boolean - Whether or not the messages array contains more than 1
     * element. If true, it's more than 1 message/string.
     */
    private boolean isMultiLined() {
        return this.messages.length > 1;
    }

    /**
     * @param cfg CFG instance
     */
    public static void repairPaths(final CFG cfg) {

        boolean changed = false;

        for (MessagesUtil enumMessage : MessagesUtil.values()) {

            /* Does our file contain our path? */
            if (cfg.getFileConfiguration().contains(enumMessage.getPath())) {
                /* It does! Let's set our message to be our path. */
                setPathToMessage(cfg, enumMessage);
                continue;
            }

            /* Since the path doesn't exist, let's set our default message to that path. */
            setMessageToPath(cfg, enumMessage);
            if (!changed) {
                changed = true;
            }

        }
        /* Save the custom yaml file. */
        if (changed) {
            cfg.saveFile();
        }
    }

    /**
     * Sets a message from the MessagesX enum to the file.
     *
     * @param cfg         CFG instance
     * @param enumMessage Message
     */
    private static void setMessageToPath(final CFG cfg, final MessagesUtil enumMessage) {
        /* Is our message multilined? */
        if (enumMessage.isMultiLined()) {
            /* Set our message (array) to the path. */
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages());
        } else {
            /* Set our message (string) to the path. */
            cfg.getFileConfiguration().set(enumMessage.getPath(), enumMessage.getMessages()[0]);
        }
    }

    /**
     * Sets the current MessagesX messages to a string/list retrieved from the
     * messages file.
     *
     * @param cfg         CFG instance
     * @param enumMessage Message
     */
    private static void setPathToMessage(final CFG cfg, final MessagesUtil enumMessage) {
        /* Is our path a list? */
        if (Utils.isList(cfg.getFileConfiguration(), enumMessage.getPath())) {
            /* Set our default message to be the path's message. */
            enumMessage.setMessages(
                    cfg.getFileConfiguration().getStringList(enumMessage.getPath()).toArray(new String[0]));
        } else {
            /* Set our default message to be the path's message. */
            enumMessage.setMessages(cfg.getFileConfiguration().getString(enumMessage.getPath()));
        }
    }

    /**
     * @return the path - The path of the enum in the file.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @return the messages - The messages array that contains all strings.
     */
    public String[] getMessages() {
        return this.messages;
    }

    /**
     * Sets the current messages to a different string array.
     *
     * @param messages The messages array that contains all strings.
     */
    public void setMessages(final String[] messages) {
        this.messages = messages;
    }

    /**
     * Sets the string message to a different string assuming that the array has
     * only 1 element.
     *
     * @param messages The message
     */
    public void setMessages(final String messages) {
        this.messages[0] = messages;
    }

    /**
     * @param target Message target
     * @see #msg(CommandSender, Map, boolean)
     */
    public void msg(final CommandSender target) {
        msg(target, null, false);
    }

    /**
     * Sends a translated message to a target commandsender with placeholders gained
     * from a map. If the map is null, no placeholder will be set and it will still
     * execute.
     *
     * @param target     Message target
     * @param map        The Map with the placeholders
     * @param ignoreCase If you want to ignore case
     */
    public void msg(final CommandSender target, final Map<String, String> map, final boolean ignoreCase) {
        if (this.isMultiLined()) {
            Utils.msg(target, this.getMessages(), map, ignoreCase);
        } else {
            Utils.msg(target, this.getMessages()[0], map, ignoreCase);
        }
    }

    /**
     * Sends a translated message to a target commandsender with placeholders gained
     * from a map. If the map is null, no placeholder will be set and it will still
     * execute.
     */
    public void msgAll() {
        if (this.isMultiLined()) {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()));
        } else {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()[0]));
        }
    }

    /**
     * Sends a translated message to a target commandsender with placeholders gained
     * from a map. If the map is null, no placeholder will be set and it will still
     * execute.
     *
     * @param map        The placeholders map
     * @param ignoreCase If you want to ignore case
     */
    public void msgAll(final Map<String, String> map, final boolean ignoreCase) {
        if (this.isMultiLined()) {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages(), map, ignoreCase));
        } else {
            Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()[0], map, ignoreCase));
        }
    }

}
