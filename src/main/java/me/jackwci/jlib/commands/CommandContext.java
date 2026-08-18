package me.jackwci.jlib.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandContext {

    private final CommandSender sender;
    private final String[] args;

    public CommandContext(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public CommandSender sender() {
        return sender;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player player() {
        if (!(sender instanceof Player player)) {
            throw new IllegalStateException("Command sender is not a player");
        }

        return player;
    }

    public String[] args() {
        return args;
    }

    public int length() {
        return args.length;
    }

    public boolean has(int index) {
        return index >= 0 && index < args.length;
    }

    public String arg(int index) {
        if (!has(index)) {
            return null;
        }

        return args[index];
    }

    public String argOr(int index, String fallback) {
        String argument = arg(index);

        return argument == null
                ? fallback
                : argument;
    }

    public String joinFrom(int index) {

        if (!has(index)) {
            return "";
        }

        return String.join("", Arrays.copyOfRange(args, index, args.length));
    }

}
