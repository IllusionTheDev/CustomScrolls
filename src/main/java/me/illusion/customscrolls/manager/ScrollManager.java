package me.illusion.customscrolls.manager;

import me.illusion.customscrolls.CustomScrollsPlugin;
import me.illusion.customscrolls.data.ScrollType;
import me.illusion.utilities.item.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ScrollManager {

    private final Map<ScrollType, ItemStack> scrollItems = new HashMap<>();
    private final CustomScrollsPlugin main;

    public ScrollManager(CustomScrollsPlugin main) {
        this.main = main;
        load();
    }

    private void load() {
        FileConfiguration config = main.getScrollFile();

        for(String key : config.getKeys(false)) {
            ScrollType type = ScrollType.valueOf(key.toUpperCase());
            ItemStack item = ItemBuilder.fromSection(config.getConfigurationSection(key + ".item"));
            item = type.applyTo(item);
            scrollItems.put(type, item);
        }
    }

    public ItemStack getItem(ScrollType type) {
        return scrollItems.get(type).clone();
    }
}
