package me.illusion.customscrolls.data;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum ScrollType {
    MOB_TRACKER("%mob_kills%"),
    PLAYER_KILL_TRACKER("%player_kills%"),
    BLOCK_BREAK_TRACKER("%block_breaks%"),
    UNBREAKABLE(),
    KEEP_ON_DEATH();

    private final boolean shouldCreateData;
    private String placeholder;

    ScrollType() {
        shouldCreateData = false;
    }
    ScrollType(String placeholder) {
        this.shouldCreateData = true;
        this.placeholder = placeholder;
    }


    public boolean shouldCreateData() {
        return shouldCreateData;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public ItemStack applyTo(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("CustomScrollType", this.name());
        return nbtItem.getItem();
    }


    public static ScrollType getScrollType(ItemStack item) {
        if(item == null || item.getType().isAir())
            return null;

        NBTItem nbtItem = new NBTItem(item);
        if(nbtItem.hasKey("CustomScrollType")) {
            return ScrollType.valueOf(nbtItem.getString("CustomScrollType"));
        }

        return null;
    }

    public static Set<String> names() {
       return EnumSet.allOf(ScrollType.class).stream().map(ScrollType::name).collect(Collectors.toSet());
    }

}
