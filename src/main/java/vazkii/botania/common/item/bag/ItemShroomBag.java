package vazkii.botania.common.item.bag;

import net.minecraft.block.Block;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.lib.LibGuiIDs;
import vazkii.botania.common.lib.LibItemNames;

public class ItemShroomBag extends ItemBagBase{

    public ItemShroomBag() {
        super(LibItemNames.SHROOM_BAG);
    }

    @Override
    protected Block getValidPickUp() {
        return ModBlocks.mushroom;
    }

    @Override
    protected int getGuiID() {
        return LibGuiIDs.SHROOM_BAG;
    }
}
