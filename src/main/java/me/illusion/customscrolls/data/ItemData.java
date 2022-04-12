package me.illusion.customscrolls.data;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemData {

    @Getter
    private final Set<ScrollType> appliedScrolls = new HashSet<>();
    private final Map<ScrollType, ScrollData> scrollData = new HashMap<>();

    public boolean tryApply(ScrollType type) {
        if(containsScroll(type))
            return false;

        appliedScrolls.add(type);

        if(type.shouldCreateData())
            scrollData.put(type, new ScrollData(type, 0));

        return true;
    }

    public boolean containsScroll(ScrollType scrollType) {
        return appliedScrolls.contains(scrollType);
    }

    public ScrollData getScrollData(ScrollType scrollType) {
        if(!appliedScrolls.contains(scrollType)) return null;
        return scrollData.get(scrollType);
    }

    public void incrementCount(ScrollType scrollType) {
        ScrollData data = getScrollData(scrollType);
        if(data == null) {
            return;
        }

        data.setCount(data.getCount() + 1);
    }

    public static ItemData fromItem(ItemStack item) {
        if(item == null || item.getType().isAir())
            return null;

        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasKey("CustomScrolls")) return null;

        ItemData itemData = new ItemData();
        NBTCompound scrollData = nbt.getCompound("CustomScrolls");

        for(String key : scrollData.getStringList("applied-scrolls"))
            itemData.appliedScrolls.add(ScrollType.valueOf(key));

        NBTCompoundList scrollDataList = scrollData.getCompoundList("scroll-data");

        for(NBTListCompound scrollDataCompound : scrollDataList) {
            ScrollType scrollType = ScrollType.valueOf(scrollDataCompound.getString("type"));
            ScrollData data = ScrollData.fromNBT(scrollDataCompound);

            itemData.scrollData.put(scrollType, data);
        }

        return itemData;
    }

    public ItemStack apply(ItemStack item) {
        NBTItem nbt = new NBTItem(item);

        if(!nbt.hasKey("CustomScrolls"))
            nbt.addCompound("CustomScrolls");

        NBTCompound scrollData = nbt.getCompound("CustomScrolls");

        for(ScrollType scrollType : this.appliedScrolls)
            scrollData.getStringList("applied-scrolls").add(scrollType.name());

        NBTCompoundList list = scrollData.getCompoundList("scroll-data");
        list.clear();

        for(ScrollData data : this.scrollData.values()) {
            data.save(list.addCompound());
        }

        return nbt.getItem();
    }
}
