/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 1, 2014, 6:09:29 PM (GMT)]
 */
package vazkii.botania.common.block.decor;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.block.BlockMod;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;

public class BlockManaGlass extends BlockMod implements ILexiconable {

	public BlockManaGlass() {
		this(LibBlockNames.MANA_GLASS);
	}

	public BlockManaGlass(String name) {
		super(Material.glass);
		setHardness(0.3F);
		setStepSound(soundTypeGlass);
		setLightLevel(1.0F);
		setBlockName(name);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean shouldSideBeRendered1(IBlockAccess worldIn, int x, int y, int z, int side) {
		Block block = worldIn.getBlock(x, y, z);

		return block == this ? false : super.shouldSideBeRendered(worldIn, x, y, z, side);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
		return shouldSideBeRendered1(worldIn, x, y, z, 1 - side);
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.pool;
	}

}
