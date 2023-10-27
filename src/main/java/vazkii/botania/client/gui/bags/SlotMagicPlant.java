package vazkii.botania.client.gui.bags;

import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.block.ModBlocks;

public abstract class SlotMagicPlant extends Slot {

    InventoryMagicPlant inv;
    int color;

    public SlotMagicPlant(InventoryMagicPlant p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_, int color) {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        this.color = color;
        inv = p_i1824_1_;
    }

    @Override
    public void onSlotChange(ItemStack oldStack, ItemStack newStack) {
        inv.setInventorySlotContents(color, newStack);
    }

    @Override
    public abstract boolean isItemValid(ItemStack stack);

}