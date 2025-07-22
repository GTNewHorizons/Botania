/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 29, 2015, 10:13:32 PM (GMT)]
 */
package vazkii.botania.common.item.relic;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.gtnewhorizon.gtnhlib.GTNHLib;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.item.IExtendedWireframeCoordinateListProvider;
import vazkii.botania.api.item.IInWorldRenderable;
import vazkii.botania.api.item.ISequentialBreaker;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.core.handler.BoundTileRenderer;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.core.helper.LokiCursor;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.lib.LibItemNames;
import baubles.api.BaubleType;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import baubles.common.network.PacketSyncBauble;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.botania.common.network.PacketLokiHudNotificationAck;


public class ItemLokiRing extends ItemRelicBauble implements IExtendedWireframeCoordinateListProvider, IManaUsingItem, IInWorldRenderable {

	private static final String TAG_CURSOR_LIST = "cursorList";
	private static final String TAG_CURSOR_PREFIX = "cursor";
	private static final String TAG_CURSOR_COUNT = "cursorCount";
	private static final String TAG_X_ORIGIN = "xOrigin";
	private static final String TAG_Y_ORIGIN = "yOrigin";
	private static final String TAG_Z_ORIGIN = "zOrigin";
	private static final String TAG_MODE = "mode";
	private static final String TAG_BREAKING_MODE = "breaking";
	private static final String TAG_MIRROR_MODE = "mirror";
	private boolean recursion = false;

	public static enum HUD_MESSAGE  {
		MODE, BREAKING, CLEAR, MIRROR, INSUFFICIENT_MANA
	}

