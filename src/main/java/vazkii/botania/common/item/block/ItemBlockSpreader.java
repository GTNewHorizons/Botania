/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockSpreader extends ItemBlockWithMetadataAndName {

	public ItemBlockSpreader(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		int meta = stack.getItemDamage();
		// Burst payload / mana loss per tick, mirrors TileSpreader.getBurst
		int transfer = meta == 3 ? 640 : meta == 2 ? 240 : 160;
		int loss = meta == 3 ? 20 : 4;
		infoList.add(StatCollector.translateToLocalFormatted("botaniamisc.spreaderTransfer", transfer));
		infoList.add(StatCollector.translateToLocalFormatted("botaniamisc.spreaderLoss", loss));
	}

}
