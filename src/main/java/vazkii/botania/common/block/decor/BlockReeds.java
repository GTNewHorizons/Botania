/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 28, 2014, 8:02:49 PM (GMT)]
 */
package vazkii.botania.common.block.decor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.core.BotaniaCreativeTab;
import vazkii.botania.common.item.block.ItemCubeMod;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReeds extends BlockRotatedPillar implements ILexiconable {

	IIcon topIcon;

	public BlockReeds() {
		super(Material.wood);
		setHardness(1.0F);
		setStepSound(soundTypeWood);
		setBlockName(LibBlockNames.REED_BLOCK);
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
	}


	@Override
	public Block setBlockName(String name) {
		GameRegistry.registerBlock(this, ItemCubeMod.class, name);
		return super.setBlockName(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = IconHelper.forBlock(register, this, 0);
		topIcon = IconHelper.forBlock(register, this, 1);
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.decorativeBlocks;
	}

	@Override
	protected IIcon getSideIcon(int lowBits) {
		return blockIcon;
	}

	@Override
	protected IIcon getTopIcon(int lowBits) {
		return topIcon;
	}

}
