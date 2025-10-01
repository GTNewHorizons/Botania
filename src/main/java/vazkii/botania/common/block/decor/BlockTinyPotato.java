/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 18, 2014, 7:58:08 PM (GMT)]
 */
package vazkii.botania.common.block.decor;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.client.lib.LibRenderIDs;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.block.BlockModContainer;
import vazkii.botania.common.block.tile.TileTinyPotato;
import vazkii.botania.common.item.block.ItemBlockTinyPotato;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockTinyPotato extends BlockModContainer<TileTinyPotato> implements ILexiconable {

	public BlockTinyPotato() {
		super(Material.cloth);
		setHardness(0.25F);
		setBlockName(LibBlockNames.TINY_POTATO);
		float f = 1F / 16F * 6F;
		setBlockBounds(f, 0, f, 1F - f, f, 1F - f);
	}

	@Override
	public Block setBlockName(String name) {
		GameRegistry.registerBlock(this, ItemBlockTinyPotato.class, name);
		return super.setBlockName(name);
	}

	@Override
	protected boolean shouldRegisterInNameSet() {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		// NO-OP
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return Blocks.hardened_clay.getIcon(0, 0);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTinyPotato) {
			((TileTinyPotato) tile).interact();
			player.addStat(ModAchievements.tinyPotatoPet, 1);
			world.spawnParticle("heart", x + minX + Math.random() * (maxX - minX), y + maxY, z + minZ + Math.random() * (maxZ - minZ), 0, 0 ,0);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		int l1 = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		world.setBlockMetadataWithNotify(x, y, z, l1, 2);
		if (itemIn.hasDisplayName())
			((TileTinyPotato) world.getTileEntity(x, y, z)).name = itemIn.getDisplayName();
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		if(!player.capabilities.isCreativeMode)
			dropBlockAsItem(world, x, y, z, meta, 0);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<>();
		TileEntity tile = world.getTileEntity(x, y, z);

		if(tile != null) {
			ItemStack stack = new ItemStack(this);
			String name = ((TileTinyPotato) tile).name;
			if(!name.isEmpty())
				stack.setStackDisplayName(name);
			list.add(stack);
		}

		return list;
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
		return LibRenderIDs.idTinyPotato;
	}

	@Override
	public TileTinyPotato createNewTileEntity(World world, int meta) {
		return new TileTinyPotato();
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.tinyPotato;
	}
}
