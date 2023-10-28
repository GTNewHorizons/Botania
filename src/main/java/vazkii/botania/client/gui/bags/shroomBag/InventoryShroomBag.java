package vazkii.botania.client.gui.bags.shroomBag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.gui.bags.InventoryMagicPlantBag;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.LibItemNames;

public class InventoryShroomBag extends InventoryMagicPlantBag {
    public InventoryShroomBag(EntityPlayer player, int slot) {
        super(player, slot);
    }

    @Override
    public boolean isMagicPlantBag(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.shroomBag;
    }

    @Override
    protected String getBagName() {
        return LibItemNames.SHROOM_BAG;
    }
}
