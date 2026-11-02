package vazkii.botania.client.core.handler;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.dashboard.api.HudBounds;
import com.gtnewhorizons.dashboard.api.HudElement;
import com.gtnewhorizons.dashboard.api.HudRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.handler.ConfigHandler;

/**
 * Lets players move, resize and hide Botania's HUD in Dashboard's HUD editor. The only class using Dashboard, so only
 * call it while Dashboard is loaded.
 */
@SideOnly(Side.CLIENT)
public final class DashboardHud {

	private static final int BAR_WIDTH = 182;
	private static final int BAR_HEIGHT = 5;
	private static final int FLIGHT_BAR_WIDTH = 81;
	private static final int FLIGHT_BAR_HEIGHT = 11;
	private static final int BOSS_BAR_WIDTH = 185;
	private static final int BOSS_BAR_HEIGHT = 25;
	private static final int BOSS_BAR_Y = 10;
	private static final int WAND_MODE_Y_OFFSET = 70;
	private static final int TEXT_HEIGHT = 9;
	/** From the Crafting Halo above the crosshair to the bind icon and popups right of and below it. */
	private static final int CROSSHAIR_INFO_LEFT = 80;
	private static final int CROSSHAIR_INFO_TOP = 71;
	private static final int CROSSHAIR_INFO_WIDTH = 200;
	private static final int CROSSHAIR_INFO_HEIGHT = 108;
	private static final int MANA_POOL_COLOR = 0x4444FF;

	private static final ResourceLocation HUD_ICONS = new ResourceLocation(LibResources.GUI_HUD_ICONS);
	private static final ResourceLocation BOSS_BAR_TEXTURE = new ResourceLocation(LibResources.GUI_BOSS_BAR);

	private static final HudElement MANA_BAR = new HudElement("botania:mana_bar", DashboardHud::getManaBarBounds);

	private static final HudElement FLIGHT_BAR = new HudElement("botania:flight_bar", DashboardHud::getFlightBarBounds);

	private static final HudElement BOSS_BAR = new HudElement("botania:boss_bar", (screenWidth, screenHeight) ->
		new HudBounds(screenWidth / 2 - BOSS_BAR_WIDTH / 2, BOSS_BAR_Y, BOSS_BAR_WIDTH, BOSS_BAR_HEIGHT));

	private static final HudElement WAND_MODE = new HudElement("botania:wand_mode", DashboardHud::getWandModeBounds);

	private static final HudElement CORPOREA_HINT = new HudElement("botania:corporea_hint", DashboardHud::getCorporeaHintBounds);

	/** Everything Botania draws around the crosshair, moved together so it keeps its layout. */
	private static final HudElement CROSSHAIR_INFO = new HudElement("botania:crosshair_info", (screenWidth, screenHeight) ->
		new HudBounds(screenWidth / 2 - CROSSHAIR_INFO_LEFT, screenHeight / 2 - CROSSHAIR_INFO_TOP, CROSSHAIR_INFO_WIDTH, CROSSHAIR_INFO_HEIGHT));

	private DashboardHud() {}

	public static void register() {
		setSample(MANA_BAR, DashboardHud::drawManaBarSample);
		MANA_BAR.setPreviewOnTop();
		setSample(FLIGHT_BAR, DashboardHud::drawFlightBarSample);
		setSample(BOSS_BAR, DashboardHud::drawBossBarSample);
		setSample(WAND_MODE, DashboardHud::drawWandModeSample);
		// Botania's own drawing, which moves itself
		CORPOREA_HINT.setPreview((screenWidth, screenHeight) -> HUDHandler.renderNearIndexDisplay(HudRegistry.getResolution()));
		setSample(CROSSHAIR_INFO, bounds -> drawCrosshairInfoSample());

		for(HudElement element : new HudElement[] { MANA_BAR, FLIGHT_BAR, BOSS_BAR, WAND_MODE, CORPOREA_HINT, CROSSHAIR_INFO })
			HudRegistry.register(element);
	}

	// False means the player hid the element, so skip drawing it and do not call endRender()

	public static boolean beginManaBar(ScaledResolution resolution) {
		return HudRegistry.beginRender(MANA_BAR, resolution);
	}

	public static boolean beginBossBar(ScaledResolution resolution) {
		return HudRegistry.beginRender(BOSS_BAR, resolution);
	}

	public static boolean beginWandMode(ScaledResolution resolution) {
		return HudRegistry.beginRender(WAND_MODE, resolution);
	}

