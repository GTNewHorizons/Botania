/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 25, 2014, 4:36:13 PM (GMT)]
 */
package vazkii.botania.common.item.rod;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import vazkii.botania.api.item.IAvatarTile;
import vazkii.botania.api.item.IAvatarWieldable;
import vazkii.botania.api.item.IManaProficiencyArmor;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.Botania;
import vazkii.botania.common.brew.ModPotions;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ItemMod;
import vazkii.botania.common.lib.LibItemNames;

public class ItemTornadoRod extends ItemMod implements IManaUsingItem, IAvatarWieldable {

	private static final ResourceLocation avatarOverlay = new ResourceLocation(LibResources.MODEL_AVATAR_TORNADO);

	private static final int FLY_TIME = 20;
	private static final int FALL_MULTIPLIER = 3;
	private static final int MAX_DAMAGE = FLY_TIME * FALL_MULTIPLIER;
	private static final int COST = 350;

	private static final String TAG_FLYING = "flying";

	IIcon iconIdle, iconFlying;

	public ItemTornadoRod() {
		setMaxDamage(MAX_DAMAGE);
		setUnlocalizedName(LibItemNames.TORNADO_ROD);
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) {
		if(entity instanceof EntityPlayer player) {
            player.getCurrentEquippedItem();
			boolean damaged = stack.getItemDamage() > 0;

			if(damaged && !isFlying(stack))
				stack.setItemDamage(stack.getItemDamage() - 1);

			int max = FALL_MULTIPLIER * FLY_TIME;
			if(stack.getItemDamage() >= max) {
				setFlying(stack, false);
				player.stopUsingItem();
			} else if(isFlying(stack)) {
				if(isHeld) {
					player.fallDistance = 0F;
					player.motionY = IManaProficiencyArmor.Helper.hasProficiency(player) ? 1.6 : 1.25;

					player.worldObj.playSoundAtEntity(player, "botania:airRod", 0.1F, 0.25F);
					for(int i = 0; i < 5; i++)
						Botania.proxy.wispFX(player.worldObj, player.posX, player.posY, player.posZ, 0.25F, 0.25F, 0.25F, 0.35F + (float) Math.random() * 0.1F, 0.2F * (float) (Math.random() - 0.5), -0.01F * (float) Math.random(), 0.2F * (float) (Math.random() - 0.5));
				}

				stack.setItemDamage(Math.min(max, stack.getItemDamage() + FALL_MULTIPLIER));
				if(stack.getItemDamage() == MAX_DAMAGE)
					setFlying(stack, false);
			}

			if(damaged)
				player.fallDistance = 0;
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		int meta = stack.getItemDamage();
		if(meta != 0 || ManaItemHandler.requestManaExactForTool(stack, player, COST, false)) {
			player.setItemInUse(stack, getMaxItemUseDuration(stack));
			if(meta == 0) {
				setFlying(stack, true);
				ManaItemHandler.requestManaExactForTool(stack, player, COST, true);
			}
		}

		return stack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {

	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return isFlying(stack) ? iconFlying : iconIdle;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 720000;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		iconIdle = IconHelper.forItem(register, this, 0);
		iconFlying = IconHelper.forItem(register, this, 1);
	}

	public boolean isFlying(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_FLYING, false);
	}

	public void setFlying(ItemStack stack, boolean flying) {
		ItemNBTHelper.setBoolean(stack, TAG_FLYING, flying);
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

	@Override
	public void onAvatarUpdate(IAvatarTile tile, ItemStack stack) {
		TileEntity te = (TileEntity) tile;
		World world = te.getWorldObj();
		if(tile.getCurrentMana() >= COST && tile.isEnabled()) {
			int range = 5;
			int rangeY = 3;
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(te.xCoord + 0.5 - range, te.yCoord + 0.5 - rangeY, te.zCoord + 0.5 - range, te.xCoord + 0.5 + range, te.yCoord + 0.5 + rangeY, te.zCoord + 0.5 + range));
			for(EntityPlayer p : players) {
				if(p.motionY > 0.3 && p.motionY < 2 && !p.isSneaking()) {
					p.motionY = 2.8;

					for(int i = 0; i < 20; i++)
						for(int j = 0; j < 5; j++)
							Botania.proxy.wispFX(p.worldObj, p.posX, p.posY + i, p.posZ, 0.25F, 0.25F, 0.25F, 0.35F + (float) Math.random() * 0.1F, 0.2F * (float) (Math.random() - 0.5), -0.01F * (float) Math.random(), 0.2F * (float) (Math.random() - 0.5));

					if(!world.isRemote) {
						p.worldObj.playSoundAtEntity(p, "botania:dash", 1F, 1F);
						p.addPotionEffect(new PotionEffect(ModPotions.featherfeet.id, 100, 0));
						tile.recieveMana(-COST);
					}
				}
			}
		}
	}

	@Override
	public ResourceLocation getOverlayResource(IAvatarTile tile, ItemStack stack) {
		return avatarOverlay;
	}

}
