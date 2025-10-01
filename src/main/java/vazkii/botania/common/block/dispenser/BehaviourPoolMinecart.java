/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 18, 2015, 12:22:58 AM (GMT)]
 */
package vazkii.botania.common.block.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import vazkii.botania.common.entity.EntityPoolMinecart;

public class BehaviourPoolMinecart extends BehaviorDefaultDispenseItem {

	@Override
	public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		EnumFacing facing = BlockDispenser.func_149937_b(source.getBlockMetadata());
		World world = source.getWorld();
		double d0 = source.getX() + facing.getFrontOffsetX() * 1.125F;
		double d1 = source.getY() + facing.getFrontOffsetY() * 1.125F;
		double d2 = source.getZ() + facing.getFrontOffsetZ() * 1.125F;
		int i = source.getXInt() + facing.getFrontOffsetX();
		int j = source.getYInt() + facing.getFrontOffsetY();
		int k = source.getZInt() + facing.getFrontOffsetZ();
		Block block = world.getBlock(i, j, k);
		double d3;

		if(BlockRailBase.func_150051_a(block))
			d3 = 0.0D;
		else {
			if(block.getMaterial() != Material.air || !BlockRailBase.func_150051_a(world.getBlock(i, j - 1, k)))
				return super.dispenseStack(source, stack);

			d3 = -1.0D;
		}

		EntityMinecart entityminecart = new EntityPoolMinecart(world, d0, d1 + d3, d2);

		if(stack.hasDisplayName())
			entityminecart.setMinecartName(stack.getDisplayName());

		world.spawnEntityInWorld(entityminecart);
		stack.splitStack(1);
		return stack;
	}

	@Override
	protected void playDispenseSound(IBlockSource source) {
		source.getWorld().playAuxSFX(1000, source.getXInt(), source.getYInt(), source.getZInt(), 0);
	}

}
