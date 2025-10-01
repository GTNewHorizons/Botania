package vazkii.botania.api.block;

import net.minecraft.item.ItemStack;

public class CatalyzedCocoonSpawn {
    public final String id;
    public final ItemStack catalyst;
    public final int maxCatalyst;

    public CatalyzedCocoonSpawn(ItemStack catalyst, int maxCatalyst, String id) {
        // TODO Add particles?
        this.id = id;
        this.catalyst = catalyst;
        this.maxCatalyst = maxCatalyst;
    }
}
