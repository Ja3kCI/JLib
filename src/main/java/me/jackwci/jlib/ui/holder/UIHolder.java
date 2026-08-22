package me.jackwci.jlib.ui.holder;

import me.jackwci.jlib.ui.UI;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public class UIHolder implements InventoryHolder {

    private final UI ui;
    private Inventory inventory;

    public UIHolder(UI ui) {
        this.ui = ui;
    }

    public UI getUI() {
        return ui;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }

}
