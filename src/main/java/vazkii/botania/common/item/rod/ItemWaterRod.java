package vazkii.botania.common.item.rod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.item.ItemMod;
import vazkii.botania.common.lib.LibItemNames;

public class ItemWaterRod extends ItemMod implements IManaUsingItem {

	public static final int COST = 75;

	public ItemWaterRod() {
		super();
		setMaxStackSize(1);
		setUnlocalizedName(LibItemNames.WATER_ROD);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		if(ManaItemHandler.requestManaExactForTool(stack, player, COST, false) && !world.provider.isHellWorld) {
			ForgeDirection dir = ForgeDirection.getOrientation(side);

			ItemStack stackToPlace = new ItemStack(Blocks.flowing_water);
			stackToPlace.tryPlaceItemIntoWorld(player, world, x, y, z, side, subX, subY, subZ);

			if(stackToPlace.stackSize == 0) {
				ManaItemHandler.requestManaExactForTool(stack, player, COST, true);
				for(int i = 0; i < 6; i++)
					Botania.proxy.sparkleFX(world, x + dir.offsetX + Math.random(), y + dir.offsetY + Math.random(), z + dir.offsetZ + Math.random(), 0.2F, 0.2F, 1F, 1F, 5);
			}
		}
		return true;
	}

	@Override
	public boolean isFull3D() {
		return true;
	}

	@Override
	public boolean usesMana(ItemStack stack) {
		return true;
	}

}
