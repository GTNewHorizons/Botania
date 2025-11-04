/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 30, 2015, 10:59:45 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.crafting.recipe.AncientWillRecipe;
import vazkii.botania.common.lib.LibItemNames;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemAncientWill extends ItemMod {

	private static final int SUBTYPES = 6;

	IIcon[] icons;

	public ItemAncientWill() {
		setUnlocalizedName(LibItemNames.ANCIENT_WILL);
		setHasSubtypes(true);
		setMaxStackSize(1);

		GameRegistry.addRecipe(new AncientWillRecipe());
		RecipeSorter.register("botania:ancientWill", AncientWillRecipe.class, Category.SHAPELESS, "");
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < SUBTYPES; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[SUBTYPES];
		for(int i = 0; i < icons.length; i++)
			icons[i] = IconHelper.forItem(register, this, i);
	}

	@Override
	public IIcon getIconFromDamage(int meta) {
		return icons[Math.min(icons.length - 1, meta)];
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.craftToAddWill"), infoList);
		addStringToTooltip(StatCollector.translateToLocal("botania.armorset.will" + stack.getItemDamage() + ".shortDesc"), infoList);
	}

	public void addStringToTooltip(String s, List<String> tooltip) {
		tooltip.add(s.replaceAll("&", "\u00a7"));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + stack.getItemDamage();
	}


}
