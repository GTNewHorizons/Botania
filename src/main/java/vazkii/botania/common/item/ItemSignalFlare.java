/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 3, 2014, 6:49:15 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntitySignalFlare;
import vazkii.botania.common.lib.LibItemNames;

public class ItemSignalFlare extends ItemMod {

	IIcon[] icons;

	private static final String TAG_COLOR = "color";

	public ItemSignalFlare() {
		super();
		setMaxStackSize(1);
		setNoRepair();
		setMaxDamage(200);
		setUnlocalizedName(LibItemNames.SIGNAL_FLARE);
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(stack.getItemDamage() == 0) {
			if(world.isRemote)
				player.swingItem();
			else {
				EntitySignalFlare flare = new EntitySignalFlare(world);
				flare.setPosition(player.posX, player.posY, player.posZ);
				flare.setColor(getColor(stack));
				world.playSoundAtEntity(player, "random.explode", 40F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

				world.spawnEntityInWorld(flare);

				int stunned = 0;
				int range = 5;
				List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range));
				for(EntityLivingBase entity : entities)
					if(entity != player && (!(entity instanceof EntityPlayer) || MinecraftServer.getServer() == null || MinecraftServer.getServer().isPVPEnabled())) {
						entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 50, 5));
						stunned++;
					}

				if(stunned >= 100)
					player.addStat(ModAchievements.signalFlareStun, 1);
			}
			stack.damageItem(200, player);
		}

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) {
		if(stack.isItemDamaged())
			stack.setItemDamage(stack.getItemDamage() - 1);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[2];
		for(int i = 0; i < icons.length; i++)
			icons[i] = IconHelper.forItem(register, this, i);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return icons[Math.min(1, pass)];
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if(renderPass == 0)
			return 0xFFFFFF;

		int colorv = getColor(stack);
		if(colorv >= EntitySheep.fleeceColorTable.length || colorv < 0)
			return 0xFFFFFF;

		float[] color = EntitySheep.fleeceColorTable[getColor(stack)];
		return new Color(color[0], color[1], color[2]).getRGB();
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < 16; i++)
			list.add(forColor(i));
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		int storedColor = getColor(stack);
		infoList.add(String.format(StatCollector.translateToLocal("botaniamisc.flareColor"), StatCollector.translateToLocal("botania.color" + storedColor)));
	}

	public static ItemStack forColor(int color) {
		ItemStack stack = new ItemStack(ModItems.signalFlare);
		ItemNBTHelper.setInt(stack, TAG_COLOR, color);

		return stack;
	}

	public static int getColor(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR, 0xFFFFFF);
	}
}