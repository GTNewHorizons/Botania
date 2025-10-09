/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 26, 2014, 7:50:37 PM (GMT)]
 */
package vazkii.botania.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.botania.common.entity.EntityVineBall;
import vazkii.botania.common.lib.LibItemNames;

public class ItemVineBall extends ItemMod {

	public ItemVineBall() {
		setUnlocalizedName(LibItemNames.VINE_BALL);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!player.capabilities.isCreativeMode)
			--stack.stackSize;

		world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));

		if(!world.isRemote)
			world.spawnEntityInWorld(new EntityVineBall(player, true));

		return stack;
	}

}
