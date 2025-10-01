/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 7, 2015, 7:59:10 PM (GMT)]
 */
package vazkii.botania.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibBlockNames;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockFelPumpkin extends BlockMod implements ILexiconable {

	private static final String TAG_FEL_SPAWNED = "Botania-FelSpawned";

	IIcon top, face;

	public BlockFelPumpkin() {
		super(Material.gourd);
		setBlockName(LibBlockNames.FEL_PUMPKIN);
		setHardness(1F);
		setStepSound(soundTypeWood);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? top : side == 0 ? top : meta == 2 && side == 2 ? face : meta == 3 && side == 5 ? face : meta == 0 && side == 3 ? face : meta == 1 && side == 4 ? face : blockIcon;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		if(!world.isRemote && world.getBlock(x, y - 1, z) == Blocks.iron_bars && world.getBlock(x, y - 2, z) == Blocks.iron_bars) {
			world.setBlock(x, y, z, getBlockById(0), 0, 2);
			world.setBlock(x, y - 1, z, getBlockById(0), 0, 2);
			world.setBlock(x, y - 2, z, getBlockById(0), 0, 2);
			EntityBlaze blaze = new EntityBlaze(world);
			blaze.setLocationAndAngles(x + 0.5D, y - 1.95D, z + 0.5D, 0.0F, 0.0F);
			blaze.getEntityData().setBoolean(TAG_FEL_SPAWNED, true);
			world.spawnEntityInWorld(blaze);
			world.notifyBlockChange(x, y, z, getBlockById(0));
			world.notifyBlockChange(x, y - 1, z, getBlockById(0));
			world.notifyBlockChange(x, y - 2, z, getBlockById(0));
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
		int l = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, l, 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)  {
		face = IconHelper.forBlock(register, this);
		top = Blocks.pumpkin.getIcon(0, 0);
		blockIcon = Blocks.pumpkin.getIcon(2, 0);
	}

	public void onDrops(LivingDropsEvent event) {
		if(event.entity instanceof EntityBlaze && event.entity.getEntityData().getBoolean(TAG_FEL_SPAWNED))
			if(event.drops.isEmpty())
				event.drops.add(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, new ItemStack(Items.blaze_powder, 6)));
			else for(EntityItem item : event.drops) {
				ItemStack stack = item.getEntityItem();
				if(stack.getItem() == Items.blaze_rod)
					item.setEntityItemStack(new ItemStack(Items.blaze_powder, stack.stackSize * 10));
			}
	}

	@Override
	public LexiconEntry getEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.gardenOfGlass;
	}

	public class EventHandler {
		@SubscribeEvent
		public void onDropsWrapper(LivingDropsEvent event) {
			BlockFelPumpkin.this.onDrops(event);
		}
	}
}
