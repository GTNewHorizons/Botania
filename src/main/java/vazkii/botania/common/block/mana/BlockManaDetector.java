/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 10, 2014, 7:57:38 PM (GMT)]
 */
package vazkii.botania.common.block.mana;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.internal.IManaBurst;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.block.BlockModContainer;
import vazkii.botania.common.block.tile.mana.TileManaDetector;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;

public class BlockManaDetector extends BlockModContainer<TileManaDetector> implements ILexiconable {

	IIcon[] icons;

	public BlockManaDetector() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(Block.soundTypeStone);
		setBlockName(LibBlockNames.MANA_DETECTOR);
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		icons = new IIcon[2];
		for(int i = 0; i < icons.length; i++)
			icons[i] = IconHelper.forBlock(register, this, i);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return icons[Math.min(icons.length - 1, meta)];
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
		return worldIn.getBlockMetadata(x, y, z) != 0 ? 15 : 0;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider) {
		if(collider != null && !(collider instanceof IManaBurst))
			super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
	}

	@Override
	public TileManaDetector createNewTileEntity(World world, int meta) {
		return new TileManaDetector();
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.manaDetector;
	}

}
