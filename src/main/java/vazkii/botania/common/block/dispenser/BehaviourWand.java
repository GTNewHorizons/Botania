/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [May 16, 2014, 11:11:31 PM (GMT)]
 */
package vazkii.botania.common.block.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import vazkii.botania.api.wand.IWandable;

public class BehaviourWand extends BehaviorDefaultDispenseItem {

	@Override
	protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		ForgeDirection facing = ForgeDirection.getOrientation(BlockDispenser.func_149937_b(source.getBlockMetadata()).ordinal());
		int x = source.getXInt() + facing.offsetX;
		int y = source.getYInt() + facing.offsetY;
		int z = source.getZInt() + facing.offsetZ;
		World world = source.getWorld();
		Block block = world.getBlock(x, y, z);
		if(block instanceof IWandable) {
			((IWandable) block).onUsedByWand(null, stack, world, x, y, z, facing.getOpposite().ordinal());
			return stack;
		}

		return super.dispenseStack(source, stack);
	}

}
