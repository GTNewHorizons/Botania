/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 21, 2014, 4:46:17 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.lib.LibItemNames;
import vazkii.botania.common.lib.LibObfuscation;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVirus extends ItemMod {

	IIcon[] icons;

	private static final int SUBTYPES = 2;

	public ItemVirus() {
		setUnlocalizedName(LibItemNames.VIRUS);
		setHasSubtypes(true);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
		if(entity instanceof EntityHorse horse) {
            if(horse.getHorseType() != 3 && horse.getHorseType() != 4 && horse.isTame()) {
				horse.setHorseType(3 + stack.getItemDamage());
				BaseAttributeMap attributes = horse.getAttributeMap();
				IAttributeInstance movementSpeed = attributes.getAttributeInstance(SharedMonsterAttributes.movementSpeed);
				IAttributeInstance health = attributes.getAttributeInstance(SharedMonsterAttributes.maxHealth);
				health.applyModifier(new AttributeModifier("Ermergerd Virus D:", health.getBaseValue(), 0));
				movementSpeed.applyModifier(new AttributeModifier("Ermergerd Virus D:", movementSpeed.getBaseValue(), 0));
				IAttributeInstance jumpHeight = attributes.getAttributeInstance(ReflectionHelper.<IAttribute, EntityHorse>getPrivateValue(EntityHorse.class, null, LibObfuscation.HORSE_JUMP_STRENGTH));
				jumpHeight.applyModifier(new AttributeModifier("Ermergerd Virus D:", jumpHeight.getBaseValue() * 0.5, 0));
				player.worldObj.playSound(entity.posX + 0.5D, entity.posY + 0.5D, entity.posZ + 0.5D, "mob.zombie.remedy", 1.0F + entity.worldObj.rand.nextFloat(), entity.worldObj.rand.nextFloat() * 0.7F + 1.3F, false);

				stack.stackSize--;
				return true;
			}
		}
		return false;
	}

	public void onLivingHurt(LivingHurtEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity.ridingEntity instanceof EntityLivingBase)
			entity = (EntityLivingBase) entity.ridingEntity;

		if(entity instanceof EntityHorse horse && event.source == DamageSource.fall) {
            if((horse.getHorseType() == 3 || horse.getHorseType() == 4) && horse.isTame())
				event.setCanceled(true);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < SUBTYPES; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return icons[Math.min(icons.length - 1, meta)];
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedNameLazy(stack) + stack.getItemDamage();
	}

	String getUnlocalizedNameLazy(ItemStack stack) {
		return super.getUnlocalizedName(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[SUBTYPES];
		for(int i = 0; i < SUBTYPES; i++)
			icons[i] = IconHelper.forItem(register, this, i);
	}

	public class EventHandler{
		@SubscribeEvent
		public void onLivingHurtWrapper(LivingHurtEvent event) {
			ItemVirus.this.onLivingHurt(event);
		}
	}

}
