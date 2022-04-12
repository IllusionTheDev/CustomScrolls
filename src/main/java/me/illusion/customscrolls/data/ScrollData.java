package me.illusion.customscrolls.data;

import de.tr7zw.nbtapi.NBTListCompound;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ScrollData {

    private final ScrollType type;
    private int count;

    public static ScrollData fromNBT(NBTListCompound nbt) {
        return new ScrollData(ScrollType.valueOf(nbt.getString("type")), Integer.parseInt(nbt.getString("count")));
    }

    public void save(NBTListCompound nbt) {
        nbt.setString("type", type.name());
        nbt.setString("count", String.valueOf(count));
    }
}
