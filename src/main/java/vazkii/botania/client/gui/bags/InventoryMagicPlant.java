package vazkii.botania.client.gui.bags;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.ItemFlowerBag;
import vazkii.botania.common.item.ModItems;

public abstract class InventoryMagicPlant implements IInventory {

    private static final ItemStack[] FALLBACK_INVENTORY = new ItemStack[16];

    EntityPlayer player;
    int slot;
    ItemStack[] stacks = null;

    boolean invPushed = false;
    ItemStack storedInv = null;

    public InventoryMagicPlant(EntityPlayer player, int slot) {
        this.player = player;
        this.slot = slot;
    }

    ItemStack getStack() {
        ItemStack stack = player.inventory.getStackInSlot(slot);
        if(stack != null)
            storedInv = stack;
        return stack;
    }


    public void pushInventory() {
        if(invPushed)
            return;

        ItemStack stack = getStack();
        if(stack == null)
            stack = storedInv;

        if(stack != null) {
            ItemStack[] inv = getInventory();
            ItemFlowerBag.setStacks(stack, inv);
        }

        invPushed = true;
    }

    public boolean isMagicPlantBag(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.flowerBag;
    }

    ItemStack[] getInventory() {
        if(stacks != null)
            return stacks;

        ItemStack stack = getStack();
        if(isMagicPlantBag(getStack())) {
            stacks = ItemFlowerBag.loadStacks(stack);
            return stacks;
        }

        return FALLBACK_INVENTORY;
    }
    @Override
    public int getSizeInventory() {
        return 16;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 0;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }
}
