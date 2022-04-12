package me.illusion.customscrolls.listener;

import me.illusion.customscrolls.data.ItemData;
import me.illusion.customscrolls.data.ScrollType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class KillTrackerListener implements Listener {

    @EventHandler
    private void onDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer == null)
            return;

        ItemStack item = killer.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir())
            return;

        ItemData data = ItemData.fromItem(item);

        if (data == null)
            return;


        data.incrementCount(event.getEntity() instanceof Player ? ScrollType.PLAYER_KILL_TRACKER : ScrollType.MOB_TRACKER);
        item = data.apply(item);

        killer.getInventory().setItemInMainHand(item);
    }
}
