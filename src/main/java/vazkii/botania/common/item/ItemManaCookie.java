/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 21, 2014, 8:44:35 PM (GMT)]
 */
package vazkii.botania.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.core.BotaniaCreativeTab;
import vazkii.botania.common.lib.LibItemNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemManaCookie extends ItemFood {

	private IIcon totalBiscuitIcon;

	public ItemManaCookie() {
		super(0, 0.1F, false);
		setPotionEffect(Potion.field_76443_y.id, 1,  0, 1F);
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
		setUnlocalizedName(LibItemNames.MANA_COOKIE);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		player.addStat(ModAchievements.manaCookieEat, 1);
	}

	@Override
	public Item setUnlocalizedName(String name) {
		GameRegistry.registerItem(this, name);
		return super.setUnlocalizedName(name);
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack stack) {
		return super.getUnlocalizedNameInefficiently(stack).replaceAll("item.", "item." + LibResources.PREFIX_MOD);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return stack.getDisplayName().toLowerCase().equals("totalbiscuit") ? totalBiscuitIcon : super.getIconIndex(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		itemIcon = IconHelper.forItem(register, this);
		totalBiscuitIcon = IconHelper.forName(register, "totalBiscuit");
	}

}
