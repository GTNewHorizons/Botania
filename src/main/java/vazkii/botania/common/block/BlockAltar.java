/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 21, 2014, 7:48:54 PM (GMT)]
 */
package vazkii.botania.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.lib.LibRenderIDs;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileAltar;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockWithMetadataAndName;
import vazkii.botania.common.item.rod.ItemWaterRod;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockAltar extends BlockModContainer<TileAltar> implements ILexiconable {

	Random random;

	protected BlockAltar() {
		super(Material.rock);
		setHardness(3.5F);
		setStepSound(soundTypeStone);
		setBlockName(LibBlockNames.ALTAR);

		float f = 1F / 16F * 2F;
		setBlockBounds(f, f, f, 1F - f, 1F / 16F * 20F, 1F - f);

		random = new Random();
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		// NO-OP
	}

	@Override
	protected boolean shouldRegisterInNameSet() {
		return false;
	}

	@Override
	public Block setBlockName(String name) {
		GameRegistry.registerBlock(this, ItemBlockWithMetadataAndName.class, name);
		return super.setBlockName(name);
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < 9; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity collider) {
		if(collider instanceof EntityItem) {
			TileAltar tile = (TileAltar) world.getTileEntity(x, y, z);
			if (tile == null) return;
			if(tile.collideEntityItem((EntityItem) collider))
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileAltar tile = (TileAltar) world.getTileEntity(x, y, z);
		if (tile == null) return 0;
		return tile.hasLava ? 15 : 0;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		ItemStack stack = player.getCurrentEquippedItem();
		TileAltar tile = (TileAltar) world.getTileEntity(x, y, z);
		if (tile == null) return false;

		if(player.isSneaking()) {
			for(int i = tile.getSizeInventory() - 1; i >= 0; i--) {
				ItemStack stackAt = tile.getStackInSlot(i);
				if(stackAt != null) {
					ItemStack copy = stackAt.copy();
					if(!player.inventory.addItemStackToInventory(copy))
						player.dropPlayerItemWithRandomChoice(copy, false);
					tile.setInventorySlotContents(i, null);
					world.func_147453_f(x, y, z, this);
					break;
				}
			}
		} else if(tile.isEmpty() && tile.hasWater && stack == null)
			tile.trySetLastRecipe(player);
		else {
			if(stack != null && (isValidWaterContainer(stack) || stack.getItem() == ModItems.waterRod && ManaItemHandler.requestManaExact(stack, player, ItemWaterRod.COST, false))) {
				if(!tile.hasWater) {
					if(stack.getItem() == ModItems.waterRod)
						ManaItemHandler.requestManaExact(stack, player, ItemWaterRod.COST, true);
					else if(!player.capabilities.isCreativeMode)
						player.inventory.setInventorySlotContents(player.inventory.currentItem, getContainer(stack));

					tile.setWater(true);
					world.func_147453_f(x, y, z, this);
				}

				return true;
			} else if(stack != null && stack.getItem() == Items.lava_bucket) {
				if(!player.capabilities.isCreativeMode)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, getContainer(stack));

				tile.setLava(true);
				tile.setWater(false);
				world.func_147453_f(x, y, z, this);

				return true;
			} else if(stack != null && stack.getItem() == Items.bucket && (tile.hasWater || tile.hasLava) && !Botania.gardenOfGlassLoaded) {
				ItemStack bucket = tile.hasLava ? new ItemStack(Items.lava_bucket) : new ItemStack(Items.water_bucket);
				if(stack.stackSize == 1)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, bucket);
				else {
					if(!player.inventory.addItemStackToInventory(bucket))
						player.dropPlayerItemWithRandomChoice(bucket, false);
					stack.stackSize--;
				}

				if(tile.hasLava)
					tile.setLava(false);
				else tile.setWater(false);
				world.func_147453_f(x, y, z, this);

				return true;
			}
		}

		return false;
	}

	@Override
	public void fillWithRain(World world, int x, int y, int z) {
		if(world.rand.nextInt(20) == 1) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileAltar altar) {
				if(!altar.hasLava && !altar.hasWater)
					altar.setWater(true);
				world.func_147453_f(x, y, z, this);
			}
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	private boolean isValidWaterContainer(ItemStack stack) {
		if(stack == null || stack.stackSize != 1)
			return false;
		if(stack.getItem() == ModItems.waterBowl)
			return true;

		if(stack.getItem() instanceof IFluidContainerItem) {
			FluidStack fluidStack = ((IFluidContainerItem) stack.getItem()).getFluid(stack);
			return fluidStack != null && fluidStack.getFluid() == FluidRegistry.WATER && fluidStack.amount >= FluidContainerRegistry.BUCKET_VOLUME;
		}
		FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
		return fluidStack != null && fluidStack.getFluid() == FluidRegistry.WATER && fluidStack.amount >= FluidContainerRegistry.BUCKET_VOLUME;
	}

	private ItemStack getContainer(ItemStack stack) {
		if(stack.getItem() == ModItems.waterBowl)
			return new ItemStack(Items.bowl);

		if (stack.getItem().hasContainerItem(stack))
			return stack.getItem().getContainerItem(stack);
		else if (stack.getItem() instanceof IFluidContainerItem) {
			((IFluidContainerItem) stack.getItem()).drain(stack, FluidContainerRegistry.BUCKET_VOLUME, true);
			return stack;
		}
		return FluidContainerRegistry.drainFluidContainer(stack);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return meta == 0 ? Blocks.cobblestone.getIcon(side, meta) : ModFluffBlocks.biomeStoneA.getIcon(side, meta + 7);
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
	public int getRenderType() {
		return LibRenderIDs.idAltar;
	}

	@Override
	public TileAltar createNewTileEntity(World world, int meta) {
		return new TileAltar();
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
		TileAltar altar = (TileAltar) world.getTileEntity(x, y, z);
		if (altar == null) return 0;
		return altar.hasWater ? 15 : 0;
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.apothecary;
	}

}
