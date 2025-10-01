/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Oct 31, 2014, 4:37:29 PM (GMT)]
 */
package vazkii.botania.common.block.mana;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.lib.LibRenderIDs;
import vazkii.botania.common.block.BlockModContainer;
import vazkii.botania.common.block.tile.TileBrewery;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;

public class BlockBrewery extends BlockModContainer<TileBrewery> implements ILexiconable, IWandHUD {

	Random random;

	public BlockBrewery() {
		super(Material.rock);
		float f = 6F / 16F;
		setBlockBounds(f, 0.05F, f, 1F - f, 0.95F, 1F - f);
		setBlockName(LibBlockNames.BREWERY);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);

		random = new Random();
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return Blocks.cobblestone.getIcon(side, meta);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		TileBrewery brew = (TileBrewery) world.getTileEntity(x, y, z);

		if(player.isSneaking()) {
			if(brew.recipe == null && world.getBlockMetadata(x, y, z) == 0)
				for(int i = brew.getSizeInventory() - 1; i >= 0; i--) {
					ItemStack stackAt = brew.getStackInSlot(i);
					if(stackAt != null) {
						ItemStack copy = stackAt.copy();
						if(!player.inventory.addItemStackToInventory(copy))
							player.dropPlayerItemWithRandomChoice(copy, false);
						brew.setInventorySlotContents(i, null);
						world.func_147453_f(x, y, z, this);
						break;
					}
				}
		} else {
			ItemStack stack = player.getCurrentEquippedItem();
			if(stack != null)
				return brew.addItem(player, stack);
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
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		TileBrewery brew = (TileBrewery) world.getTileEntity(x, y, z);
		return brew.signal;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		// NO-OP
	}

	@Override
	public int getRenderType() {
		return LibRenderIDs.idBrewery;
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
	public TileBrewery createNewTileEntity(World world, int meta) {
		return new TileBrewery();
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.brewery;
	}

	@Override
	public void renderHUD(Minecraft mc, ScaledResolution res, World world, int x, int y, int z) {
		((TileBrewery) world.getTileEntity(x, y, z)).renderHUD(mc, res);
	}

}
