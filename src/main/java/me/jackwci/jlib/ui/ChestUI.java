package me.jackwci.jlib.ui;

import me.jackwci.jlib.ui.holder.UIHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestUI implements UI {
    private static final MiniMessage msg = MiniMessage.miniMessage();

    private final int rows;
    private final Component title;

    private final UIHolder holder;
    private final Inventory inventory;

    public ChestUI(int rows, String title) {

       if (rows < 1 || rows > 6) {
           throw new IllegalArgumentException("rows must be between 1 and 6");
       }

       this.rows = rows;
       this.title = msg.deserialize(title);

       this.holder = new UIHolder(this);

       this.inventory = Bukkit.createInventory(holder, rows * 9, this.title);

       holder.setInventory(this.inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void close(Player player) {
        player.closeInventory();
    }

    public Component getTitle() {
        return title;
    }
}
