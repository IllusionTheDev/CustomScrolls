package me.illusion.customscrolls.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class DeathData {

    private final Map<Integer, ItemStack> preventedItems;
}
