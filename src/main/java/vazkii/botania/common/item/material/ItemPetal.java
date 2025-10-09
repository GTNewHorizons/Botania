/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 19, 2014, 3:28:21 PM (GMT)]
 */
package vazkii.botania.common.item.material;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.botania.api.recipe.IFlowerComponent;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.Item16Colors;
import vazkii.botania.common.lib.LibItemNames;

public class ItemPetal extends Item16Colors implements IFlowerComponent {

	public ItemPetal() {
		super(LibItemNames.PETAL);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		ItemStack stackToPlace = new ItemStack(ModBlocks.buriedPetals, 1, stack.getItemDamage());
		stackToPlace.tryPlaceItemIntoWorld(player, world, x, y, z, side, subX, subY, subZ);

		if(stackToPlace.stackSize == 0) {
			if(!player.capabilities.isCreativeMode)
				stack.stackSize--;

			return true;
		}
		return false;
	}

	@Override
	public boolean canFit(ItemStack stack, IInventory apothecary) {
		return true;
	}

	@Override
	public int getParticleColor(ItemStack stack) {
		return getColorFromItemStack(stack, 0);
	}
}
