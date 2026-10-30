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
import vazkii.botania.api.subtile.ManaInfoTier;
import vazkii.botania.common.block.tile.mana.TileSpreader;
import vazkii.botania.common.core.handler.ConfigHandler;

public class ItemBlockSpreader extends ItemBlockWithMetadataAndName {

	public ItemBlockSpreader(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		int mode = ConfigHandler.manaInfoTooltips;
		if(mode == 0)
			return;

		int meta = stack.getItemDamage();
		if(mode == 2) {
			int transfer = meta == 3 ? TileSpreader.BURST_MANA_ULTRA : meta == 2 ? TileSpreader.BURST_MANA_DREAMWOOD : TileSpreader.BURST_MANA;
			int loss = (int) (meta == 3 ? TileSpreader.MANA_LOSS_PER_TICK_ULTRA : TileSpreader.MANA_LOSS_PER_TICK);
			infoList.add(StatCollector.translateToLocalFormatted("botaniamisc.spreaderTransfer", transfer));
			infoList.add(StatCollector.translateToLocalFormatted("botaniamisc.spreaderLoss", loss));
		} else {
			ManaInfoTier tier = meta == 3 ? ManaInfoTier.HIGH : meta == 2 ? ManaInfoTier.MODERATE : ManaInfoTier.LOW;
			infoList.add(StatCollector.translateToLocalFormatted("botania.manaInfo.transfer", StatCollector.translateToLocal(tier.getUnlocalizedName())));
		}
	}

}
