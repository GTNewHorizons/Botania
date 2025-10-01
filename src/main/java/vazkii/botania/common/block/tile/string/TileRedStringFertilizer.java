/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 16, 2014, 7:17:16 PM (GMT)]
 */
package vazkii.botania.common.block.tile.string;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class TileRedStringFertilizer extends TileRedString {

	public boolean func_149851_a(World world, boolean isClient) {
		ChunkCoordinates binding = getBinding();
		Block block = getBlockAtBinding();

		return block instanceof IGrowable ? ((IGrowable) block).func_149851_a(world, binding.posX, binding.posY, binding.posZ, isClient) : false;
	}

	public boolean func_149852_a(World world, Random random) {
		ChunkCoordinates binding = getBinding();
		Block block = getBlockAtBinding();
		return block instanceof IGrowable ? ((IGrowable) block).func_149852_a(world, random, binding.posX, binding.posY, binding.posZ) : false;
	}

	public void func_149853_b(World world, Random random) {
		ChunkCoordinates binding = getBinding();
		Block block = getBlockAtBinding();
		if(block instanceof IGrowable)
			((IGrowable) block).func_149853_b(world, random, binding.posX, binding.posY, binding.posZ);
	}

	@Override
	public boolean acceptBlock(int x, int y, int z) {
		return worldObj.getBlock(x, y, z) instanceof IGrowable;
	}

}
