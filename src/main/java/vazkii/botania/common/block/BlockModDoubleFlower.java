/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 22, 2015, 7:46:55 PM (GMT)]
 */
package vazkii.botania.common.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.client.lib.LibRenderIDs;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.BotaniaCreativeTab;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.item.block.ItemBlockWithMetadataAndName;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockModDoubleFlower extends BlockDoublePlant implements ILexiconable {

	private static final int COUNT = 8;

	IIcon[] doublePlantTopIcons, doublePlantBottomIcons;
	IIcon[] doublePlantTopIconsAlt, doublePlantBottomIconsAlt;

	final int offset;

	public BlockModDoubleFlower(boolean second) {
		offset = second ? 8 : 0;
		setBlockName(LibBlockNames.DOUBLE_FLOWER + (second ? 2 : 1));
		setHardness(0F);
		setStepSound(soundTypeGrass);
		setTickRandomly(false);
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
	}

	@Override
	public Block setBlockName(String name) {
		if(!name.equals("doublePlant"))
			GameRegistry.registerBlock(this, ItemBlockWithMetadataAndName.class, name);
		return super.setBlockName(name);
	}

	@Override
	public Item getItemDropped(int meta, Random random, int fortune) {
		return null;
	}

	@Override
	public int damageDropped(int meta) {
		return meta & 7;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon func_149888_a(boolean top, int index) {
		return (ConfigHandler.altFlowerTextures ? top ? doublePlantTopIconsAlt : doublePlantBottomIconsAlt : top ? doublePlantTopIcons : doublePlantBottomIcons)[index & 7];
	}

	@Override
	public void func_149889_c(World world, int x, int y, int z, int baseMeta, int flags) {
		world.setBlock(x, y, z, this, baseMeta, flags);
		world.setBlock(x, y + 1, z, this, baseMeta | 8, flags);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		world.setBlock(x, y + 1, z, this, itemIn.getItemDamage() | 8, 2);
	}

	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		return false;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
		if(world.isRemote || player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != Items.shears || func_149887_c(meta))
			harvestBlockCopy(world, player, x, y, z, meta);
	}

	// This is how I get around encapsulation
	public void harvestBlockCopy(World world, EntityPlayer player, int x, int y, int z, int meta) {
		player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
		player.addExhaustion(0.025F);

		if(this.canSilkHarvest(world, player, x, y, z, meta) && EnchantmentHelper.getSilkTouchModifier(player)) {
			ArrayList<ItemStack> items = new ArrayList<>();
			ItemStack itemstack = createStackedBlock(meta);

			if(itemstack != null)
				items.add(itemstack);

			ForgeEventFactory.fireBlockHarvesting(items, world, this, x, y, z, meta, 0, 1.0f, true, player);
			for(ItemStack is : items)
				this.dropBlockAsItem(world, x, y, z, is);
		} else {
			harvesters.set(player);
			int i1 = EnchantmentHelper.getFortuneModifier(player);
			this.dropBlockAsItem(world, x, y, z, meta, i1);
			harvesters.set(null);
		}
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
		if(func_149887_c(meta)) {
			if(world.getBlock(x, y - 1, z) == this) {
				if(!player.capabilities.isCreativeMode) {
					int i1 = world.getBlockMetadata(x, y - 1, z);
					int j1 = func_149890_d(i1);

					if(j1 != 3 && j1 != 2);
					//world.func_147480_a(x, y - 1, z, true);
					else {
						/*if (!world.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
                        {
                            this.func_149886_b(world, x, y, z, i1, player);
                        }*/

						world.setBlockToAir(x, y - 1, z);
					}
				} else world.setBlockToAir(x, y - 1, z);
			}
		} else if(player.capabilities.isCreativeMode && world.getBlock(x, y + 1, z) == this)
			world.setBlock(x, y + 1, z, Blocks.air, 0, 2);

		//super.onBlockHarvested(world, x, y, z, meta, player);
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<>();
		ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z) & 7));
		return ret;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		return new ArrayList<>();
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		boolean top = func_149887_c(meta);
		return (ConfigHandler.altFlowerTextures ? top ? doublePlantTopIconsAlt : doublePlantBottomIconsAlt : top ? doublePlantTopIcons : doublePlantBottomIcons)[meta & 7];
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean top = func_149887_c(meta);
		if(top)
			meta = world.getBlockMetadata(x, y - 1, z);

		return (ConfigHandler.altFlowerTextures ? top ? doublePlantBottomIconsAlt : doublePlantTopIconsAlt : top ? doublePlantBottomIcons : doublePlantTopIcons)[meta & 7];
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		doublePlantTopIcons = new IIcon[COUNT];
		doublePlantBottomIcons = new IIcon[COUNT];
		doublePlantTopIconsAlt = new IIcon[COUNT];
		doublePlantBottomIconsAlt = new IIcon[COUNT];
		for(int i = 0; i < COUNT; i++) {
			int off = offset(i);
			doublePlantTopIcons[i] = IconHelper.forName(register, "flower" + off + "Tall0");
			doublePlantBottomIcons[i] = IconHelper.forName(register, "flower" + off + "Tall1");
			doublePlantTopIconsAlt[i] = IconHelper.forName(register, "flower" + off + "Tall0", BlockModFlower.ALT_DIR);
			doublePlantBottomIconsAlt[i] = IconHelper.forName(register, "flower" + off + "Tall1", BlockModFlower.ALT_DIR);
		}
	}

	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		return 16777215;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < COUNT; ++i)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public int getRenderType() {
		return LibRenderIDs.idDoubleFlower;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int meta = world.getBlockMetadata(x, y, z);
		float[] color = EntitySheep.fleeceColorTable[offset(meta & 7)];

		if(random.nextDouble() < ConfigHandler.flowerParticleFrequency)
			Botania.proxy.sparkleFX(world, x + 0.3 + random.nextFloat() * 0.5, y + 0.5 + random.nextFloat() * 0.5, z + 0.3 + random.nextFloat() * 0.5, color[0], color[1], color[2], random.nextFloat(), 5);
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.flowers;
	}

	int offset(int meta) {
		return meta + offset;
	}

}
