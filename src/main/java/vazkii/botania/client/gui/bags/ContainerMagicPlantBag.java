package vazkii.botania.client.gui.bags;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.gui.SlotLocked;

public abstract class ContainerMagicPlantBag extends Container {

    protected InventoryMagicPlantBag magicPlantInv;
    public ContainerMagicPlantBag(EntityPlayer player) {
        int i;
        int j;

        IInventory playerInv = player.inventory;

        addMagicPlantSlots(player);

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }


        for(i = 0; i < 9; ++i) {
            if(player.inventory.currentItem == i) {
                addSlotToContainer(new SlotLocked(playerInv, i, 8 + i * 18, 142));
            }
            else addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    public abstract void  addMagicPlantSlots(EntityPlayer player);

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        boolean can = magicPlantInv.isUseableByPlayer(player);
        if(!can)
            onContainerClosed(player);

        return can;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        magicPlantInv.pushInventory();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(index);

        if(slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(index < 16) {
                if(!mergeItemStack(itemstack1, 16, 52, true))
                    return null;
            } else {
                int i = itemstack.getItemDamage();
                if(i < 16) {
                    Slot slot1 = (Slot)inventorySlots.get(i);
                    if(slot1.isItemValid(itemstack) && !mergeItemStack(itemstack1, i, i + 1, true))
                        return null;
                }
            }

            if(itemstack1.stackSize == 0)
                slot.putStack((ItemStack)null);
            else slot.onSlotChanged();

            if(itemstack1.stackSize == itemstack.stackSize)
                return null;

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

}


