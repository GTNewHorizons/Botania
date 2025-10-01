/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Sep 23, 2015, 11:44:35 PM (GMT)]
 */
package vazkii.botania.common.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.common.block.tile.TileGaiaHead;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockMod;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGaiaHead extends BlockSkull {

	public BlockGaiaHead() {
		setBlockName(LibBlockNames.GAIA_HEAD);
		setHardness(1.0F);
	}

	@Override
	public Block setBlockName(String name) {
		GameRegistry.registerBlock(this, ItemBlockMod.class, name);
		return super.setBlockName(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return ModItems.gaiaHead;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		// NO-OP
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();

		if((meta & 8) == 0) {
			ItemStack itemstack = new ItemStack(ModItems.gaiaHead, 1);
			TileEntitySkull tileentityskull = (TileEntitySkull)world.getTileEntity(x, y, z);

			if(tileentityskull == null)
				return ret;

			ret.add(itemstack);
		}
		return ret;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModItems.gaiaHead;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z)  {
		return 0;
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileGaiaHead();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return Blocks.coal_block.getBlockTextureFromSide(side);
	}

}
