/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Feb 3, 2014, 9:59:17 PM (GMT)]
 */
package vazkii.botania.client.core.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.TileSignature;
import vazkii.botania.client.core.handler.LightningHandler.LightningBolt;
import vazkii.botania.client.gui.lexicon.GuiLexicon;
import vazkii.botania.common.block.subtile.functional.SubTileVinculotus;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;
import vazkii.botania.common.core.handler.ManaNetworkHandler;
import vazkii.botania.common.item.ItemTwigWand;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientTickHandler {

	public static int ticksWithLexicaOpen = 0;
	public static int pageFlipTicks = 0;
	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;

	private void calcDelta() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}

	@SubscribeEvent
	public void renderTick(RenderTickEvent event) {
		if(event.phase == Phase.START)
			partialTicks = event.renderTickTime;
		else {
			TooltipAdditionDisplayHandler.render();
			calcDelta();
		}
	}

	@SubscribeEvent
	public void clientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			LightningBolt.update();
			RedStringRenderer.tick();
			ItemsRemainingRenderHandler.tick();

			if(Minecraft.getMinecraft().theWorld == null) {
				ManaNetworkHandler.instance.clear();
				TileCorporeaIndex.indexes.clear();
				SubTileVinculotus.existingFlowers.clear();
			}

			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui == null || !gui.doesGuiPauseGame()) {
				ticksInGame++;
				partialTicks = 0;

				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				if(player != null) {
					ItemStack stack = player.getCurrentEquippedItem();
					if(stack != null && stack.getItem() instanceof ItemTwigWand) {
						List<TileSignature> list = new ArrayList<>(ManaNetworkHandler.instance.getAllCollectorsInWorld(Minecraft.getMinecraft().theWorld));
						for(TileSignature sig : list) {
							if(!sig.remoteWorld)
								continue;

							TileEntity tile = sig.tile;
							if(tile instanceof IManaCollector)
								((IManaCollector) tile).onClientDisplayTick();
						}
					}
				}
			}

			int ticksToOpen = 10;
			if(gui instanceof GuiLexicon) {
				if(ticksWithLexicaOpen < 0)
					ticksWithLexicaOpen = 0;
				if(ticksWithLexicaOpen < ticksToOpen)
					ticksWithLexicaOpen++;
				if(pageFlipTicks > 0)
					pageFlipTicks--;
			} else {
				pageFlipTicks = 0;
				if(ticksWithLexicaOpen > 0) {
					if(ticksWithLexicaOpen > ticksToOpen)
						ticksWithLexicaOpen = ticksToOpen;
					ticksWithLexicaOpen--;
				}
			}

			calcDelta();
		}
	}

	public static void notifyPageChange() {
		if(pageFlipTicks == 0)
			pageFlipTicks = 5;
	}

}
