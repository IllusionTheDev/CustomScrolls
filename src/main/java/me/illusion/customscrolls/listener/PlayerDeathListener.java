package me.illusion.customscrolls.listener;

import me.illusion.customscrolls.CustomScrollsPlugin;
import me.illusion.customscrolls.data.DeathData;
import me.illusion.customscrolls.data.ItemData;
import me.illusion.customscrolls.data.ScrollType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerDeathListener implements Listener {

    private final Map<UUID, DeathData> deathData = new HashMap<>();
    private final CustomScrollsPlugin main;

    public PlayerDeathListener(CustomScrollsPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory())
            return;

        Player player = event.getEntity();
        Map<Integer, ItemStack> items = new HashMap<>();

        ItemStack[] inventory = player.getInventory().getContents();

        for (int index = 0; index < inventory.length; index++) {
            ItemStack item = inventory[index];

            if(item == null)
                continue;

            ItemData data = ItemData.fromItem(item);

            if (data == null)
                continue;

            if(!data.containsScroll(ScrollType.KEEP_ON_DEATH))
                continue;

            if(keep())
                items.put(index, item);
        }

        if(items.isEmpty())
            return;

        event.getDrops().removeIf(items::containsValue);

        deathData.put(player.getUniqueId(), new DeathData(items));
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        DeathData data = deathData.remove(player.getUniqueId());

        if (data == null)
            return;

        for (Map.Entry<Integer, ItemStack> entry : data.getPreventedItems().entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }

    }

    private boolean keep() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt(100) < main.getScrollFile().getInt("keep_on_death.chance");
    }
}
