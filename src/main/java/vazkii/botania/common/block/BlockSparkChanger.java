/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 28, 2015, 10:01:01 PM (GMT)]
 */
package vazkii.botania.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.block.tile.TileSparkChanger;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;

public class BlockSparkChanger extends BlockModContainer<TileSparkChanger> implements ILexiconable {

	IIcon[] icons;
	Random random;

	public BlockSparkChanger() {
		super(Material.rock);
		setBlockBounds(0F, 0F, 0F, 1F, 3F / 16F, 1F);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setBlockName(LibBlockNames.SPARK_CHANGER);

		random = new Random();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess worldIn, int x, int y, int z) {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		icons = new IIcon[3];
		for(int i = 0; i < icons.length; i++)
			icons[i] = IconHelper.forBlock(register, this, i);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[Math.min(2, side)];
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		boolean power = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
		int meta = world.getBlockMetadata(x, y, z);
		boolean powered = (meta & 8) != 0;

		if(power && !powered) {
			((TileSparkChanger) world.getTileEntity(x, y, z)).doSwap();
			world.setBlockMetadataWithNotify(x, y, z, meta | 8, 4);
		} else if(!power && powered)
			world.setBlockMetadataWithNotify(x, y, z, meta & -9, 4);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int s, float xs, float ys, float zs) {
		TileSparkChanger changer = (TileSparkChanger) world.getTileEntity(x, y, z);
		ItemStack cstack = changer.getStackInSlot(0);
		ItemStack pstack = player.getCurrentEquippedItem();
		if(cstack != null) {
			changer.setInventorySlotContents(0, null);
			world.func_147453_f(x, y, z, this);
			changer.markDirty();
			if(!player.inventory.addItemStackToInventory(cstack))
				player.dropPlayerItemWithRandomChoice(cstack, false);
			return true;
		} else if(pstack != null && pstack.getItem() == ModItems.sparkUpgrade) {
			changer.setInventorySlotContents(0, pstack.copy().splitStack(1));
			world.func_147453_f(x, y, z, this);
			changer.markDirty();

			pstack.stackSize--;
			if(pstack.stackSize == 0)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);

			return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
		TileSimpleInventory inv = (TileSimpleInventory) world.getTileEntity(x, y, z);

		if (inv != null) {
			for (int j1 = 0; j1 < inv.getSizeInventory(); ++j1) {
				ItemStack itemstack = inv.getStackInSlot(j1);

				if (itemstack != null) {
					float f = random.nextFloat() * 0.8F + 0.1F;
					float f1 = random.nextFloat() * 0.8F + 0.1F;
					EntityItem entityitem;

					for (float f2 = random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
						int k1 = random.nextInt(21) + 10;

						if (k1 > itemstack.stackSize)
							k1 = itemstack.stackSize;

						itemstack.stackSize -= k1;
						entityitem = new EntityItem(world, x + f, y + f1, z + f2, new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
						float f3 = 0.05F;
						entityitem.motionX = (float)random.nextGaussian() * f3;
						entityitem.motionY = (float)random.nextGaussian() * f3 + 0.2F;
						entityitem.motionZ = (float)random.nextGaussian() * f3;

						if (itemstack.hasTagCompound())
							entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
					}
				}
			}

			world.func_147453_f(x, y, z, blockBroken);
		}

		super.breakBlock(world, x, y, z, blockBroken, meta);
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int s) {
		TileSparkChanger changer = (TileSparkChanger) world.getTileEntity(x, y, z);
		ItemStack stack = changer.getStackInSlot(0);
		if(stack == null)
			return 0;
		return stack.getItemDamage() + 1;
	}

	@Override
	public TileSparkChanger createNewTileEntity(World world, int meta) {
		return new TileSparkChanger();
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.sparkChanger;
	}

}