	public ItemLokiRing() {
		super(LibItemNames.LOKI_RING);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		int x = event.x;
		int y = event.y;
		int z = event.z;
		int side = event.blockMetadata;
		ItemStack stack = player.getCurrentEquippedItem();
		if(stack == null) return;
		Item item = player.getCurrentEquippedItem().getItem();
		breakOnAllCursors(player, item, stack, x, y, z, side);
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		if(recursion) return;

		EntityPlayer player = event.entityPlayer;
		ItemStack lokiRing = getLokiRing(player);
		if (lokiRing == null || player.worldObj.isRemote)
			return;

		ItemStack heldItemStack = player.getCurrentEquippedItem();
		ChunkCoordinates originCoords = getOriginPos(lokiRing);
		MovingObjectPosition lookPos = ToolCommons.raytraceFromEntity(player.worldObj, player, true, 10F);
		List<LokiCursor> cursors = getCursorList(lokiRing);
		int cursorCount = cursors.size();
		//I don`t think mana for placing should be cruel , so I just made graph with funny line
		int cost = (int)(50*Math.sin(0.04* cursorCount)+cursorCount+120+20*Math.cos(0.2*cursorCount));

		if (heldItemStack == null && event.action == Action.RIGHT_CLICK_BLOCK && player.isSneaking() && isRingEnabled(lokiRing)) {
			if(originCoords.posY == -1 && lookPos != null) {
				setOriginPos(lokiRing, lookPos.blockX, lookPos.blockY, lookPos.blockZ);
				if(player instanceof EntityPlayerMP)
					syncLokiRing(player);
			} else if(lookPos != null) {
				if(originCoords.posX == lookPos.blockX && originCoords.posY == lookPos.blockY && originCoords.posZ == lookPos.blockZ) {
					clearMasterCursor(lokiRing);
					if(player instanceof EntityPlayerMP)
						syncLokiRing(player);
				} else {
					addCursor : {
						int relX = lookPos.blockX - originCoords.posX;
						int relY = lookPos.blockY - originCoords.posY;
						int relZ = lookPos.blockZ - originCoords.posZ;

						for(LokiCursor cursor : cursors)
							if(cursor.getX() == relX && cursor.getY() == relY && cursor.getZ() == relZ) {
								cursors.remove(cursor);
								setCursorList(lokiRing, cursors);
								if(player instanceof EntityPlayerMP)
									syncLokiRing(player);
								break addCursor;
							}

						addCursor(lokiRing, relX, relY, relZ, getRingMirrorMode(lokiRing) );
						if(player instanceof EntityPlayerMP)
							syncLokiRing(player);
					}
				}
			}
		} else if (heldItemStack != null && event.action == Action.RIGHT_CLICK_BLOCK && lookPos != null && isRingEnabled(lokiRing)) {
			if(!ManaItemHandler.requestManaExact(lokiRing, player, cost, true)){
				if(player instanceof EntityPlayerMP){
					vazkii.botania.common.network.PacketHandler.INSTANCE.sendTo(new PacketLokiHudNotificationAck(HUD_MESSAGE.INSUFFICIENT_MANA),(EntityPlayerMP) player);
				}
				else
				{
					renderHUDNotification(HUD_MESSAGE.INSUFFICIENT_MANA);
				}
				return;
			}

			recursion = true;

			double oldPosX = player.posX;
			double oldPosY = player.posY;
			double oldPosZ = player.posZ;
			float oldPitch = player.rotationPitch;
			float oldYaw = player.rotationYaw;

			int masterOffsetX = originCoords.posY == -1 ? 0 :lookPos.blockX - originCoords.posX;
			int masterOffsetY = originCoords.posY == -1 ? 0 :lookPos.blockY - originCoords.posY;
			int masterOffsetZ = originCoords.posY == -1 ? 0 :lookPos.blockZ - originCoords.posZ;

			double playerOffsetX = player.posX - originCoords.posX;
			double playerOffsetY = player.posY - originCoords.posY;
			double playerOffsetZ = player.posZ - originCoords.posZ;

			for(LokiCursor cursor : cursors) {

				int x = lookPos.blockX + cursor.getX();
				int y = lookPos.blockY + cursor.getY();
				int z = lookPos.blockZ + cursor.getZ();

				if(cursor.isMirrorX()){
					x -= 2*masterOffsetX;
				}
				if(cursor.isMirrorY()){
					y -= 2*masterOffsetY;
				}
				if(cursor.isMirrorZ()){
					z -= 2*masterOffsetZ;
				}

				if (player.worldObj.isAirBlock(x, y, z) ) {
					continue;
				}

				player.posX = cursor.getX()+oldPosX;
				player.posY = cursor.getY()+oldPosY;
				player.posZ = cursor.getZ()+oldPosZ;
				player.rotationYaw = oldYaw;

				if(cursor.isMirrorX()){
					player.posX -= 2*playerOffsetX;
					player.rotationYaw = player.rotationYaw * (-1);
				}
				if(cursor.isMirrorY()){
					player.posY -= 2*playerOffsetY;
					player.rotationPitch = oldPitch * (-1);
				}
				if(cursor.isMirrorZ()){
					player.posZ -= 2*playerOffsetZ;
					player.rotationYaw = 180 - (Math.abs(player.rotationYaw));
					if(oldYaw < 0){
						player.rotationYaw *= -1;
					}
				}

				float hitX = (float) (lookPos.hitVec.xCoord - lookPos.blockX);
				float hitY = (float) (lookPos.hitVec.yCoord - lookPos.blockY);
				float hitZ = (float) (lookPos.hitVec.zCoord - lookPos.blockZ);
				if(cursor.isMirrorX()){
					hitX = 1-hitX;
				}
				if(cursor.isMirrorY()){
					hitY = 1-hitY;
				}
				if(cursor.isMirrorZ()){
					hitZ = 1-hitZ;
				}

				int hitSide = lookPos.sideHit;
				if(cursor.isMirrorX() && (hitSide == ForgeDirection.EAST.ordinal() || hitSide == ForgeDirection.WEST.ordinal()) ){
					hitSide = hitSide ^ 1;
				}
				if(cursor.isMirrorY() && (hitSide == ForgeDirection.DOWN.ordinal() || hitSide == ForgeDirection.UP.ordinal()) ){
					hitSide = hitSide ^ 1;
				}
				if(cursor.isMirrorZ() && (hitSide == ForgeDirection.NORTH.ordinal() || hitSide == ForgeDirection.SOUTH.ordinal()) ){
					hitSide = hitSide ^ 1;
				}

				Item item = heldItemStack.getItem();
				Block markedBlock = player.worldObj.getBlock(x, y, z);

				boolean wasActivated = markedBlock.onBlockActivated(player.worldObj, x, y, z, player, hitSide, hitX,hitY,hitZ);

				if (heldItemStack.stackSize == 0 ) {
					event.setCanceled(true);
					break;
				}
				if (!wasActivated) {
					item.onItemUse(player.capabilities.isCreativeMode ? heldItemStack.copy() : heldItemStack, player, player.worldObj, x, y, z, hitSide, (float) lookPos.hitVec.xCoord - x, (float) lookPos.hitVec.yCoord - y, (float) lookPos.hitVec.zCoord - z);
					if(heldItemStack.stackSize == 0) {
						event.setCanceled(true);
						break;
					}
				}

			}
			recursion = false;
			player.posX = oldPosX;
			player.posY = oldPosY;
			player.posZ = oldPosZ;
			player.rotationPitch = oldPitch;
			player.rotationYaw = oldYaw;
		}
	}

