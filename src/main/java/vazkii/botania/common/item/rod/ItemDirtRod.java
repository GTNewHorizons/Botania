/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Apr 11, 2014, 2:53:41 PM (GMT)]
 */
package vazkii.botania.common.item.rod;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import vazkii.botania.api.item.IBlockProvider;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.achievement.ICraftAchievement;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.item.ItemMod;
import vazkii.botania.common.lib.LibItemNames;

public class ItemDirtRod extends ItemMod implements IManaUsingItem, ICraftAchievement, IBlockProvider {

	static final int COST = 75;

	public ItemDirtRod() {
		this(LibItemNames.DIRT_ROD);
	}

	public ItemDirtRod(String name) {
		super();
		setMaxStackSize(1);
		setUnlocalizedName(name);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		return place(stack, player, world, x, y, z, side, subX, subY, subZ, Blocks.dirt, COST, 0.35F, 0.2F, 0.05F);
	}

	public static boolean place(ItemStack tool, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, Block block, int cost, float r, float g, float b) {
		if(ManaItemHandler.requestManaExactForTool(tool, player, cost, false)) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			int entities = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, x + dir.offsetX + 1, y + dir.offsetY + 1, z + dir.offsetZ + 1)).size();

			if(entities == 0) {
				ItemStack stackToPlace = new ItemStack(block);
				stackToPlace.tryPlaceItemIntoWorld(player, world, x, y, z, side, hitX, hitY, hitZ);

				if(stackToPlace.stackSize == 0) {
					ManaItemHandler.requestManaExactForTool(tool, player, cost, true);
					for(int i = 0; i < 6; i++)
						Botania.proxy.sparkleFX(world, x + dir.offsetX + Math.random(), y + dir.offsetY + Math.random(), z + dir.offsetZ + Math.random(), r, g, b, 1F, 5);
				}
			}
		}

		return true;
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
	public Achievement getAchievementOnCraft(ItemStack stack, EntityPlayer player, IInventory matrix) {
		return ModAchievements.dirtRodCraft;
	}

	@Override
	public boolean provideBlock(EntityPlayer player, ItemStack requestor, ItemStack stack, Block block, int meta, boolean doit) {
		if(block == Blocks.dirt && meta == 0)
			return !doit || ManaItemHandler.requestManaExactForTool(requestor, player, COST, true);
		return false;
	}

	@Override
	public int getBlockCount(EntityPlayer player, ItemStack requestor, ItemStack stack, Block block, int meta) {
		if(block == Blocks.dirt && meta == 0)
			return -1;
		return 0;
	}


}