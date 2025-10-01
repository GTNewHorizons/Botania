/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 7, 2014, 2:20:51 PM (GMT)]
 */
package vazkii.botania.common.block;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.common.block.tile.TileCamo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockCamo extends BlockModContainer<TileCamo> {

	static List<Integer> validRenderTypes = Arrays.asList(0, 31, 39);

	protected BlockCamo(Material material) {
		super(material);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		if(tile instanceof TileCamo) {
			TileCamo camo = (TileCamo) tile;
			Block block = camo.camo;
			if(block != null && isValidBlock(block))
				return block.getIcon(side, camo.camoMeta);
		}

		return getIconFromSideAfterCheck(tile, meta, side);
	}

	public static boolean isValidBlock(Block block) {
		return block.isOpaqueCube() || isValidRenderType(block.getRenderType());
	}

	public static boolean isValidRenderType(int type) {
		return validRenderTypes.contains(type);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		TileEntity tile = world.getTileEntity(x, y, z);

		if(tile instanceof TileCamo) {
			TileCamo camo = (TileCamo) tile;
			ItemStack currentStack = player.getCurrentEquippedItem();

			if(currentStack == null)
				currentStack = new ItemStack(Block.getBlockFromName("air"), 1, 0);

			boolean doChange = true;
			Block block = null;
			checkChange : {
				if(currentStack.getItem() != Item.getItemFromBlock(Block.getBlockFromName("air"))) {
					if(Item.getIdFromItem(currentStack.getItem()) == 0) {
						doChange = false;
						break checkChange;
					}

					block = Block.getBlockFromItem(currentStack.getItem());
					if(block == null || !isValidBlock(block) || block instanceof BlockCamo || block.getMaterial() == Material.air)
						doChange = false;
				}
			}

			if(doChange && currentStack.getItem() != null) {
				int metadata = currentStack.getItemDamage();
				if(block instanceof BlockDirectional) {
					switch (side) {
					case 0:
					case 1:
						break;
					case 2:
						metadata = metadata & 12 | 2;
						break;
					case 3:
						metadata = metadata & 12;
						break;
					case 4:
						metadata = metadata & 12 | 1;
						break;
					case 5:
						metadata = metadata & 12 | 3;
						break;
					}
				}
				camo.camo = Block.getBlockFromItem(currentStack.getItem());
				camo.camoMeta = metadata;
				world.markBlockForUpdate(x,y,z);

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
		TileEntity tile = worldIn.getTileEntity(x, y, z);
		if(tile instanceof TileCamo) {
			TileCamo camo = (TileCamo) tile;
			Block block = camo.camo;
			if(block != null)
				return block instanceof BlockCamo ? 0xFFFFFF : block.getRenderColor(camo.camoMeta);

		}
		return 0xFFFFFF;
	}

	public IIcon getIconFromSideAfterCheck(TileEntity tile, int meta, int side) {
		return getIcon(side, meta);
	}

}