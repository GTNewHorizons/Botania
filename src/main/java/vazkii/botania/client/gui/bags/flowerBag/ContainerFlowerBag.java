package vazkii.botania.client.gui.bags.flowerBag;

import net.minecraft.entity.player.EntityPlayer;
import vazkii.botania.client.gui.bags.ContainerMagicPlantBag;

public class ContainerFlowerBag extends ContainerMagicPlantBag {
    public ContainerFlowerBag(EntityPlayer player) {
        super(player);
    }

    @Override
    public void addMagicPlantSlots(EntityPlayer player) {
        int slot = player.inventory.currentItem;
        magicPlantInv = new InventoryFlowerBag(player, slot);

        for(int i = 0; i < 2; ++i) {
            for (int j = 0; j < 8; ++j) {
                int k = j + i * 8;
                addSlotToContainer(new SlotFlower(magicPlantInv, k, 17 + j * 18, 26 + i * 18, k));
            }
        }
    }
}
