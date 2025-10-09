/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 19, 2014, 4:10:47 PM (GMT)]
 */
package vazkii.botania.common.item.material;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vazkii.botania.api.item.IDyablePool;
import vazkii.botania.common.item.Item16Colors;
import vazkii.botania.common.lib.LibItemNames;

public class ItemDye extends Item16Colors {

	public ItemDye() {
		super(LibItemNames.DYE);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		Block block = world.getBlock(x, y, z);
		int meta = stack.getItemDamage();
		if(meta != world.getBlockMetadata(x, y, z) && (block == Blocks.wool || block == Blocks.carpet)) {
			world.setBlockMetadataWithNotify(x, y, z, meta, 1 | 2);
			stack.stackSize--;
			return true;
		}
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof IDyablePool dyable) {
            if(meta != dyable.getColor()) {
				dyable.setColor(meta);
				stack.stackSize--;
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
		if(target instanceof EntitySheep entitysheep) {
            int i = stack.getItemDamage();

			if(!entitysheep.getSheared() && entitysheep.getFleeceColor() != i) {
				entitysheep.setFleeceColor(i);
				--stack.stackSize;
			}

			return true;
		}
		return false;
	}

}
