/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 22, 2015, 9:00:06 PM (GMT)]
 */
package vazkii.botania.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import vazkii.botania.client.lib.LibRenderIDs;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderDoubleFlower implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		// NO-OP
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockDoublePlant flower = (BlockDoublePlant) block;
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(flower.getMixedBrightnessForBlock(world, x, y, z));
		tessellator.setColorOpaque_F(1F, 1F, 1F);
        int meta = world.getBlockMetadata(x, y, z);
		boolean isTopHalf = BlockDoublePlant.func_149887_c(meta);
		if (isTopHalf)
		{
			if (world.getBlock(x, y - 1, z) != flower)
			{
				return false;
			}

			BlockDoublePlant.func_149890_d(world.getBlockMetadata(x, y - 1, z));
		}
		else
		{
			BlockDoublePlant.func_149890_d(meta);
		}

		// Only change here, to use xyz rather than side/meta
		IIcon icon = renderer.getBlockIcon(block, world, x, y, z, 0);
		RenderSpecialFlower.drawCrossedSquares(world, block, icon, x, y, z, x, y, z, 1F, renderer);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return LibRenderIDs.idDoubleFlower;
	}

}
