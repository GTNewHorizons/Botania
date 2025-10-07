/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 20, 2014, 7:42:46 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.wand.ICoordBoundItem;
import vazkii.botania.api.wand.ITileBound;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.Botania;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.block.BlockPistonRelay;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.TileEnchanter;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.Vector3;
import vazkii.botania.common.lib.LibItemNames;

public class ItemTwigWand extends Item16Colors implements ICoordBoundItem {

	IIcon[] icons;

	private static final String TAG_COLOR1 = "color1";
	private static final String TAG_COLOR2 = "color2";
	private static final String TAG_BOUND_TILE_X = "boundTileX";
	private static final String TAG_BOUND_TILE_Y = "boundTileY";
	private static final String TAG_BOUND_TILE_Z = "boundTileZ";
	private static final String TAG_BIND_MODE = "bindMode";

	public ItemTwigWand() {
		super(LibItemNames.TWIG_WAND);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		Block block = world.getBlock(x, y, z);
		ChunkCoordinates boundTile = getBoundTile(stack);

		if(boundTile.posY != -1 && player.isSneaking() && (boundTile.posX != x || boundTile.posY != y || boundTile.posZ != z)) {
			TileEntity tile = world.getTileEntity(boundTile.posX, boundTile.posY, boundTile.posZ);
			if(tile instanceof IWandBindable) {
				if(((IWandBindable) tile).bindTo(player, stack, x, y, z, side)) {
					Vector3 orig = new Vector3(boundTile.posX + 0.5, boundTile.posY + 0.5, boundTile.posZ + 0.5);
					Vector3 end = new Vector3(x + 0.5, y + 0.5, z + 0.5);
					doParticleBeam(world, orig, end);

					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, boundTile.posX, boundTile.posY, boundTile.posZ);
					setBoundTile(stack, 0, -1, 0);
				}

				return true;
			} else setBoundTile(stack, 0, -1, 0);
		} else if(player.isSneaking()) {
			block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side));
			if(world.isRemote)
				player.swingItem();
		}

		if(block == Blocks.lapis_block && ConfigHandler.enchanterEnabled) {
			int meta = -1;
			if(TileEnchanter.canEnchanterExist(world, x, y, z, 0))
				meta = 0;
			else if(TileEnchanter.canEnchanterExist(world, x, y, z, 1))
				meta = 1;

			if(meta != -1 && !world.isRemote) {
				world.setBlock(x, y, z, ModBlocks.enchanter, meta, 1 | 2);
				player.addStat(ModAchievements.enchanterMake, 1);
				world.playSoundEffect(x, y, z, "botania:enchanterBlock", 0.5F, 0.6F);
				for(int i = 0; i < 50; i++) {
					float red = (float) Math.random();
					float green = (float) Math.random();
					float blue = (float) Math.random();

					double dx = (Math.random() - 0.5) * 6;
					double dy = (Math.random() - 0.5) * 6;
					double dz = (Math.random() - 0.5) * 6;

					float velMul = 0.07F;

					Botania.proxy.wispFX(world, x + 0.5 + dx, y + 0.5 + dy, z + 0.5 + dz, red, green, blue, (float) Math.random() * 0.15F + 0.15F, (float) -dx * velMul, (float) -dy * velMul, (float) -dz * velMul);
				}
			}
		} else if(block instanceof IWandable) {
			TileEntity tile = world.getTileEntity(x, y, z);
			boolean bindable = tile instanceof IWandBindable;

			boolean wanded = false;
			if(getBindMode(stack) && bindable && player.isSneaking() && ((IWandBindable) tile).canSelect(player, stack, x, y, z, side)) {
				if(boundTile.posX == x && boundTile.posY == y && boundTile.posZ == z)
					setBoundTile(stack, 0, -1, 0);
				else setBoundTile(stack, x, y, z);

				if(world.isRemote)
					player.swingItem();
				world.playSoundAtEntity(player, "botania:ding", 0.1F, 1F);

				wanded = true;
			} else {
				wanded = ((IWandable) block).onUsedByWand(player, stack, world, x, y, z, side);
				if(wanded && world.isRemote)
					player.swingItem();
			}

			return wanded;
		} else if(BlockPistonRelay.playerPositions.containsKey(player.getCommandSenderName()) && !world.isRemote) {
			String bindPos = BlockPistonRelay.playerPositions.get(player.getCommandSenderName());
			String currentPos = BlockPistonRelay.getCoordsAsString(world.provider.dimensionId, x, y, z);

			BlockPistonRelay.playerPositions.remove(player.getCommandSenderName());
			BlockPistonRelay.mappedPositions.put(bindPos, currentPos);
			BlockPistonRelay.WorldData.get(world).markDirty();

			world.playSoundAtEntity(player, "botania:ding", 1F, 1F);
		}

		return false;
	}

	public static void doParticleBeam(World world, Vector3 orig, Vector3 end) {
		if(!world.isRemote)
			return;

		Vector3 diff = end.copy().sub(orig);
		Vector3 movement = diff.copy().normalize().multiply(0.05);
		int iters = (int) (diff.mag() / movement.mag());
		float huePer = 1F / iters;
		float hueSum = (float) Math.random();

		Vector3 currentPos = orig.copy();
		for(int i = 0; i < iters; i++) {
			float hue = i * huePer + hueSum;
			Color color = Color.getHSBColor(hue, 1F, 1F);
			float r = color.getRed() / 255F;
			float g = color.getGreen() / 255F;
			float b = color.getBlue() / 255F;

			Botania.proxy.setSparkleFXNoClip(true);
			Botania.proxy.sparkleFX(world, currentPos.x, currentPos.y, currentPos.z, r, g, b, 0.5F, 4);
			Botania.proxy.setSparkleFXNoClip(false);
			currentPos.add(movement);
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeld) {
		ChunkCoordinates coords = getBoundTile(stack);
		TileEntity tile = world.getTileEntity(coords.posX, coords.posY, coords.posZ);
		if(!(tile instanceof IWandBindable))
			setBoundTile(stack, 0, -1, 0);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote && player.isSneaking()) {
			setBindMode(stack, !getBindMode(stack));
			world.playSoundAtEntity(player, "botania:ding", 0.1F, 1F);
		}

		return stack;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[4];
		for(int i = 0; i < icons.length; i++)
			icons[i] = IconHelper.forItem(register, this, i);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if(pass == 3 && !getBindMode(stack))
			pass = 0;

		return icons[Math.min(icons.length - 1, pass)];
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if(renderPass == 0 || renderPass == 3)
			return 0xFFFFFF;

		float[] color = EntitySheep.fleeceColorTable[renderPass == 1 ? getColor1(stack) : getColor2(stack)];
		return new Color(color[0], color[1], color[2]).getRGB();
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 4;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < 16; i++)
			list.add(forColors(i, i));
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedNameLazy(stack);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advanced) {
		infoList.add(StatCollector.translateToLocal(getModeString(stack)));
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.rare;
	}

	public static ItemStack forColors(int color1, int color2) {
		ItemStack stack = new ItemStack(ModItems.twigWand);
		ItemNBTHelper.setInt(stack, TAG_COLOR1, color1);
		ItemNBTHelper.setInt(stack, TAG_COLOR2, color2);

		return stack;
	}

	public static int getColor1(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR1, 0);
	}

	public static int getColor2(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_COLOR2, 0);
	}

	public static void setBoundTile(ItemStack stack, int x, int y, int z) {
		ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_X, x);
		ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_Y, y);
		ItemNBTHelper.setInt(stack, TAG_BOUND_TILE_Z, z);
	}

	public static ChunkCoordinates getBoundTile(ItemStack stack) {
		int x = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_X, 0);
		int y = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_Y, -1);
		int z = ItemNBTHelper.getInt(stack, TAG_BOUND_TILE_Z, 0);
		return new ChunkCoordinates(x, y, z);
	}

	public static boolean getBindMode(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_BIND_MODE, true);
	}

	public static void setBindMode(ItemStack stack, boolean bindMode) {
		ItemNBTHelper.setBoolean(stack, TAG_BIND_MODE, bindMode);
	}

	public static String getModeString(ItemStack stack) {
		return "botaniamisc.wandMode." + (getBindMode(stack) ? "bind" : "function");
	}

	@Override
	public ChunkCoordinates getBinding(ItemStack stack) {
		ChunkCoordinates bound = getBoundTile(stack);
		if(bound.posY != -1)
			return bound;

		MovingObjectPosition pos = Minecraft.getMinecraft().objectMouseOver;
		if(pos != null) {
			TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pos.blockX, pos.blockY, pos.blockZ);
			if(tile instanceof ITileBound) {
				ChunkCoordinates coords = ((ITileBound) tile).getBinding();
				return coords;
			}
		}

		return null;
	}

}
