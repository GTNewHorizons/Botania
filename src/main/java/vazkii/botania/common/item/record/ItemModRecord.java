/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Apr 11, 2015, 9:56:00 PM (GMT)]
 */
package vazkii.botania.common.item.record;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.core.BotaniaCreativeTab;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.gtnewhorizon.gtnhlib.api.MusicRecordMetadataProvider;

public class ItemModRecord extends ItemRecord implements MusicRecordMetadataProvider {

	private final String file;

	public ItemModRecord(String record, String name) {
		super("botania:" + record);
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
		setUnlocalizedName(name);
		file = "botania:music." + record;
	}

	@Override
	public Item setUnlocalizedName(String name) {
		GameRegistry.registerItem(this, name);
		return super.setUnlocalizedName(name);
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		return super.getUnlocalizedNameInefficiently(stack).replaceAll("item\\.", "item." + LibResources.PREFIX_MOD);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		itemIcon = IconHelper.forItem(register, this);
	}

	@Override
	public ResourceLocation getRecordResource(String name) {
		return new ResourceLocation(file);
	}

	@Override
	public ResourceLocation getMusicRecordResource(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemModRecord)) {
			return null;
		}
		return new ResourceLocation(file);
	}
}
