package vazkii.botania.client.gui.bags.flowerBag;

import net.minecraft.block.Block;
import vazkii.botania.client.gui.bags.InventoryMagicPlantBag;
import vazkii.botania.client.gui.bags.SlotMagicPlant;
import vazkii.botania.common.block.ModBlocks;

public class SlotFlower extends SlotMagicPlant {
    public SlotFlower(InventoryMagicPlantBag p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_, int color) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_, color);
    }

    @Override
    protected Block getValidBlock() {
        return ModBlocks.flower;
    }

}
