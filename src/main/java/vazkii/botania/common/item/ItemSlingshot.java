/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 27, 2014, 12:38:58 AM (GMT)]
 */
package vazkii.botania.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import vazkii.botania.common.entity.EntityVineBall;
import vazkii.botania.common.lib.LibItemNames;

public class ItemSlingshot extends ItemMod {

	public ItemSlingshot() {
		setMaxStackSize(1);
		setUnlocalizedName(LibItemNames.SLINGSHOT);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemInUseCount) {
		int j = getMaxItemUseDuration(stack) - itemInUseCount;

		if(player.capabilities.isCreativeMode || player.inventory.hasItem(ModItems.vineBall)) {
			float f = j / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if(f < 1F)
				return;

			if(!player.capabilities.isCreativeMode)
				player.inventory.consumeInventoryItem(ModItems.vineBall);

			if(!world.isRemote) {
				EntityVineBall ball = new EntityVineBall(player, false);
				ball.motionX *= 1.6;
				ball.motionY *= 1.6;
				ball.motionZ *= 1.6;
				world.spawnEntityInWorld(ball);
			}
		}
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(player.capabilities.isCreativeMode || player.inventory.hasItem(ModItems.vineBall))
			player.setItemInUse(stack, getMaxItemUseDuration(stack));

		return stack;
	}

}
