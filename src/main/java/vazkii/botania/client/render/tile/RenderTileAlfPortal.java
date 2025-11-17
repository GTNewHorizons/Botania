/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 9, 2014, 9:55:07 PM (GMT)]
 */
package vazkii.botania.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.BlockAlfPortal;
import vazkii.botania.common.block.tile.TileAlfPortal;

public class RenderTileAlfPortal extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileAlfPortal portal = (TileAlfPortal) tileEntity;
		int meta = portal.getBlockMetadata();
		if(meta == 0)
			return;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(-1F, 1F, 0.25F);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		float alpha = (float) Math.min(1F, (Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 8D) + 1D) / 7D + 0.6D) * (Math.min(60, portal.ticksOpen) / 60F) * 0.5F;
		GL11.glColor4f(1F, 1F, 1F, alpha);

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

		if(meta == 2) {
			GL11.glTranslatef(1.25F, 0F, 1.75F);
			GL11.glRotatef(90F, 0F, 1F, 0F);
		}

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		RenderHelper.renderIcon(0, 0, BlockAlfPortal.portalTex, 3, 3, 240);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslated(0F, 0F, 0.5F);
		RenderHelper.renderIcon(0, 0, BlockAlfPortal.portalTex, 3, 3, 240);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glPopMatrix();
	}
}
