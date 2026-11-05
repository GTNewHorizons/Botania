/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Apr 27, 2014, 8:32:52 PM (GMT)]
 */
package vazkii.botania.common.item.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import vazkii.botania.api.subtile.ManaInfoTier;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.core.handler.ConfigHandler;

public class ItemBlockPool extends ItemBlockWithMetadataAndName {

	public ItemBlockPool(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		int meta = stack.getItemDamage();
		if(meta == 1) {
			for(int i = 0; i < 2; i++)
				infoList.add(StatCollector.translateToLocal("botaniamisc.creativePool" + i));
			return;
		}

		int mode = ConfigHandler.manaInfoTooltips;
		if(mode == 0)
			return;

		boolean diluted = meta == 2;
		if(mode == 2) {
			int capacity = diluted ? TilePool.MAX_MANA_DILLUTED : TilePool.MAX_MANA;
			infoList.add(StatCollector.translateToLocalFormatted("botaniamisc.poolStorage", String.format("%,d", capacity)));
		} else {
			ManaInfoTier tier = diluted ? ManaInfoTier.LOW : ManaInfoTier.HIGH;
			infoList.add(StatCollector.translateToLocalFormatted("botania.manaInfo.storage", StatCollector.translateToLocal(tier.getUnlocalizedName())));
		}
	}

}
