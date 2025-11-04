/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 11, 2014, 1:13:36 AM (GMT)]
 */
package vazkii.botania.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import vazkii.botania.common.block.decor.quartz.BlockSpecialQuartz;
import vazkii.botania.common.core.handler.ConfigHandler;

import java.util.List;

public class ItemBlockSpecialQuartz extends ItemMultiTexture {

	public ItemBlockSpecialQuartz(Block block) {
		super(block, block, new String[]{ "" });
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
        String[] names = ((BlockSpecialQuartz) field_150939_a).getNames();
        return stack.getItemDamage() >= names.length ? names[names.length - 1] : names[stack.getItemDamage()];
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		if (ConfigHandler.noMobSpawnOnBlocks)
			infoList.add(StatCollector.translateToLocal("nomobspawnsonthisblock.tip"));
	}
}