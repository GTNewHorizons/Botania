/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 8, 2014, 7:20:26 PM (GMT)]
 */
package vazkii.botania.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.common.block.mana.BlockTerraPlate;
import vazkii.botania.common.block.tile.TileTerraPlate;

public class RenderTileTerraPlate extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileTerraPlate plate = (TileTerraPlate) tileEntity;

		float max = TileTerraPlate.MAX_MANA / 10F;
		float alphaMod = Math.min(max, plate.getCurrentMana()) / max;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		GL11.glRotated(90F, 1F, 0F, 0F);
		GL11.glTranslatef(0F, 0F, -3F / 16F - 0.001F);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		float alpha = (float) ((Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 8D) + 1D) / 5D + 0.6D) * alphaMod;
		if(ShaderHelper.useShaders())
			GL11.glColor4f(1F, 1F, 1F, alpha);
		else {
			int light = 15728880;
			int lightmapX = light % 65536;
			int lightmapY = light / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
			GL11.glColor4f(0.6F + (float) ((Math.cos((ClientTickHandler.ticksInGame + partialTicks) / 6D) + 1D) / 5D), 0.1F, 0.9F, alpha);
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

		ShaderHelper.useShader(ShaderHelper.terraPlateRune);
		RenderHelper.renderIcon(0, 0, BlockTerraPlate.overlay, 1, 1, 240);
		ShaderHelper.releaseShader();

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPopMatrix();
	}
}
