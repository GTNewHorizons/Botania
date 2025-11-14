/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 26, 2014, 12:25:11 AM (GMT)]
 */
package vazkii.botania.client.render.tile;

import java.awt.Color;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import vazkii.botania.api.mana.IPoolOverlayProvider;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.handler.MultiblockRenderHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.core.helper.ShaderHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.client.model.ModelPool;
import vazkii.botania.common.block.mana.BlockPool;
import vazkii.botania.common.block.tile.mana.TilePool;

public class RenderTilePool extends TileEntitySpecialRenderer {

	private static final ResourceLocation texture = new ResourceLocation(LibResources.MODEL_POOL);
	private static final ResourceLocation textureInf = new ResourceLocation(LibResources.MODEL_INFINITE_POOL);
	private static final ResourceLocation textureDil = new ResourceLocation(LibResources.MODEL_DILUTED_POOL);

	private static final ModelPool model = new ModelPool();
	RenderItem renderItem = new RenderItem();

	public static int forceMeta = 0;
	public static boolean forceMana = false;
	public static int forceManaNumber = -1;

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TilePool pool = (TilePool) tileEntity;

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float a = MultiblockRenderHandler.rendering ? 0.6F : 1F;
		GL11.glColor4f(1F, 1F, 1F, a);
		GL11.glTranslated(x, y, z);
		boolean inf = tileEntity.getWorldObj() == null ? forceMeta == 1 : tileEntity.getBlockMetadata() == 1;
		boolean dil = tileEntity.getWorldObj() == null ? forceMeta == 2 : tileEntity.getBlockMetadata() == 2;
		boolean fab = tileEntity.getWorldObj() == null ? forceMeta == 3 : tileEntity.getBlockMetadata() == 3;

		Minecraft.getMinecraft().renderEngine.bindTexture(inf ? textureInf : dil ? textureDil : texture);

		GL11.glTranslatef(0.5F, 1.5F, 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		if(fab) {
			float time = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks;
			if(tileEntity != null)
				time += new Random(tileEntity.xCoord ^ tileEntity.yCoord ^ tileEntity.zCoord).nextInt(100000);

			Color color = Color.getHSBColor(time * 0.005F, 0.6F, 1F);
			GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 255);
		} else {
			int color = pool.color;
			float[] acolor = EntitySheep.fleeceColorTable[color];
			GL11.glColor4f(acolor[0], acolor[1], acolor[2], a);
		}

		model.render();
		GL11.glColor4f(1F, 1F, 1F, a);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

		int mana = pool.getCurrentMana();
		if(forceManaNumber > -1)
			mana = forceManaNumber;
		int cap = pool.manaCap;
		if(cap == -1)
			cap = TilePool.MAX_MANA;

		float waterLevel = (float) mana / (float) cap * 0.4F;
		if(forceMana)
			waterLevel = 0.4F;

		float s = 1F / 16F;
		float v = 1F / 8F;
		float w = -v * 3.5F;

		if(pool.getWorldObj() != null) {
			Block below = pool.getWorldObj().getBlock(pool.xCoord, pool.yCoord - 1, pool.zCoord);
			if(below instanceof IPoolOverlayProvider) {
				IIcon overlay = ((IPoolOverlayProvider) below).getIcon(pool.getWorldObj(), pool.xCoord, pool.yCoord - 1, pool.zCoord);
				if(overlay != null) {
					GL11.glPushMatrix();
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glColor4f(1F, 1F, 1F, a * (float) ((Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 20.0) + 1) * 0.3 + 0.2));
					GL11.glTranslatef(-0.5F, -1F - 0.43F, -0.5F);
					GL11.glRotatef(90F, 1F, 0F, 0F);
					GL11.glScalef(s, s, s);

					RenderHelper.renderIcon(0, 0, overlay, 16, 16, 240);

					GL11.glEnable(GL11.GL_ALPHA_TEST);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glPopMatrix();
				}
			}
		}

		if(waterLevel > 0) {
			s = 1F / 256F * 14F;
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1F, 1F, 1F, a);
			GL11.glTranslatef(w, -1F - (0.43F - waterLevel), w);
			GL11.glRotatef(90F, 1F, 0F, 0F);
			GL11.glScalef(s, s, s);

			ShaderHelper.useShader(ShaderHelper.manaPool);
			RenderHelper.renderIcon(0, 0, BlockPool.manaIcon, 16, 16, 240);
			ShaderHelper.releaseShader();

			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		forceMeta = 0;
		forceMana = false;
		forceManaNumber = -1;
	}
}
