/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 22, 2014, 3:00:09 PM (GMT)]
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.Botania;
import vazkii.botania.common.lib.LibItemNames;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;

public class ItemReachRing extends ItemBauble {

	public ItemReachRing() {
		super(LibItemNames.REACH_RING);
	}

	@Override
	public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
		Botania.proxy.setExtraReach(player, 3.5F);
	}

	@Override
	public void onPlayerLoad(ItemStack stack, EntityLivingBase player) {
		if(player.worldObj.isRemote) {
			// Baubles resends all baubles to the client on login and on every dimension change and calls this for each
			// of them, but the client keeps its reach extension across dimension changes. Set it instead of adding to it.
			Botania.proxy.setClientExtraReach(player, 3.5F * countWornRings(player));
		} else super.onPlayerLoad(stack, player);
	}

	private static int countWornRings(EntityLivingBase player) {
		if(!(player instanceof EntityPlayer))
			return 0;

		int count = 0;
		IInventory baubles = BaublesApi.getBaubles((EntityPlayer) player);
		for(int i = 0; i < baubles.getSizeInventory(); i++) {
			ItemStack bauble = baubles.getStackInSlot(i);
			if(bauble != null && bauble.getItem() instanceof ItemReachRing)
				count++;
		}
		return count;
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {
		Botania.proxy.setExtraReach(player, -3.5F);
		super.onUnequipped(stack, player);
	}

	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}

}
