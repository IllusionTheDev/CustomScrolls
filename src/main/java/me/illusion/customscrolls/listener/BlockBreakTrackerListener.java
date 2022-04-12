package me.illusion.customscrolls.listener;

import me.illusion.customscrolls.data.ItemData;
import me.illusion.customscrolls.data.ScrollType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakTrackerListener implements Listener {

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir())
            return;

        ItemData data = ItemData.fromItem(item);

        if (data == null)
            return;

        data.incrementCount(ScrollType.BLOCK_BREAK_TRACKER);
        item = data.apply(item);

        player.getInventory().setItemInMainHand(item);
    }
}
