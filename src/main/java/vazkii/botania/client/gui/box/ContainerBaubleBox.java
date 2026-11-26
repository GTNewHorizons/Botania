/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [25/11/2015, 19:59:06 (GMT)]
 */
package vazkii.botania.client.gui.box;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.client.gui.SlotLocked;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.container.SlotBauble;
import baubles.common.lib.PlayerHandler;

public class ContainerBaubleBox extends Container {

	InventoryBaubleBox baubleBoxInv;
	InventoryBaubles baubles;
	private final ItemStack[] baubleStacks;

	public ContainerBaubleBox(EntityPlayer player) {
		int i;
		int j;

		int slot = player.inventory.currentItem;
		IInventory playerInv = player.inventory;
		baubleBoxInv = new InventoryBaubleBox(player, slot);

		baubles = PlayerHandler.getPlayerBaubles(player);
		baubleStacks = baubles.stackList;

		addBaubleSlot(BaubleType.AMULET, 0);
		addBaubleSlot(BaubleType.RING, 1);
		addBaubleSlot(BaubleType.RING, 2);
		addBaubleSlot(BaubleType.BELT, 3);

		for(i = 0; i < 4; ++i)
			for(j = 0; j < 6; ++j) {
				int k = j + i * 6;
				addSlotToContainer(new SlotAnyBauble(baubleBoxInv, k, 62 + j * 18, 8 + i * 18));
			}

		for(i = 0; i < 3; ++i)
			for(j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

		for(i = 0; i < 9; ++i) {
			if(player.inventory.currentItem == i)
				addSlotToContainer(new SlotLocked(playerInv, i, 8 + i * 18, 142));
			else addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return baubleStacks == baubles.stackList && baubleBoxInv.isUseableByPlayer(player);
	}

	private void addBaubleSlot(BaubleType type, int index) {
		addSlotToContainer(new SlotBauble(baubles, type, index, 8, 8 + index * 18) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return baubleStacks == baubles.stackList && baubles.isItemValidForSlot(index, stack)
						&& super.isItemValid(stack);
			}
		});
	}

	@Override
	public void detectAndSendChanges() {
		EntityPlayer player = baubleBoxInv.player;
		if(!canInteractWith(player)) {
			if(!player.worldObj.isRemote && player.openContainer == this)
				player.closeScreen();
			return;
		}
		super.detectAndSendChanges();
	}

	@Override
	public ItemStack slotClick(int slot, int button, int mode, EntityPlayer player) {
		if(!canInteractWith(player)) return null;
		return super.slotClick(slot, button, mode, player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		baubleBoxInv.pushInventory();

		if(player instanceof EntityPlayerMP serverPlayer && baubleStacks != baubles.stackList)
			serverPlayer.sendContainerToPlayer(serverPlayer.inventoryContainer);
	}

	@Override
	public void putStacksInSlots(ItemStack[] stacks) {
		boolean previous = baubles.blockEvents;
		baubles.blockEvents = true;
		try {
			super.putStacksInSlots(stacks);
		} finally {
			baubles.blockEvents = previous;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		if(!canInteractWith(player) || index < 0 || index >= inventorySlots.size()) return null;
		ItemStack itemstack = null;
		Slot slot = (Slot)inventorySlots.get(index);

		if(slot != null && slot.getHasStack() && slot.canTakeStack(player)) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			System.out.println(index + " " + itemstack);
			if(index < 28) {
				if(!mergeItemStack(itemstack1, 28, 64, true))
					return null;
			} else {
				if(itemstack1 != null && (itemstack1.getItem() instanceof IBauble || itemstack1.getItem() instanceof IManaItem) && !mergeItemStack(itemstack1, 4, 28, false))
					return null;
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
