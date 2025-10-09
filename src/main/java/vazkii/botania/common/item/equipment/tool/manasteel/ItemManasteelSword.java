/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Apr 13, 2014, 7:21:32 PM (GMT)]
 */
package vazkii.botania.common.item.equipment.tool.manasteel;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.core.BotaniaCreativeTab;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.lib.LibItemNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemManasteelSword extends ItemSword implements IManaUsingItem {

	public static final int MANA_PER_DAMAGE = 60;

	public static IIcon elucidatorIcon;

	public ItemManasteelSword() {
		this(BotaniaAPI.manasteelToolMaterial, LibItemNames.MANASTEEL_SWORD);
	}

	public ItemManasteelSword(ToolMaterial mat, String name) {
		super(mat);
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
		setUnlocalizedName(name);
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
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		itemIcon = IconHelper.forItem(register, this);
		elucidatorIcon = IconHelper.forName(register, "elucidator");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase attacker) {
		if(usesMana(stack))
			ToolCommons.damageItem(stack, 1, attacker, getManaPerDamage());
		return true;
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		String name = stack.getDisplayName().toLowerCase().trim();
		return name.equals("the elucidator") ? elucidatorIcon : super.getIconIndex(stack);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
		if(usesMana(stack) && block.getBlockHardness(world, x, y, z) != 0F)
			ToolCommons.damageItem(stack, 1, entity, getManaPerDamage());

		return true;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) {
		if(!world.isRemote && entity instanceof EntityPlayer && stack.getItemDamage() > 0 && ManaItemHandler.requestManaExactForTool(stack, (EntityPlayer) entity, getManaPerDamage() * 2, true))
			stack.setItemDamage(stack.getItemDamage() - 1);
	}

	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		return repairMaterial.getItem() == ModItems.manaResource && repairMaterial.getItemDamage() == 0 ? true : super.getIsRepairable(stack, repairMaterial);
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

}
