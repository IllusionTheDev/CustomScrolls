package me.illusion.customscrolls.listener;

import me.illusion.customscrolls.data.ItemData;
import me.illusion.customscrolls.data.ScrollType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBreakListener implements Listener {

    @EventHandler
    private void onDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        ItemData data = ItemData.fromItem(item);

        if (data == null)
            return;

        if(!data.containsScroll(ScrollType.UNBREAKABLE))
            return;

        event.setCancelled(true);
    }
}
