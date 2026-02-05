/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 8, 2015, 4:44:23 PM (GMT)]
 */
package vazkii.botania.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import vazkii.botania.client.lib.LibResources;
import vazkii.botania.client.model.ModelCocoon;
import vazkii.botania.common.block.tile.TileCocoon;

public class RenderTileCocoon extends TileEntitySpecialRenderer {

	private static final ResourceLocation texture = new ResourceLocation(LibResources.MODEL_COCOON);
	ModelCocoon model = new ModelCocoon();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileCocoon cocoon = (TileCocoon) tileEntity;

		float rot = 0F;
		float modval = 60F - (float) cocoon.timePassed / (float) TileCocoon.TOTAL_TIME * 30F;
		if(cocoon.timePassed % modval < 10) {
			float mod = (cocoon.timePassed + partialTicks) % modval;
			float v = mod / 5 * (float) Math.PI * 2;
			rot = (float) Math.sin(v) * (float) Math.log(cocoon.timePassed + partialTicks);
		}

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glTranslated(x, y, z);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		GL11.glRotatef(90F, 1F, 0F, 0F);
		GL11.glTranslatef(0.5F, -0.5F - 3F / 16F, -0.5F + 1F / 16F);
		GL11.glRotatef(rot, 0F, 1F, 0F);
		model.render();
		GL11.glColor3f(1F, 1F, 1F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}
