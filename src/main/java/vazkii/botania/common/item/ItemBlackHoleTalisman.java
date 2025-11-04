/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 31, 2015, 11:04:12 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import vazkii.botania.api.item.IBlockProvider;
import vazkii.botania.client.core.handler.ItemsRemainingRenderHandler;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.core.helper.InventoryHelper;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.crafting.recipe.BlackHoleTalismanExtractRecipe;
import vazkii.botania.common.lib.LibItemNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlackHoleTalisman extends ItemMod implements IBlockProvider {

	private static final String TAG_BLOCK_NAME = "blockName";
	private static final String TAG_BLOCK_META = "blockMeta";
	private static final String TAG_BLOCK_COUNT = "blockCount";

	IIcon enabledIcon;

	public ItemBlackHoleTalisman() {
		setUnlocalizedName(LibItemNames.BLACK_HOLE_TALISMAN);
		setMaxStackSize(1);
		setHasSubtypes(true);

		GameRegistry.addRecipe(new BlackHoleTalismanExtractRecipe());
		RecipeSorter.register("botania:blackHoleTalismanExtract", BlackHoleTalismanExtractRecipe.class, Category.SHAPELESS, "");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(getBlock(stack) != Blocks.air && player.isSneaking()) {
			int dmg = stack.getItemDamage();
			stack.setItemDamage(~dmg & 1);
			world.playSoundAtEntity(player, "random.orb", 0.3F, 0.1F);
		}

		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		boolean set = setBlock(stack, block, meta);

		if(!set) {
			Block bBlock = getBlock(stack);
			int bmeta = getBlockMeta(stack);

			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof IInventory inv) {
                int[] slots = inv instanceof ISidedInventory ? ((ISidedInventory) inv).getAccessibleSlotsFromSide(side) : InventoryHelper.buildSlotsForLinearInventory(inv);
				for(int slot : slots) {
					ItemStack stackInSlot = inv.getStackInSlot(slot);
					if(stackInSlot == null) {
						ItemStack blockStack = new ItemStack(bBlock, 1, bmeta);
						int maxSize = blockStack.getMaxStackSize();
						blockStack.stackSize = remove(stack, maxSize);
						if(blockStack.stackSize != 0) {
							if(inv.isItemValidForSlot(slot, blockStack) && (!(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canInsertItem(slot, blockStack, side))) {
								inv.setInventorySlotContents(slot, blockStack);
								inv.markDirty();
								set = true;
							}
						}
					} else if(stackInSlot.getItem() == Item.getItemFromBlock(bBlock) && stackInSlot.getItemDamage() == bmeta) {
						int maxSize = stackInSlot.getMaxStackSize();
						int missing = maxSize - stackInSlot.stackSize;
						if(inv.isItemValidForSlot(slot, stackInSlot) && (!(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canInsertItem(slot, stackInSlot, side))) {
							stackInSlot.stackSize += remove(stack, missing);
							inv.markDirty();
							set = true;
						}
					}
				}
			} else {
				ForgeDirection dir = ForgeDirection.getOrientation(side);
				int entities = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, x + dir.offsetX + 1, y + dir.offsetY + 1, z + dir.offsetZ + 1)).size();

				if(entities == 0) {
					int remove = player.capabilities.isCreativeMode ? 1 : remove(stack, 1);
					if(remove > 0) {
						ItemStack blockStack = new ItemStack(bBlock, 1, bmeta);
						ItemsRemainingRenderHandler.set(blockStack, getBlockCount(stack));

						Item.getItemFromBlock(bBlock).onItemUse(blockStack, player, world, x, y, z, side, subX, subY, subZ);
						set = true;
					}
				}
			}
		}

		player.setCurrentItemOrArmor(0, stack);
		return set;
	}

	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int invSlot, boolean isHeld) {
		Block block = getBlock(itemstack);
		if(!entity.worldObj.isRemote && itemstack.getItemDamage() == 1 && block != Blocks.air && entity instanceof EntityPlayer player) {
            int meta = getBlockMeta(itemstack);

			int highest = -1;
			int[] counts = new int[player.inventory.getSizeInventory() - player.inventory.armorInventory.length];
			Arrays.fill(counts, 0);

			for(int i = 0; i < counts.length; i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack == null) {
					continue;
				}

				if(Item.getItemFromBlock(block) == stack.getItem() && stack.getItemDamage() == meta) {
					counts[i] = stack.stackSize;
					if(highest == -1)
						highest = i;
					else highest = counts[i] > counts[highest] && highest > 8 ? i : highest;
				}
			}

			if(highest == -1) {
				/*ItemStack heldItem = player.inventory.getItemStack();
				if(hasFreeSlot && (heldItem == null || Item.getItemFromBlock(block) == heldItem.getItem() || heldItem.getItemDamage() != meta)) {
					ItemStack stack = new ItemStack(block, remove(itemstack, 64), meta);
					if(stack.stackSize != 0)
						player.inventory.addItemStackToInventory(stack);
				}*/
				// Used to keep one stack, disabled for now
			} else {
				for(int i = 0; i < counts.length; i++) {
					int count = counts[i];

					// highest is used to keep one stack, disabled for now
					if(/*i == highest || */count == 0)
						continue;

					add(itemstack, count);
					player.inventory.setInventorySlotContents(i, null);
				}

				/*int countInHighest = counts[highest];
				int maxSize = new ItemStack(block, 1, meta).getMaxStackSize();
				if(countInHighest < maxSize) {
					int missing = maxSize - countInHighest;
					ItemStack stackInHighest = player.inventory.getStackInSlot(highest);
					stackInHighest.stackSize += remove(itemstack, missing);
				}*/
				// Used to keep one stack, disabled for now
			}
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		Block block = getBlock(stack);
		int meta = getBlockMeta(stack);
		ItemStack blockStack = new ItemStack(block, 1, meta);

		return super.getItemStackDisplayName(stack) + (blockStack == null || blockStack.getItem() == null ? "" : " (" + EnumChatFormatting.GREEN + blockStack.getDisplayName() + EnumChatFormatting.RESET + ")");
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		int count = getBlockCount(itemStack);
		if(count == 0)
			return null;

		int extract = Math.min(64, count);
		ItemStack copy = itemStack.copy();
		remove(copy, extract);

		int dmg = copy.getItemDamage();
		if(dmg == 1)
			copy.setItemDamage(0);

		return copy;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return getContainerItem(stack) != null;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
		return false;
	}

	private boolean setBlock(ItemStack stack, Block block, int meta) {
		if(getBlock(stack) == Blocks.air || getBlockCount(stack) == 0) {
			ItemNBTHelper.setString(stack, TAG_BLOCK_NAME, Block.blockRegistry.getNameForObject(block));
			ItemNBTHelper.setInt(stack, TAG_BLOCK_META, meta);
			return true;
		}
		return false;
	}

	private void add(ItemStack stack, int count) {
		int current = getBlockCount(stack);
		setCount(stack, current + count);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		itemIcon = IconHelper.forItem(register, this, 0);
		enabledIcon = IconHelper.forItem(register, this, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
		return meta == 1 ? enabledIcon : itemIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		Block block = getBlock(stack);
		if(block != null && block != Blocks.air) {
			int count = getBlockCount(stack);
			infoList.add(count + " " + StatCollector.translateToLocal(new ItemStack(block, 1, getBlockMeta(stack)).getUnlocalizedName() + ".name"));
		}

		if(stack.getItemDamage() == 1)
			addStringToTooltip(StatCollector.translateToLocal("botaniamisc.active"), infoList);
		else addStringToTooltip(StatCollector.translateToLocal("botaniamisc.inactive"), infoList);
	}

	void addStringToTooltip(String s, List<String> tooltip) {
		tooltip.add(s.replaceAll("&", "\u00a7"));
	}

	private static void setCount(ItemStack stack, int count) {
		ItemNBTHelper.setInt(stack, TAG_BLOCK_COUNT, count);
	}

	public static int remove(ItemStack stack, int count) {
		int current = getBlockCount(stack);
		setCount(stack, Math.max(current - count, 0));

		return Math.min(current, count);
	}

	public static String getBlockName(ItemStack stack) {
		return ItemNBTHelper.getString(stack, TAG_BLOCK_NAME, "");
	}

	public static Block getBlock(ItemStack stack) {
		Block block = Block.getBlockFromName(getBlockName(stack));
		return block;
	}

	public static int getBlockMeta(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_BLOCK_META, 0);
	}

	public static int getBlockCount(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_BLOCK_COUNT, 0);
	}

	@Override
	public boolean provideBlock(EntityPlayer player, ItemStack requestor, ItemStack stack, Block block, int meta, boolean doit) {
		Block stored = getBlock(stack);
		int storedMeta = getBlockMeta(stack);
		if(stored == block && storedMeta == meta) {
			int count = getBlockCount(stack);
			if(count > 0) {
				if(doit)
					setCount(stack, count - 1);
				return true;
			}
		}

		return false;
	}

	@Override
	public int getBlockCount(EntityPlayer player, ItemStack requestor, ItemStack stack, Block block, int meta) {
		Block stored = getBlock(stack);
		int storedMeta = getBlockMeta(stack);
		if(stored == block && storedMeta == meta)
			return getBlockCount(stack);
		return 0;
	}

}
