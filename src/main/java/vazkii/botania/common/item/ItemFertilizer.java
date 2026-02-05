/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 29, 2014, 10:41:52 PM (GMT)]
 */
package vazkii.botania.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.lib.LibItemNames;

public class ItemFertilizer extends ItemMod {

	public ItemFertilizer() {
		super();
		setUnlocalizedName(LibItemNames.FERTILIZER);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		final int range = 3;
		if(!world.isRemote) {
			List<ChunkCoordinates> validCoords = new ArrayList<>();

			for(int i = -range - 1; i < range; i++)
				for(int j = -range - 1; j < range; j++) {
					for(int k = 2; k >= -2; k--) {
						int tryX = x + i + 1;
						int tryY = y + k + 1;
						int tryZ = z + j + 1;
						if(world.isAirBlock(tryX, tryY, tryZ) && (!world.provider.hasNoSky || tryY < 255) && ModBlocks.flower.canBlockStay(world, tryX, tryY, tryZ))
							validCoords.add(new ChunkCoordinates(tryX, tryY, tryZ));
					}
				}

			int flowerCount = Math.min(validCoords.size(), world.rand.nextBoolean() ? 3 : 4);
			for(int i = 0; i < flowerCount; i++) {
				ChunkCoordinates coords = validCoords.get(world.rand.nextInt(validCoords.size()));
				validCoords.remove(coords);
				world.setBlock(coords.posX, coords.posY, coords.posZ, ModBlocks.flower, world.rand.nextInt(16), 1 | 2);
			}
			stack.stackSize--;
		} else {
			for(int i = 0; i < 15; i++) {
				double px = x - range + world.rand.nextInt(range * 2 + 1) + Math.random();
				double py = y + 1;
				double pz = z - range + world.rand.nextInt(range * 2 + 1) + Math.random();
				float red = (float) Math.random();
				float green = (float) Math.random();
				float blue = (float) Math.random();
				Botania.proxy.wispFX(world, px, py, pz, red, green, blue, 0.15F + (float) Math.random() * 0.25F, -(float) Math.random() * 0.1F - 0.05F);
			}
		}

		return true;
	}
}
