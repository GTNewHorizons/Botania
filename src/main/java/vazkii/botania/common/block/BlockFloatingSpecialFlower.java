/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 17, 2014, 5:31:53 PM (GMT)]
 */
package vazkii.botania.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.ISpecialFlower;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.decor.BlockFloatingFlower;
import vazkii.botania.common.block.tile.TileFloatingSpecialFlower;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.block.tile.TileSpecialFlower;
import vazkii.botania.common.crafting.recipe.SpecialFloatingFlowerRecipe;
import vazkii.botania.common.integration.coloredlights.LightHelper;
import vazkii.botania.common.item.block.ItemBlockFloatingSpecialFlower;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockFloatingSpecialFlower extends BlockFloatingFlower implements ISpecialFlower, IWandable, ILexiconable, IWandHUD {

	public BlockFloatingSpecialFlower() {
		super(LibBlockNames.FLOATING_SPECIAL_FLOWER);

		GameRegistry.addRecipe(new SpecialFloatingFlowerRecipe());
		RecipeSorter.register("botania:floatingSpecialFlower", SpecialFloatingFlowerRecipe.class, Category.SHAPELESS, "");
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int currentLight = ((TileSpecialFlower) world.getTileEntity(x, y, z)).getLightValue();
		if(currentLight == -1)
			currentLight = originalLight;
		return LightHelper.getPackedColor(world.getBlockMetadata(x, y, z), currentLight);
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return ((TileSpecialFlower) world.getTileEntity(x, y, z)).getComparatorInputOverride(side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return ((TileSpecialFlower) world.getTileEntity(x, y, z)).getPowerLevel(side);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		// NO-OP
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(String s : BotaniaAPI.subtilesForCreativeMenu) {
			list.add(ItemBlockSpecialFlower.ofType(new ItemStack(item), s));
			if(BotaniaAPI.miniFlowers.containsKey(s))
				list.add(ItemBlockSpecialFlower.ofType(new ItemStack(item), BotaniaAPI.miniFlowers.get(s)));
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		String name = ((TileSpecialFlower) world.getTileEntity(x, y, z)).subTileName;
		return ItemBlockSpecialFlower.ofType(new ItemStack(world.getBlock(x, y, z)), name);
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		if(!player.capabilities.isCreativeMode) {
			dropBlockAsItem(world, x, y, z, meta, 0);
			((TileSpecialFlower) world.getTileEntity(x, y, z)).onBlockHarvested(world, x, y, z, meta, player);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<>();
		TileEntity tile = world.getTileEntity(x, y, z);

		if(tile != null) {
			String name = ((TileSpecialFlower) tile).subTileName;
			list.add(ItemBlockSpecialFlower.ofType(new ItemStack(world.getBlock(x, y, z)), name));
			((TileSpecialFlower) tile).getDrops(list);
		}

		return list;
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventData) {
		super.onBlockEventReceived(world, x, y, z, eventId, eventData);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null ? tileentity.receiveClientEvent(eventId, eventData) : false;
	}

	@Override
	public boolean onUsedByWand(EntityPlayer player, ItemStack stack, World world, int x, int y, int z, int side) {
		return ((TileSpecialFlower) world.getTileEntity(x, y, z)).onWanded(stack, player);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		((TileSpecialFlower) world.getTileEntity(x, y, z)).onBlockPlacedBy(world, x, y, z, entity, stack);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return ((TileSpecialFlower) world.getTileEntity(x, y, z)).onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ) || super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		((TileSpecialFlower) world.getTileEntity(x, y, z)).onBlockAdded(world, x, y, z);
	}

	@Override
	public void renderHUD(Minecraft mc, ScaledResolution res, World world, int x, int y, int z) {
		((TileSpecialFlower) world.getTileEntity(x, y, z)).renderHUD(mc, res);
	}

	@Override
	protected void register(String name) {
		GameRegistry.registerBlock(this, ItemBlockFloatingSpecialFlower.class, name);
	}

	@Override
	public TileMod createNewTileEntity(World world, int meta) {
		return new TileFloatingSpecialFlower();
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return ((TileSpecialFlower) world.getTileEntity(x, y, z)).getEntry();
	}
}
