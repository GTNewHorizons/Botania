/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 28, 2014, 6:43:33 PM (GMT)]
 */
package vazkii.botania.common.block.decor;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.BlockModFlower;
import vazkii.botania.common.integration.coloredlights.ColoredLightHelper;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.Optional;

public class BlockBuriedPetals extends BlockModFlower {

	public BlockBuriedPetals() {
		super(LibBlockNames.BURIED_PETALS);
		setBlockBounds(0F, 0F, 0F, 1F, 0.1F, 1F);
		setLightLevel(0.25F);
	}

	@Override
	@Optional.Method(modid = "easycoloredlights")
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return ColoredLightHelper.getPackedColor(world.getBlockMetadata(x, y, z), originalLight);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int meta = world.getBlockMetadata(x, y, z);
		float[] color = EntitySheep.fleeceColorTable[meta];

		Botania.proxy.setSparkleFXNoClip(true);
		Botania.proxy.sparkleFX(world, x + 0.3 + random.nextFloat() * 0.5, y + 0.1 + random.nextFloat() * 0.1, z + 0.3 + random.nextFloat() * 0.5, color[0], color[1], color[2], random.nextFloat(), 5);
		Botania.proxy.setSparkleFXNoClip(false);
	}

	@Override
	public boolean registerInCreative() {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		blockIcon = IconHelper.forBlock(register, this);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return ModItems.petal;
	}

	@Override
	public int getRenderType() {
		return 0;
	}
}
