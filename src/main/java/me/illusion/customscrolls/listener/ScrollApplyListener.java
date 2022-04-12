package me.illusion.customscrolls.listener;

import me.illusion.customscrolls.CustomScrollsPlugin;
import me.illusion.customscrolls.data.ItemData;
import me.illusion.customscrolls.data.ScrollType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

public class ScrollApplyListener implements Listener {

    private CustomScrollsPlugin main;

    public ScrollApplyListener(CustomScrollsPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (cursor == null || current == null || cursor.getType().isAir() || current.getType().isAir()) {
            return;
        }

        ScrollType type = ScrollType.getScrollType(cursor);

        if (type == null) {
            return;
        }

        List<String> keywords = main.getScrollFile().getStringList(type.name().toLowerCase(Locale.ROOT) + ".applicable-item-keywords");

        if(!matchesKeywords(keywords, current.getType())) {
            return;
        }

        event.setCancelled(true);

        Inventory inventory = event.getClickedInventory();

        if(inventory == null) {
            return;
        }

        ItemData itemData = ItemData.fromItem(current);

        if (itemData == null) {
            itemData = new ItemData();
        }

        if(!itemData.tryApply(type)) {
            return;
        }

        event.getView().setCursor(null);
        inventory.setItem(event.getSlot(), itemData.apply(current));
    }

    private boolean matchesKeywords(List<String> keywords, Material material) {
        // DIAMOND_SWORD
        // ["sword", "shovel", "axe", "hoe"]

        // Does the top text contain any of the words in the list?
        for(String keyword : keywords) {
            if(material.name().toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}
