package vazkii.botania.client.gui.bags.flowerBag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.gui.bags.InventoryMagicPlantBag;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.LibItemNames;

public class InventoryFlowerBag extends InventoryMagicPlantBag {
    public InventoryFlowerBag(EntityPlayer player, int slot) {
        super(player, slot);
    }

    @Override
    public boolean isMagicPlantBag(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.flowerBag;
    }

    @Override
    protected String getBagName() {
        return LibItemNames.FLOWER_BAG;
    }
}
