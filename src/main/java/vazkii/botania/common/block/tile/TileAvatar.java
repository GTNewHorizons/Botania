/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Oct 24, 2015, 3:17:44 PM (GMT)]
 */
package vazkii.botania.common.block.tile;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import vazkii.botania.api.item.IAvatarTile;
import vazkii.botania.api.item.IAvatarWieldable;
import vazkii.botania.common.lib.LibBlockNames;

public class TileAvatar extends TileSimpleInventory implements IAvatarTile, ISidedInventory {

	private static final int MAX_MANA = 6400;

	private static final String TAG_ENABLED = "enabled";
	private static final String TAG_TICKS_ELAPSED = "ticksElapsed";
	private static final String TAG_MANA = "ticksElapsed";

	boolean enabled;
	int ticksElapsed;
	int mana;

	@Override
	public void updateEntity() {
		super.updateEntity();

		enabled = true;
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int redstoneSide = worldObj.getIndirectPowerLevelTo(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir.ordinal());
			if(redstoneSide > 0) {
				enabled = false;
				break;
			}
		}

		ItemStack stack = getStackInSlot(0);
		if(stack != null && stack.getItem() instanceof IAvatarWieldable wieldable) {
            wieldable.onAvatarUpdate(this, stack);
		}

		if(enabled)
			ticksElapsed++;
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		super.writeCustomNBT(compound);
		compound.setBoolean(TAG_ENABLED, enabled);
		compound.setInteger(TAG_TICKS_ELAPSED, ticksElapsed);
		compound.setInteger(TAG_MANA, mana);
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		super.readCustomNBT(compound);
		enabled = compound.getBoolean(TAG_ENABLED);
		ticksElapsed = compound.getInteger(TAG_TICKS_ELAPSED);
		mana = compound.getInteger(TAG_MANA);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() instanceof IAvatarTile;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return false;
	}

	@Override
	public String getInventoryName() {
		return LibBlockNames.AVATAR;
	}

	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public void recieveMana(int mana) {
		this.mana = Math.min(MAX_MANA, this.mana + mana);
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return getStackInSlot(0) != null;
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public int getElapsedFunctionalTicks() {
		return ticksElapsed;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
