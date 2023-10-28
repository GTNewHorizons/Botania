package vazkii.botania.common.item.bag;

import net.minecraft.block.Block;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.lib.LibGuiIDs;
import vazkii.botania.common.lib.LibItemNames;

public class ItemFlowerBag extends ItemBagBase {

	public ItemFlowerBag() {
		super(LibItemNames.FLOWER_BAG);
	}

	@Override
	protected Block getValidPickUp() {
		return ModBlocks.flower;
	}

	@Override
	protected int getGuiID() {
		return LibGuiIDs.FLOWER_BAG;
	}
}
