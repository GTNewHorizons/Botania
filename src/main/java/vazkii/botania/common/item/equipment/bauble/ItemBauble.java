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

import java.util.HashMap;
import java.util.HashSet;
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

	private static final HashMap<UUID, UUID> itemToPlayerRemote = new HashMap<>();
	private static final HashMap<UUID, UUID> itemToPlayer = new HashMap<>();
	private static final HashSet<UUID> toRemoveItems = new HashSet<>();

	public ItemBauble(String name) {
		super();
		setMaxStackSize(1);
		setUnlocalizedName(name);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if(!EntityDoppleganger.isTruePlayer(par3EntityPlayer))
			return par1ItemStack;

		if(canEquip(par1ItemStack, par3EntityPlayer)) {
			InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(par3EntityPlayer);
			for(int i = 0; i < baubles.getSizeInventory(); i++) {
				if(baubles.isItemValidForSlot(i, par1ItemStack)) {
					ItemStack stackInSlot = baubles.getStackInSlot(i);
					if(stackInSlot == null || ((IBauble) stackInSlot.getItem()).canUnequip(stackInSlot, par3EntityPlayer)) {
						if(!par2World.isRemote) {
							baubles.setInventorySlotContents(i, par1ItemStack.copy());
							if(!par3EntityPlayer.capabilities.isCreativeMode)
								par3EntityPlayer.inventory.setInventorySlotContents(par3EntityPlayer.inventory.currentItem, null);
						}

						if(stackInSlot != null) {
							((IBauble) stackInSlot.getItem()).onUnequipped(stackInSlot, par3EntityPlayer);
							return stackInSlot.copy();
						}
						break;
					}
				}
			}
		}

		return par1ItemStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(GuiScreen.isShiftKeyDown())
			addHiddenTooltip(par1ItemStack, par2EntityPlayer, par3List, par4);
		else addStringToTooltip(StatCollector.translateToLocal("botaniamisc.shiftinfo"), par3List);
	}

	public void addHiddenTooltip(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		BaubleType type = getBaubleType(par1ItemStack);
		addStringToTooltip(StatCollector.translateToLocal("botania.baubletype." + type.name().toLowerCase()), par3List);

		String key = vazkii.botania.client.core.helper.RenderHelper.getKeyDisplayString("Baubles Inventory");

		if(key != null && !key.equals("NONE")) {
			addStringToTooltip(StatCollector.translateToLocal("botania.baubletooltip").replaceAll("%key%", key), par3List);
		}

		ItemStack cosmetic = getCosmeticItem(par1ItemStack);
		if(cosmetic != null)
			addStringToTooltip(String.format(StatCollector.translateToLocal("botaniamisc.hasCosmetic"), cosmetic.getDisplayName()), par3List);

		if(hasPhantomInk(par1ItemStack))
			addStringToTooltip(StatCollector.translateToLocal("botaniamisc.hasPhantomInk"), par3List);
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
		UUID itemUUID = getBaubleUUID(stack);
		if(toRemoveItems.contains(itemUUID)) {
			// this is done like this because on server worn tick gets called after unequip
			// so it would get reapplied
			unapplyItem(itemUUID, player.worldObj.isRemote);
			toRemoveItems.remove(itemUUID);
			return;
		}
		if(!wasPlayerApplied(itemUUID, player.getUniqueID(), player.worldObj.isRemote)) {
			onEquippedOrLoadedIntoWorld(stack, player);
			applyToPlayer(itemUUID, player.getUniqueID(), player.worldObj.isRemote);
		}
	}

	@Override
	public void onEquipped(ItemStack stack, EntityLivingBase player) {
		if(player != null) {
			if(!player.worldObj.isRemote)
				player.worldObj.playSoundAtEntity(player, "botania:equipBauble", 0.1F, 1.3F);

			if(player instanceof EntityPlayer)
				((EntityPlayer) player).addStat(ModAchievements.baubleWear, 1);

			onEquippedOrLoadedIntoWorld(stack, player);
			applyToPlayer(getBaubleUUID(stack), player.getUniqueID(), player.worldObj.isRemote);
		}
	}

	public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
		// NO-OP
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {
		toRemoveItems.add(getBaubleUUID(stack));
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
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack p_77630_1_) {
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

	public static boolean wasPlayerApplied(UUID itemUUID, UUID playerUUID, boolean remote) {
		return playerUUID.equals(remote ? itemToPlayerRemote.get(itemUUID) : itemToPlayer.get(itemUUID));
	}

	public static void applyToPlayer(UUID itemUUID, UUID playerUUID, boolean remote) {
		if(remote) {
			itemToPlayerRemote.put(itemUUID, playerUUID);
		} else {
			itemToPlayer.put(itemUUID, playerUUID);
		}
	}

	public static void unapplyItem(UUID itemUUID, boolean remote) {
		if(remote) {
			itemToPlayerRemote.remove(itemUUID);
		} else {
			itemToPlayer.remove(itemUUID);
		}
	}

	public static void removePlayer(UUID playerUUID) {
		for(UUID item : itemToPlayerRemote.keySet().toArray(new UUID[0])) {
			if(playerUUID.equals(itemToPlayerRemote.get(item))) {
				itemToPlayerRemote.remove(item);
			}
		}
		for(UUID item : itemToPlayer.keySet().toArray(new UUID[0])) {
			if(playerUUID.equals(itemToPlayer.get(item))) {
				itemToPlayer.remove(item);
			}
		}
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
