/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 24, 2015, 4:43:16 PM (GMT)]
 */
package vazkii.botania.common.item.lens;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.common.core.handler.ConfigHandler;

import static vazkii.botania.common.core.helper.ItemHelper.canBreakBlock;

public class LensWeight extends Lens {

	@Override
	public boolean collideBurst(IManaBurst burst, EntityThrowable entity, MovingObjectPosition pos, boolean isManaBlock, boolean dead, ItemStack stack, EntityPlayer player) {
		if(!burst.isFake() && !entity.worldObj.isRemote) {
			World world = entity.worldObj;
			int x = pos.blockX;
			int y = pos.blockY;
			int z = pos.blockZ;
			int harvestLevel = ConfigHandler.harvestLevelWeight;
			
			Block block = world.getBlock(x, y, z);
			Block blockBelow = world.getBlock(x, y - 1, z);
			int meta = world.getBlockMetadata(x, y, z);
			int neededHarvestLevel = block.getHarvestLevel(meta);
			
			if(canBreakBlock(world,x,y,z,player) && blockBelow.isAir(world, x, y - 1, z) && block.getBlockHardness(world, x, y, z) != -1 && neededHarvestLevel <= harvestLevel && world.getTileEntity(x, y, z) == null && block.canSilkHarvest(world, null, x, y, z, meta)) {
				EntityFallingBlock falling = new EntityFallingBlock(world, x + 0.5, y + 0.5, z + 0.5, block, meta);
					world.spawnEntityInWorld(falling);
			}
		}

		return dead;
	}

}
