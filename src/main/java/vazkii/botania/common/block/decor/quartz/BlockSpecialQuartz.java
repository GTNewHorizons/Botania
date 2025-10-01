/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 11, 2014, 1:05:32 AM (GMT)]
 */
package vazkii.botania.common.block.decor.quartz;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.block.BlockMod;
import vazkii.botania.common.block.ModFluffBlocks;
import vazkii.botania.common.item.block.ItemBlockSpecialQuartz;
import vazkii.botania.common.lexicon.LexiconData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpecialQuartz extends BlockMod implements ILexiconable {

	private final String[] iconNames;
	public final String type;
	private IIcon[] specialQuartzIcons;
	private IIcon chiseledSpecialQuartzIcon;
	private IIcon pillarSpecialQuartzIcon;
	private IIcon specialQuartzTopIcon;

	public BlockSpecialQuartz(String type) {
		super(Material.rock);
		this.type = type;
		iconNames = new String[]{ "block" + type + "Quartz0", "chiseled" + type + "Quartz0", "pillar" + type + "Quartz0", null, null };
		setHardness(0.8F);
		setResistance(10F);
		setBlockName("quartzType" + type);
	}

	@Override
	public Block setBlockName(String name) {
		GameRegistry.registerBlock(this, ItemBlockSpecialQuartz.class, name);
		return super.setBlockName(name);
	}

	@Override
	protected boolean shouldRegisterInNameSet() {
		return false;
	}

	public String[] getNames() {
		return new String[] {
				"tile.botania:block" + type + "Quartz",
				"tile.botania:chiseled" + type + "Quartz",
				"tile.botania:pillar" + type + "Quartz",
		};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (meta != 2 && meta != 3 && meta != 4) {
			if (side != 1 && (side != 0 || meta != 1)) {
				if (side == 0)
					return specialQuartzTopIcon;
				else {
					if (meta < 0 || meta >= specialQuartzIcons.length)
						meta = 0;

					return specialQuartzIcons[meta];
				}
			} else return meta == 1 ? chiseledSpecialQuartzIcon : specialQuartzTopIcon;
		} else
			return meta == 2 && (side == 1 || side == 0) ? pillarSpecialQuartzIcon : meta == 3 && (side == 5 || side == 4) ? pillarSpecialQuartzIcon : meta == 4 && (side == 2 || side == 3) ? pillarSpecialQuartzIcon : specialQuartzIcons[meta];
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
		if (meta == 2) {
			switch (side) {
			case 0:
			case 1:
				meta = 2;
				break;
			case 2:
			case 3:
				meta = 4;
				break;
			case 4:
			case 5:
				meta = 3;
			}
		}

		return meta;
	}

	@Override
	public int damageDropped(int meta) {
		return meta != 3 && meta != 4 ? meta : 2;
	}

	@Override
	public ItemStack createStackedBlock(int meta) {
		return meta != 3 && meta != 4 ? super.createStackedBlock(meta) : new ItemStack(this, 1, 2);
	}

	@Override
	public int getRenderType() {
		return 39;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
		list.add(new ItemStack(this, 1, 2));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		specialQuartzIcons = new IIcon[iconNames.length];

		for (int i = 0; i < specialQuartzIcons.length; ++i) {
			if (iconNames[i] == null)
				specialQuartzIcons[i] = specialQuartzIcons[i - 1];
			else specialQuartzIcons[i] = IconHelper.forName(register, iconNames[i]);
		}

		specialQuartzTopIcon = IconHelper.forName(register, "block"  + type + "Quartz1");
		chiseledSpecialQuartzIcon = IconHelper.forName(register, "chiseled" + type + "Quartz1");
		pillarSpecialQuartzIcon = IconHelper.forName(register, "pillar" + type + "Quartz1");
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return this == ModFluffBlocks.elfQuartz ? LexiconData.elvenResources : LexiconData.decorativeBlocks;
	}
}
