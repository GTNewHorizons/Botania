/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 29, 2015, 7:56:27 PM (GMT)]
 */
package vazkii.botania.common.item.relic;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.world.World;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.IRelic;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;

public abstract class ItemRelicBauble extends ItemBauble implements IRelic {

	Achievement achievement;

	public ItemRelicBauble(String name) {
		super(name);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int p_77663_4_, boolean p_77663_5_) {
		if(entityIn instanceof EntityPlayer)
			ItemRelic.updateRelic(stack, (EntityPlayer) entityIn);
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		super.onWornTick(stack, player);
		if(player instanceof EntityPlayer) {
			EntityPlayer ePlayer = (EntityPlayer) player;
			ItemRelic.updateRelic(stack, ePlayer);
			if(ItemRelic.isRightPlayer(ePlayer, stack))
				onValidPlayerWornTick(stack, ePlayer);
		}
	}

	@Override
	public void addHiddenTooltip(ItemStack stack, EntityPlayer player, List<String> infoList, boolean adv) {
		super.addHiddenTooltip(stack, player, infoList, adv);
		ItemRelic.addBindInfo(infoList, stack, player);
	}

	public void onValidPlayerWornTick(ItemStack stack, EntityPlayer player) {
		// NO-OP
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityLivingBase player) {
		return player instanceof EntityPlayer && ItemRelic.isRightPlayer((EntityPlayer) player, stack);
	}

	@Override
	public void bindToUsername(String playerName, ItemStack stack) {
		ItemRelic.bindToUsernameS(playerName, stack);
	}

	@Override
	public String getSoulbindUsername(ItemStack stack) {
		return ItemRelic.getSoulbindUsernameS(stack);
	}

	@Override
	public Achievement getBindAchievement() {
		return achievement;
	}

	@Override
	public void setBindAchievement(Achievement achievement) {
		this.achievement = achievement;
	}

	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return BotaniaAPI.rarityRelic;
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world) {
		return Integer.MAX_VALUE;
	}

}
