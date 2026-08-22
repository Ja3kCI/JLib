package me.jackwci.jlib.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface UI {

    Inventory getInventory();

    void open(Player player);
    void close(Player player);
    //todo void close(Player player)

}