	public static void setMode(ItemStack stack, boolean state) {
		stack.stackTagCompound.setBoolean(TAG_MODE, state);
	}

	public static void setBreakingMode(ItemStack stack, boolean state) {
		stack.stackTagCompound.setBoolean(TAG_BREAKING_MODE, state);
	}

	public static void setMirrorMode (ItemStack stack, byte state) {
		stack.stackTagCompound.setByte(TAG_MIRROR_MODE, state);
	}


	@SideOnly(Side.CLIENT)
	public static void renderHUDNotification(HUD_MESSAGE type){
		Minecraft mc = Minecraft.getMinecraft();
		String text;
		switch (type) {
			case MODE:
				text = getLokiModeText(getLokiRing(mc.thePlayer));
				break;
			case BREAKING:
				text = getLokiBreakingModeText(getLokiRing(mc.thePlayer));
				break;
			case CLEAR:
				text = getLokiCearText(getLokiRing(mc.thePlayer));
				break;
			case MIRROR:
				text = getLokiMirrorText(getLokiRing(mc.thePlayer));
				break;
			case INSUFFICIENT_MANA:
				text = EnumChatFormatting.RED + StatCollector.translateToLocal("botaniamisc.insufficient_mana");
				break;
			default:
				return;
				
		}
		 
		GTNHLib.proxy.printMessageAboveHotbar(text, 60, true, true);
	}
	
	public static String getOnOffString(boolean state){
		return state ? EnumChatFormatting.GREEN + StatCollector.translateToLocal("botaniamisc.lokiOn") :
				EnumChatFormatting.RED + StatCollector.translateToLocal("botaniamisc.lokiOff");
	}

	public static String getAxisString(byte state) {
		EnumChatFormatting x = LokiCursor.isMirrorX(state) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		EnumChatFormatting y = LokiCursor.isMirrorY(state) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
		EnumChatFormatting z = LokiCursor.isMirrorZ(state) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;

		return x + "X " + y + "Y " + z + "Z";
	}

	public static String getLokiModeText(ItemStack stack){
		return EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.botania:lokiRing.name") + " " + getOnOffString(isRingEnabled(stack));
	}

	public static String getLokiMirrorText(ItemStack stack){
		return EnumChatFormatting.GOLD + StatCollector.translateToLocal("botaniamisc.lokiMirror") + " "+
				getAxisString(getRingMirrorMode(stack));
	}

	public static String getLokiCearText(ItemStack stack){
		return EnumChatFormatting.GOLD + StatCollector.translateToLocal("botaniamisc.lokiClear");		
	}

	public static String getLokiBreakingModeText(ItemStack stack){
		return EnumChatFormatting.GOLD + StatCollector.translateToLocal("item.botania:lokiRing.name") + " "+
				StatCollector.translateToLocal("botaniamisc.breaking") + " " + getOnOffString(isRingBreakingEnabled(stack));
	}



	public static boolean isRingEnabled (final ItemStack stack){
		if (stack.hasTagCompound())
		{
			return stack.stackTagCompound.getBoolean(TAG_MODE);
		}
		return false;
	}

	public static boolean isRingBreakingEnabled (final ItemStack stack){
		if (stack.hasTagCompound())
		{
			return stack.stackTagCompound.getBoolean(TAG_BREAKING_MODE);
		}
		return false;
	}

	public static byte getRingMirrorMode(final ItemStack stack){
		if(stack.hasTagCompound()){
			return stack.stackTagCompound.getByte(TAG_MIRROR_MODE);
		}
		return 0;
	}
	public static void breakOnAllCursors(EntityPlayer player, Item item, ItemStack stack, int x, int y, int z, int side) {
		ItemStack lokiRing = getLokiRing(player);
		if (lokiRing == null || player.worldObj.isRemote || !isRingEnabled(lokiRing) || !isRingBreakingEnabled(lokiRing))
			return;
		List<LokiCursor> cursors = getCursorList(lokiRing);
		//In case someone wants to mine ore veins with loki, this should make 1 manapool worth of mana last for 2 veins
		int cost = 30 * cursors.size();
		if (!ManaItemHandler.requestManaExact(lokiRing, player, cost, true)) {

		if (player instanceof EntityPlayerMP) {
			vazkii.botania.common.network.PacketHandler.INSTANCE.sendTo(new PacketLokiHudNotificationAck(HUD_MESSAGE.INSUFFICIENT_MANA), (EntityPlayerMP) player);
		} else {
			renderHUDNotification(HUD_MESSAGE.INSUFFICIENT_MANA);
		}
		}
		ISequentialBreaker breaker  = null;
		if(item instanceof ISequentialBreaker)
			breaker = (ISequentialBreaker) item;
		World world = player.worldObj;
		boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0;
		int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);

