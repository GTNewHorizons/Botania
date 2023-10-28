package vazkii.botania.client.gui.bags;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.bag.ItemBagBase;

public abstract class InventoryMagicPlantBag implements IInventory {

    private static final ItemStack[] FALLBACK_INVENTORY = new ItemStack[16];

    EntityPlayer player;
    int slot;
    ItemStack[] stacks = null;

    boolean invPushed = false;
    ItemStack storedInv = null;

    public InventoryMagicPlantBag(EntityPlayer player, int slot) {
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
            ItemBagBase.setStacks(stack, inv);
        }

        invPushed = true;
    }

    ItemStack[] getInventory() {
        if(stacks != null)
            return stacks;

        ItemStack stack = getStack();
        if(isMagicPlantBag(getStack())) {
            stacks = ItemBagBase.loadStacks(stack);
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
        return getInventory()[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack[] inventorySlots = getInventory();
        if (inventorySlots[index] != null) {
            ItemStack stackAt;

            if (inventorySlots[index].stackSize <= count) {
                stackAt = inventorySlots[index];
                inventorySlots[index] = null;
                return stackAt;
            } else {
                stackAt = inventorySlots[index].splitStack(count);

                if (inventorySlots[index].stackSize == 0)
                    inventorySlots[index] = null;

                return stackAt;
            }
        }

        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return getStackInSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack[] inventorySlots = getInventory();
        inventorySlots[index] = stack;
    }

    @Override
    public String getInventoryName() {
        return getBagName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return isMagicPlantBag(getStack()) ? 64 : 0;
    }

    @Override
    public void markDirty() {
        // NO-OP
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return isMagicPlantBag(getStack());
    }

    @Override
    public void openInventory() {
        // NO-OP
    }

    @Override
    public void closeInventory() {
        // NO-OP
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return isMagicPlantBag(getStack());
    }

    protected abstract String getBagName();

    public abstract boolean isMagicPlantBag(ItemStack stack);
}
