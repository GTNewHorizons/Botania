/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 18, 2015, 3:15:38 PM (GMT)]
 */
package vazkii.botania.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import vazkii.botania.client.lib.LibResources;
import vazkii.botania.client.model.ModelPump;
import vazkii.botania.common.block.tile.mana.TilePump;

public class RenderTilePump extends TileEntitySpecialRenderer {

	private static final float[] ROTATIONS = new float[] {
		180F, 0F, 90F, 270F
	};

	private static final ResourceLocation texture = new ResourceLocation(LibResources.MODEL_PUMP);
	private static final ModelPump model = new ModelPump();

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TilePump pump = (TilePump) tileEntity;

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glTranslated(x, y, z);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		int meta = pump.getWorldObj() != null ? pump.getBlockMetadata() : 0;

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glRotatef(ROTATIONS[Math.max(Math.min(ROTATIONS.length - 1, meta - 2), 0)], 0F, 1F, 0F);
		model.render(Math.max(0F, Math.min(8F, pump.innerRingPos + pump.moving * partialTicks)));
		GL11.glColor3f(1F, 1F, 1F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

}
