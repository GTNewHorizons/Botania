package vazkii.botania.common.block.subtile.generating.compat;

import net.minecraft.item.ItemStack;

import gregtech.api.interfaces.IFoodStat;
import gregtech.api.items.MetaGeneratedItem;

public class GTFoodHelper {

    public static int getFoodHungerValue(ItemStack stack) {
        if (!(stack.getItem() instanceof MetaGeneratedItem)) return -1;

        MetaGeneratedItem metaItem = (MetaGeneratedItem) stack.getItem();
        IFoodStat foodStat = metaItem.mFoodStats.get((short) stack.getItemDamage());
        if (foodStat == null) return -1;

        int hungerValue = foodStat.getFoodLevel(metaItem, stack, null);
        return hungerValue > 0 ? hungerValue : -1;
    }
}