		ChunkCoordinates originCoords = getOriginPos(lokiRing);

		int masterOffsetX = originCoords.posY == -1 ? 0 :x - originCoords.posX;
		int masterOffsetY = originCoords.posY == -1 ? 0 :y - originCoords.posY;
		int masterOffsetZ = originCoords.posY == -1 ? 0 :z - originCoords.posZ;

		for(int i = 0; i < cursors.size(); i++) {
			LokiCursor cursor = cursors.get(i);
			int xp = x + cursor.getX();
			int yp = y + cursor.getY();
			int zp = z + cursor.getZ();
			if(cursor.isMirrorX()){
				xp -= 2*masterOffsetX;
			}
			if(cursor.isMirrorY()){
				yp -= 2*masterOffsetY;
			}
			if(cursor.isMirrorZ()){
				zp -= 2*masterOffsetZ;
			}

			if(cursor.isMirrorX() && (side == ForgeDirection.EAST.ordinal() || side == ForgeDirection.WEST.ordinal()) ){
				side = side ^ 1;
			}
			if(cursor.isMirrorY() && (side == ForgeDirection.DOWN.ordinal() || side == ForgeDirection.UP.ordinal()) ){
				side = side ^ 1;
			}
			if(cursor.isMirrorZ() && (side == ForgeDirection.NORTH.ordinal() || side == ForgeDirection.SOUTH.ordinal()) ){
				side = side ^ 1;
			}



			Block block = world.getBlock(xp, yp, zp);
			if(breaker != null)
				breaker.breakOtherBlock(player, stack, xp, yp, zp, x, y, z, side);
			ToolCommons.removeBlockWithDrops(player, stack, player.worldObj, xp, yp, zp, x, y, z, block, new Material[] { block.getMaterial() }, silk, fortune, block.getBlockHardness(world, xp, yp, zp), true);
		}
	}

	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase player) {
		clearCursors(stack);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean adv) {
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.lokiDescription"), list);
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.lokiDescription2"), list);
		addStringToTooltip("", list);
		addStringToTooltip(EnumChatFormatting.WHITE +StatCollector.translateToLocal("botaniamisc.lokiCurrent"), list);
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.lokiState") + ": " + getOnOffString(isRingEnabled(stack)), list);
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.breaking") + ": " + getOnOffString(isRingBreakingEnabled(stack)), list);
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.lokiMirror") + getAxisString(getRingMirrorMode(stack)), list);
		addStringToTooltip("", list);
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.lokiToggleDescription") + " " + getOnOffString(true) + EnumChatFormatting.RESET + "/"+ getOnOffString(false), list);
		addStringToTooltip(StatCollector.translateToLocal("botaniamisc.lokiBreakingDescription") + " " + getOnOffString(true) + EnumChatFormatting.RESET+"/" + getOnOffString(false), list);	
		super.addInformation(stack, player, list, adv);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<ChunkCoordinates> getWireframesToDraw(EntityPlayer player, ItemStack stack) {
		ItemStack lokiRing = getLokiRing(player);
		if(lokiRing != stack || !isRingEnabled(lokiRing) )
			return null;

		MovingObjectPosition lookPos = Minecraft.getMinecraft().objectMouseOver;

		if(lookPos != null && lookPos.entityHit == null) {
			List<LokiCursor> list = getCursorList(stack);
			ChunkCoordinates origin = getOriginPos(stack);
			List<ChunkCoordinates> toDraw = new ArrayList<>();

			if(origin.posY != -1) {
				for(LokiCursor cursor : list) {
					ChunkCoordinates coords = cursor.getCoordinates();
					coords.posX += origin.posX;
					coords.posY += origin.posY;
					coords.posZ += origin.posZ;
					toDraw.add(coords);
				}
			} else for(LokiCursor cursor : list) {
				ChunkCoordinates coords = cursor.getCoordinates();
				coords.posX += lookPos.blockX;
				coords.posY += lookPos.blockY;
				coords.posZ += lookPos.blockZ;
				toDraw.add(coords);
			}

			return toDraw;
		}

		return null;
	}

	@Override
	public ChunkCoordinates getSourceWireframe(EntityPlayer player, ItemStack stack) {
		return getLokiRing(player) == stack && isRingEnabled(stack) ? getOriginPos(stack) : null;
	}

	public static void clearCursors(ItemStack stack){
		setCursorList(stack, null);
	}
	
	public static void clearMasterCursor(ItemStack stack){
		setOriginPos(stack, 0, -1, 0);
	}

	public static ItemStack getLokiRing(EntityPlayer player) {
		InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
		ItemStack stack1 = baubles.getStackInSlot(1);
		ItemStack stack2 = baubles.getStackInSlot(2);
		return isLokiRing(stack1) ? stack1 : isLokiRing(stack2) ? stack2 : null;
	}

	public static int getLokiRingSlot(EntityPlayer player) {
		InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
		ItemStack stack1 = baubles.getStackInSlot(1);
		ItemStack stack2 = baubles.getStackInSlot(2);
		return isLokiRing(stack1) ? 1 : isLokiRing(stack2) ? 2 : null;
	}

	public static void syncLokiRing(EntityPlayer player){
		baubles.common.network.PacketHandler.INSTANCE.sendTo(new PacketSyncBauble(player, getLokiRingSlot(player)), (EntityPlayerMP) player);
	}

	private static boolean isLokiRing(ItemStack stack) {
		return stack != null && (stack.getItem() == ModItems.lokiRing || stack.getItem() == ModItems.aesirRing);
	}

	private static ChunkCoordinates getOriginPos(ItemStack stack) {
		int x = ItemNBTHelper.getInt(stack, TAG_X_ORIGIN, 0);
		int y = ItemNBTHelper.getInt(stack, TAG_Y_ORIGIN, -1);
		int z = ItemNBTHelper.getInt(stack, TAG_Z_ORIGIN, 0);
		return new ChunkCoordinates(x, y, z);
	}

	private static void setOriginPos(ItemStack stack, int x, int y, int z) {
		ItemNBTHelper.setInt(stack, TAG_X_ORIGIN, x);
		ItemNBTHelper.setInt(stack, TAG_Y_ORIGIN, y);
		ItemNBTHelper.setInt(stack, TAG_Z_ORIGIN, z);
	}

	private static List<LokiCursor> getCursorList(ItemStack stack) {
		NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, TAG_CURSOR_LIST, false);
		List<LokiCursor> cursors = new ArrayList<>();

		int count = cmp.getInteger(TAG_CURSOR_COUNT);
		for(int i = 0; i < count; i++) {
			NBTTagCompound cursorCmp = cmp.getCompoundTag(TAG_CURSOR_PREFIX + i);
			cursors.add(LokiCursor.fromNBT(cursorCmp));

		}

		return cursors;
	}

	private static void setCursorList(ItemStack stack, List<LokiCursor> cursors) {
		NBTTagCompound cmp = new NBTTagCompound();
		if(cursors != null) {
			int i = 0;
			for(LokiCursor cursor : cursors) {
				NBTTagCompound cursorCmp = cursor.toNBT();
				cmp.setTag(TAG_CURSOR_PREFIX + i, cursorCmp);
				i++;
			}
			cmp.setInteger(TAG_CURSOR_COUNT, i);
		}

		ItemNBTHelper.setCompound(stack, TAG_CURSOR_LIST, cmp);
	}



	private static void addCursor(ItemStack stack, int x, int y, int z, byte mirrorMode) {
		NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, TAG_CURSOR_LIST, false);
		int count = cmp.getInteger(TAG_CURSOR_COUNT);
		cmp.setTag(TAG_CURSOR_PREFIX + count, new LokiCursor(x,y,z, mirrorMode).toNBT());
		cmp.setInteger(TAG_CURSOR_COUNT, count + 1);
		ItemNBTHelper.setCompound(stack, TAG_CURSOR_LIST, cmp);
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInWorld(EntityPlayer player, ItemStack stack){
		renderMirrors(player,stack);
	}

	@SideOnly(Side.CLIENT)
	private void renderMirrors(EntityPlayer player,ItemStack stack) {
		ItemStack lokiRing = getLokiRing(player);
		if(lokiRing != stack || !isRingEnabled(lokiRing) )
			return;

		World world = Minecraft.getMinecraft().theWorld;
		List<LokiCursor> cursors = getCursorList(stack);
		ChunkCoordinates origin = getOriginPos(stack);
		MovingObjectPosition lookPos = Minecraft.getMinecraft().objectMouseOver;


		for(LokiCursor cursor : cursors) {
			if(!cursor.isMirror()) {
				continue;
			}
			ChunkCoordinates pos = cursor.getCoordinates();
			if(origin.posY != -1){

				pos.posX += origin.posX;
				pos.posY += origin.posY;
				pos.posZ += origin.posZ;
			}else{
				pos.posX += lookPos.blockX;
				pos.posY += lookPos.blockY;
				pos.posZ += lookPos.blockZ;
			}


			GL11.glPushMatrix();
			GL11.glTranslated(pos.posX - RenderManager.renderPosX, pos.posY - RenderManager.renderPosY, pos.posZ - RenderManager.renderPosZ + 1);
			Color colorRGB = new Color(BoundTileRenderer.getWireframeColor());
			GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 255);
			Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);

			if (block != null) {
				AxisAlignedBB axis = block.getSelectedBoundingBoxFromPool(world, pos.posX, pos.posY, pos.posZ);

				if (axis != null) {
					axis.minX -= pos.posX;
					axis.maxX -= pos.posX;
					axis.minY -= pos.posY;
					axis.maxY -= pos.posY;
					axis.minZ -= pos.posZ + 1;
					axis.maxZ -= pos.posZ + 1;

					GL11.glScalef(1F, 1F, 1F);

					GL11.glLineWidth(1F);
					Tessellator tessellator = Tessellator.instance;
					tessellator.startDrawing(GL11.GL_LINES);
					if (cursor.isMirrorX()) {

						tessellator.addVertex(axis.minX,axis.minY,axis.minZ);
						tessellator.addVertex(axis.minX,axis.maxY,axis.maxZ);

						tessellator.addVertex(axis.minX,axis.maxY,axis.minZ);
						tessellator.addVertex(axis.minX,axis.minY,axis.maxZ);

						tessellator.addVertex(axis.maxX,axis.minY,axis.minZ);
						tessellator.addVertex(axis.maxX,axis.maxY,axis.maxZ);

						tessellator.addVertex(axis.maxX,axis.maxY,axis.minZ);
						tessellator.addVertex(axis.maxX,axis.minY,axis.maxZ);

					}
					if (cursor.isMirrorY()) {

						tessellator.addVertex(axis.minX,axis.minY,axis.minZ);
						tessellator.addVertex(axis.maxX,axis.minY,axis.maxZ);

						tessellator.addVertex(axis.maxX,axis.minY,axis.minZ);
						tessellator.addVertex(axis.minX,axis.minY,axis.maxZ);

						tessellator.addVertex(axis.minX,axis.maxY,axis.minZ);
						tessellator.addVertex(axis.maxX,axis.maxY,axis.maxZ);

						tessellator.addVertex(axis.maxX,axis.maxY,axis.minZ);
						tessellator.addVertex(axis.minX,axis.maxY,axis.maxZ);

					}
					if (cursor.isMirrorZ()) {

						tessellator.addVertex(axis.minX,axis.minY,axis.minZ);
						tessellator.addVertex(axis.maxX,axis.maxY,axis.minZ);

						tessellator.addVertex(axis.minX,axis.maxY,axis.minZ);
						tessellator.addVertex(axis.maxX,axis.minY,axis.minZ);

						tessellator.addVertex(axis.minX,axis.minY,axis.maxZ);
						tessellator.addVertex(axis.maxX,axis.maxY,axis.maxZ);

						tessellator.addVertex(axis.minX,axis.maxY,axis.maxZ);
						tessellator.addVertex(axis.maxX,axis.minY,axis.maxZ);


					}
					tessellator.draw();
				}
			}

			GL11.glPopMatrix();
		}
	}

	public class EventHandler{
		@SubscribeEvent
		public void onBlockBreakWrapper(BlockEvent.BreakEvent event) {
			ItemLokiRing.this.onBlockBreak(event);
		}

		@SubscribeEvent
		public void onPlayerInteractWrapper(PlayerInteractEvent event) {
			ItemLokiRing.this.onPlayerInteract(event);
		}
	}

}

