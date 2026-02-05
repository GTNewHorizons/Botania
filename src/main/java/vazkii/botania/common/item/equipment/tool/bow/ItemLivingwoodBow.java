/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Feb 21, 2015, 4:58:45 PM (GMT)]
 */
package vazkii.botania.common.item.equipment.tool.bow;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
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

public class ItemLivingwoodBow extends ItemBow implements IManaUsingItem {

	public static final int MANA_PER_DAMAGE = 40;
	IIcon[] pullIcons = new IIcon[3];

	public ItemLivingwoodBow() {
		this(LibItemNames.LIVINGWOOD_BOW);
	}

	public ItemLivingwoodBow(String name) {
		super();
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
		setUnlocalizedName(name);
		setMaxDamage(500);
		setFull3D();
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
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		ArrowNockEvent event = new ArrowNockEvent(player, stack);
		MinecraftForge.EVENT_BUS.post(event);
		if(event.isCanceled())
			return event.result;

		if(canFire(stack, world, player, 0))
			player.setItemInUse(stack, getMaxItemUseDuration(stack));

		return stack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemInUseCount) {
		int j = (int) ((getMaxItemUseDuration(stack) - itemInUseCount) * chargeVelocityMultiplier());

		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
		MinecraftForge.EVENT_BUS.post(event);
		if(event.isCanceled())
			return;
		j = event.charge;

		boolean flag = canFire(stack, world, player, itemInUseCount);
		boolean infinity = EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		if(flag) {
			float f = j / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if(f < 0.1D)
				return;

			if(f > 1.0F)
				f = 1.0F;

			EntityArrow entityarrow = makeArrow(stack, world, player, itemInUseCount, f);

			if(f == 1.0F)
				entityarrow.setIsCritical(true);

			int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if(k > 0)
				entityarrow.setDamage(entityarrow.getDamage() + k * 0.5D + 0.5D);

			int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if(l > 0)
				entityarrow.setKnockbackStrength(l);

			if(EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
				entityarrow.setFire(100);

			ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

			onFire(stack, world, player, itemInUseCount, infinity, entityarrow);

			if(!world.isRemote)
				world.spawnEntityInWorld(entityarrow);
		}
	}

	float chargeVelocityMultiplier() {
		return 1F;
	}

	boolean postsEvent() {
		return true;
	}

	EntityArrow makeArrow(ItemStack stack, World world, EntityPlayer player, int itemInUseCount, float f) {
		return new EntityArrow(world, player, f * 2.0F);
	}

	boolean canFire(ItemStack stack, World world, EntityPlayer player, int itemInUseCount) {
		return player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0 || player.inventory.hasItem(Items.arrow);
	}

	void onFire(ItemStack stack, World world, EntityPlayer player, int itemInUseCount, boolean infinity, EntityArrow arrow) {
		if(infinity)
			arrow.canBePickedUp = 2;
		else player.inventory.consumeInventoryItem(Items.arrow);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		itemIcon = IconHelper.forItem(register, this, 0);
		for(int i = 0; i < 3; i++)
			pullIcons[i] = IconHelper.forItem(register, this, i + 1);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) {
		if(!world.isRemote && entity instanceof EntityPlayer && stack.getItemDamage() > 0 && ManaItemHandler.requestManaExactForTool(stack, (EntityPlayer) entity, MANA_PER_DAMAGE * 2, true))
			stack.setItemDamage(stack.getItemDamage() - 1);
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		return repairMaterial.getItem() == ModItems.manaResource && repairMaterial.getItemDamage() == 3 ? true : super.getIsRepairable(stack, repairMaterial);
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if(stack != usingItem)
			return itemIcon;

		int j = (int) ((getMaxItemUseDuration(stack) - useRemaining) * chargeVelocityMultiplier());

		if(j >= 18)
			return pullIcons[2];
		if(j > 13)
			return pullIcons[1];
		if(j > 0)
			return pullIcons[0];

		return itemIcon;
	}

}
