package me.illusion.customscrolls.lore;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.illusion.customscrolls.CustomScrollsPlugin;
import me.illusion.customscrolls.data.ItemData;
import me.illusion.customscrolls.data.ScrollType;
import me.illusion.utilities.item.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PacketHandler {

    private final CustomScrollsPlugin main;

    public PacketHandler(CustomScrollsPlugin main) {
        this.main = main;
        registerListener(main);
    }

    private void registerListener(CustomScrollsPlugin main) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        // Format (in order)
        // int syncId, int revision, int slot, ItemStack stack

        manager.addPacketListener(new PacketAdapter(main, PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                ItemStack item = packet.getItemModifier().read(0);
                //System.out.println("Slot: " + packet.getIntegers().read(2));

                final int[] valueIndex = {1};
                /*
                packet.getModifier().getValues().forEach(value -> {
                    System.out.println("Value " + valueIndex[0]++ + ": " + value);

                            System.out.println("Value Type: " + value.getClass().getName());
                        });

                 */
                packet.getItemModifier().write(0, replaceData(item));
                //System.out.println("---------------------------------");

            }
        });

        manager.addPacketListener(new PacketAdapter(main, PacketType.Play.Server.WINDOW_ITEMS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                List<ItemStack> items = packet.getItemListModifier().readSafely(0);

                for (int index = 0; index < items.size(); index++) {
                    ItemStack item = items.get(index);

                    if(item == null || item.getType().isAir())
                        continue;

                    items.set(index, replaceData(item));
                }

                ItemStack hand = packet.getItemModifier().read(0);
                if(hand != null && !hand.getType().isAir())
                    packet.getItemModifier().write(0, replaceData(hand));

                packet.getItemListModifier().write(0, items);

            }
        });

    }

    /**
     * Using NBT stuff for compatibility reasons, can be updated to NMS but can break
     *
     * @param item - Bukkit itemstack
     * @return Itemstack with new lore
     */
    private ItemStack replaceData(ItemStack item) {
        if (item == null || item.getType().isAir() || ItemData.fromItem(item) == null)
            return item;

        ItemStack copy = item.clone();
        ItemMeta copyMeta = copy.getItemMeta();

        List<String> newLore = copyMeta.getLore();

        if (newLore == null)
            newLore = new ArrayList<>();

        newLore = removeLore(copy, newLore);

        newLore.addAll(getLore(copy));
        //  System.out.println("Adding lore pre-colorization: " + newLore);

        if(newLore.isEmpty()) {
            System.out.println("Empty lore??");
            newLore = getLore(copy);
        }

        copyMeta.setLore(ItemUtil.colorize(newLore));

        //System.out.println("Result lore: " + copyMeta.getLore());
        copy.setItemMeta(copyMeta);

        return copy;
    }

    private List<String> removeLore(ItemStack item, List<String> original) {
        List<String> copy = new ArrayList<>(original);
        ItemData data = ItemData.fromItem(item);

        if(data == null)
            return copy;

        List<String> lore = getLore(item);

        copy.removeIf(lore::contains);

        return copy;
    }


    private List<String> getLore(ItemStack item) {
        ItemData data = ItemData.fromItem(item);
        List<String> list = new ArrayList<>();

        if(data == null)
            return list;

        for(ScrollType type : data.getAppliedScrolls()) {
            List<String> text = main.getScrollFile().getStringList(type.name().toLowerCase(Locale.ROOT) + ".lore-on-item");

            list.addAll(text);
        }

        for(int index = 0; index < list.size(); index++) {
            String line = list.get(index);
            line = replacePlaceholders(line, data);
            line = ChatColor.translateAlternateColorCodes('&', line);
            list.set(index, line);
        }

        return list;

    }

    private String replacePlaceholders(String str, ItemData data) {
        for(ScrollType type : data.getAppliedScrolls()) {
            if(!type.shouldCreateData())
                continue;

            String placeholder = type.getPlaceholder();
            str = str.replace(placeholder, String.valueOf(data.getScrollData(type).getCount()));
        }

        return str;
    }
}