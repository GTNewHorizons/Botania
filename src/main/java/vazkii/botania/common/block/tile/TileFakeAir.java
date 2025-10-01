/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Apr 10, 2015, 10:24:48 PM (GMT)]
 */
package vazkii.botania.common.block.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.common.block.subtile.functional.SubTileBubbell;

public class TileFakeAir extends TileMod {

	private static final String TAG_FLOWER_X = "flowerX";
	private static final String TAG_FLOWER_Y = "flowerY";
	private static final String TAG_FLOWER_Z = "flowerZ";

	int flowerX, flowerY, flowerZ;

	@Override
	public boolean canUpdate() {
		return false;
	}

	public void setFlower(TileEntity tile) {
		flowerX = tile.xCoord;
		flowerY = tile.yCoord;
		flowerZ = tile.zCoord;
	}

	public boolean canStay() {
		return SubTileBubbell.isValidBubbell(worldObj, flowerX, flowerY, flowerZ);
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger(TAG_FLOWER_X, flowerX);
		compound.setInteger(TAG_FLOWER_Y, flowerY);
		compound.setInteger(TAG_FLOWER_Z, flowerZ);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		flowerX = compound.getInteger(TAG_FLOWER_X);
		flowerY = compound.getInteger(TAG_FLOWER_Y);
		flowerZ = compound.getInteger(TAG_FLOWER_Z);
	}

}
