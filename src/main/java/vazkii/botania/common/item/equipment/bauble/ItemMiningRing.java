/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [May 17, 2014, 4:12:46 PM (GMT)]
 */
package vazkii.botania.common.item.equipment.bauble;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.lib.LibItemNames;
import baubles.api.BaubleType;

public class ItemMiningRing extends ItemBauble implements IManaUsingItem {
	public static final int MANA_COST = 5;
	public static final int HASTE_AMPLIFIER = 1;

	public ItemMiningRing() {
		super(LibItemNames.MINING_RING);
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (!(player instanceof EntityPlayer) || player.worldObj.isRemote) {
			return;
		}
		final PotionEffect effect = player.getActivePotionEffect(Potion.digSpeed);
		if (!ManaItemHandler.requestManaExact(stack, (EntityPlayer) player, MANA_COST, player.swingProgress == 0.25F)) {
			if (effect != null && effect.getAmplifier() == HASTE_AMPLIFIER) {
				player.removePotionEffect(Potion.digSpeed.id);
			}
		} else if (effect == null || effect.getDuration() <= 20) {
			if (effect != null && effect.getAmplifier() == HASTE_AMPLIFIER) {
				player.removePotionEffect(Potion.digSpeed.id);
			}
			player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 200, HASTE_AMPLIFIER, true));
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {
		final PotionEffect effect = player.getActivePotionEffect(Potion.digSpeed);
		if (effect != null && effect.getAmplifier() == HASTE_AMPLIFIER) {
			player.removePotionEffect(Potion.digSpeed.id);
		}
	}

	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

}