	public static boolean beginCorporeaHint(ScaledResolution resolution) {
		return HudRegistry.beginRender(CORPOREA_HINT, resolution);
	}

	public static void endRender() {
		HudRegistry.endRender();
	}

	/** Drawn at the element's default place, and moved like the real one. */
	private static void setSample(HudElement element, Consumer<HudBounds> draw) {
		element.setPreview((screenWidth, screenHeight) ->
			HudRegistry.render(element, () -> draw.accept(element.getDefaultBounds(screenWidth, screenHeight))));
	}

	/** A full bar in Botania's light blue. */
	private static void drawManaBarSample(HudBounds bounds) {
		bindTexture(HUDHandler.manaBar);
		GL11.glColor4f(0.4F, 0.85F, 1F, 1F);
		RenderHelper.drawTexturedModalRect(bounds.x, bounds.y, 0, 0, 251, BAR_WIDTH, BAR_HEIGHT);
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}

	/** All ten feathers, like a fully rested tiara. */
	private static void drawFlightBarSample(HudBounds bounds) {
		bindTexture(HUD_ICONS);
		for(int i = 0; i < 10; i++)
			RenderHelper.drawTexturedModalRect(bounds.x + i * 8, bounds.y + 2, 0, 0, 0, 9, 9);
	}

	/** Gaia's bar at two thirds health, with its name above in Botania's purple. */
	private static void drawBossBarSample(HudBounds bounds) {
		int barY = bounds.y + 10;
		bindTexture(BOSS_BAR_TEXTURE);
		RenderHelper.drawTexturedModalRect(bounds.x, barY, 0, 0, 0, BOSS_BAR_WIDTH, 15);
		RenderHelper.drawTexturedModalRect(bounds.x + 2, barY + 4, 0, 0, 15, 181 * 2 / 3, 7);
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		String name = StatCollector.translateToLocal("entity.Botania.botania:doppleganger.name");
		font.drawStringWithShadow(name, bounds.x + bounds.width / 2 - font.getStringWidth(name) / 2, bounds.y, 0xA2018C);
	}

	private static void drawWandModeSample(HudBounds bounds) {
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getWandModeSample(), bounds.x, bounds.y, 0x00CC00);
	}

	/** Botania's own mana info, like when looking at a half full Mana Pool with a Wand of the Forest. */
	private static void drawCrosshairInfoSample() {
		String name = new ItemStack(ModBlocks.pool).getDisplayName();
		HUDHandler.drawSimpleManaHUD(MANA_POOL_COLOR, 500_000, 1_000_000, name, HudRegistry.getResolution());
	}

	private static void bindTexture(ResourceLocation texture) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}

	private static HudBounds getManaBarBounds(int screenWidth, int screenHeight) {
		return new HudBounds(screenWidth / 2 - BAR_WIDTH / 2, screenHeight - ConfigHandler.manaBarHeight, BAR_WIDTH, BAR_HEIGHT);
	}

	/** Higher while underwater, so it stays above the air bubbles. */
	private static HudBounds getFlightBarBounds(int screenWidth, int screenHeight) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		boolean underwater = player != null && player.getAir() < 300;
		int height = underwater ? ConfigHandler.flightBarBreathHeight : ConfigHandler.flightBarHeight;
		return new HudBounds(screenWidth / 2 + 10, screenHeight - height - 2, FLIGHT_BAR_WIDTH, FLIGHT_BAR_HEIGHT);
	}

	private static HudBounds getWandModeBounds(int screenWidth, int screenHeight) {
		int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(getWandModeSample());
		return new HudBounds(screenWidth / 2 - width / 2, screenHeight - WAND_MODE_Y_OFFSET, width, TEXT_HEIGHT);
	}

	private static String getWandModeSample() {
		return StatCollector.translateToLocal("botaniamisc.wandMode.function");
	}

	/** Three lines right of the corporea index icon, in two dark boxes, near the bottom right corner. */
	private static HudBounds getCorporeaHintBounds(int screenWidth, int screenHeight) {
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		int textWidth = Math.max(
			font.getStringWidth(StatCollector.translateToLocal("botaniamisc.nearIndex0")),
			Math.max(font.getStringWidth(StatCollector.translateToLocal("botaniamisc.nearIndex1")),
				font.getStringWidth(StatCollector.translateToLocal("botaniamisc.nearIndex2"))));
		int width = textWidth + 20;
		int x = screenWidth - width - 20;
		int y = screenHeight - 60;
		return new HudBounds(x - 6, y - 6, width + 12, 43);
	}
}
