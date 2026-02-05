/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Apr 20, 2014, 3:30:06 PM (GMT)]
 */
package vazkii.botania.common.item.equipment.bauble;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thaumcraft.api.IRunicArmor;
import vazkii.botania.api.item.ICosmeticAttachable;
import vazkii.botania.api.item.IPhantomInkable;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.entity.EntityDoppleganger;
import vazkii.botania.common.item.ItemMod;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.IRunicArmor")
public abstract class ItemBauble extends ItemMod implements IBauble, ICosmeticAttachable, IPhantomInkable, IRunicArmor {

	private static final String TAG_BAUBLE_UUID_MOST = "baubleUUIDMost";
	private static final String TAG_BAUBLE_UUID_LEAST = "baubleUUIDLeast";
	private static final String TAG_COSMETIC_ITEM = "cosmeticItem";
	private static final String TAG_PHANTOM_INK = "phantomInk";


	public ItemBauble(String name) {
		super();
		setMaxStackSize(1);
		setUnlocalizedName(name);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!EntityDoppleganger.isTruePlayer(player))
			return stack;

		if(canEquip(stack, player)) {
			InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
			for(int i = 0; i < baubles.getSizeInventory(); i++) {
				if(baubles.isItemValidForSlot(i, stack)) {
					ItemStack stackInSlot = baubles.getStackInSlot(i);
					if(stackInSlot == null || ((IBauble) stackInSlot.getItem()).canUnequip(stackInSlot, player)) {
						if(!world.isRemote) {
							baubles.setInventorySlotContents(i, stack.copy());
							if(!player.capabilities.isCreativeMode)
								player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
						}

						if(stackInSlot != null) {
							((IBauble) stackInSlot.getItem()).onUnequipped(stackInSlot, player);
							return stackInSlot.copy();
						}
						break;
					}
				}
			}
		}

		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		if(GuiScreen.isShiftKeyDown())
			addHiddenTooltip(stack, player, infoList, advanced);
		else addStringToTooltip(StatCollector.translateToLocal("botaniamisc.shiftinfo"), infoList);
	}

	public void addHiddenTooltip(ItemStack stack, EntityPlayer player, List<String> infoList, boolean adv) {
		BaubleType type = getBaubleType(stack);
		addStringToTooltip(StatCollector.translateToLocal("botania.baubletype." + type.name().toLowerCase()), infoList);

		String key = vazkii.botania.client.core.helper.RenderHelper.getKeyDisplayString("Baubles Inventory");

		if(key != null && !key.equals("NONE")) {
			addStringToTooltip(StatCollector.translateToLocal("botania.baubletooltip").replaceAll("%key%", key), infoList);
		}

		ItemStack cosmetic = getCosmeticItem(stack);
		if(cosmetic != null)
			addStringToTooltip(String.format(StatCollector.translateToLocal("botaniamisc.hasCosmetic"), cosmetic.getDisplayName()), infoList);

		if(hasPhantomInk(stack))
			addStringToTooltip(StatCollector.translateToLocal("botaniamisc.hasPhantomInk"), infoList);
	}

	public void addStringToTooltip(String s, List<String> tooltip) {
		tooltip.add(s.replaceAll("&", "\u00a7"));
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityLivingBase player) {
		return true;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
		return true;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		// NO-OP
	}

	@Override
	public void onPlayerLoad(ItemStack stack, EntityLivingBase player) {
		onEquippedOrLoadedIntoWorld(stack, player);
	}

	@Override
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
		if(player != null) {
			if(!player.worldObj.isRemote)
				player.worldObj.playSoundAtEntity(player, "botania:equipBauble", 0.1F, 1.3F);

			if(player instanceof EntityPlayer)
				((EntityPlayer) player).addStat(ModAchievements.baubleWear, 1);

			onEquippedOrLoadedIntoWorld(stack, player);
		}
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {
		// NO-OP
	}

	public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
		// NO-OP
	}

	@Override
	public ItemStack getCosmeticItem(ItemStack stack) {
		NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, TAG_COSMETIC_ITEM, true);
		if(cmp == null)
			return null;
		return ItemStack.loadItemStackFromNBT(cmp);
	}

	@Override
	public void setCosmeticItem(ItemStack stack, ItemStack cosmetic) {
		NBTTagCompound cmp = new NBTTagCompound();
		if(cosmetic != null)
			cosmetic.writeToNBT(cmp);
		ItemNBTHelper.setCompound(stack, TAG_COSMETIC_ITEM, cmp);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return getContainerItem(stack) != null;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return getCosmeticItem(itemStack);
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
		return false;
	}

	public static UUID getBaubleUUID(ItemStack stack) {
		long most = ItemNBTHelper.getLong(stack, TAG_BAUBLE_UUID_MOST, 0);
		if(most == 0) {
			UUID uuid = UUID.randomUUID();
			ItemNBTHelper.setLong(stack, TAG_BAUBLE_UUID_MOST, uuid.getMostSignificantBits());
			ItemNBTHelper.setLong(stack, TAG_BAUBLE_UUID_LEAST, uuid.getLeastSignificantBits());
			return getBaubleUUID(stack);
		}

		long least = ItemNBTHelper.getLong(stack, TAG_BAUBLE_UUID_LEAST, 0);
		return new UUID(most, least);
	}

	@Override
	public boolean hasPhantomInk(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_PHANTOM_INK, false);
	}

	@Override
	public void setPhantomInk(ItemStack stack, boolean ink) {
		ItemNBTHelper.setBoolean(stack, TAG_PHANTOM_INK, ink);
	}

	@Override
	@Optional.Method(modid = "Thaumcraft")
	public int getRunicCharge(ItemStack itemstack) {
		return 0;
	}
}
