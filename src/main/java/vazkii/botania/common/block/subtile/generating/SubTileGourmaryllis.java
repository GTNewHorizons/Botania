/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jul 26, 2014, 1:42:17 PM (GMT)]
 */
package vazkii.botania.common.block.subtile.generating;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.lexicon.LexiconData;

public class SubTileGourmaryllis extends SubTileGenerating {

	private static final String TAG_COOLDOWN = "cooldown";
	private static final int RANGE = 1;

	int cooldown = 0;
	int storedMana = 0;

	@Override
	public void onUpdate() {
		super.onUpdate();

		//if(cooldown > 0)
		//	cooldown--;
		//if(cooldown == 0 && !supertile.getWorldObj().isRemote) {
		//	mana = Math.min(getMaxMana(), mana + storedMana);
		//	storedMana = 0;
		//	sync();
		//}
		if (cooldown > 0) {
			if (cooldown-- % 10 == 1) {
				mana = Math.min(getMaxMana(), mana + storedMana);
				sync();
			}
		}
		int slowdown = getSlowdownFactor();

		boolean remote = supertile.getWorldObj().isRemote;
		List<EntityItem> items = supertile.getWorldObj().getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(supertile.xCoord - RANGE, supertile.yCoord - RANGE, supertile.zCoord - RANGE, supertile.xCoord + RANGE + 1, supertile.yCoord + RANGE + 1, supertile.zCoord + RANGE + 1));
		for (EntityItem item : items) {
			ItemStack stack = item.getEntityItem();
			if (stack != null && stack.getItem() instanceof ItemFood && !item.isDead && item.age >= slowdown) {
				if (cooldown == 0) {
					if (!remote) {
						float val = ((ItemFood) stack.getItem()).func_150905_g(stack);
						float satMod = ((ItemFood) stack.getItem()).func_150906_h(stack);
						if (satMod > 0.5) // use saturation if higher
							val *= 2 * satMod; // saturation bars = hunger bars * saturation modifier * 2
						storedMana = (int) (val * 64 + 0.5); // round
						cooldown = (int) (val * 10 + 0.5);
						supertile.getWorldObj().playSoundEffect(supertile.xCoord, supertile.yCoord, supertile.zCoord, "random.eat", 0.2F, 0.5F + (float) Math.random() * 0.5F);
						sync();
					} else for (int i = 0; i < 10; i++) {
						float m = 0.2F;
						float mx = (float) (Math.random() - 0.5) * m;
						float my = (float) (Math.random() - 0.5) * m;
						float mz = (float) (Math.random() - 0.5) * m;
						supertile.getWorldObj().spawnParticle("iconcrack_" + Item.getIdFromItem(stack.getItem()), item.posX, item.posY, item.posZ, mx, my, mz);
					}
				}
				if (!remote)
					item.setDead();
			}
		}
	}

	@Override
	public void writeToPacketNBT(NBTTagCompound cmp) {
		super.writeToPacketNBT(cmp);
		cmp.setInteger(TAG_COOLDOWN, cooldown);
		cmp.setInteger(TAG_COOLDOWN, cooldown);
	}

	@Override
	public void readFromPacketNBT(NBTTagCompound cmp) {
		super.readFromPacketNBT(cmp);
		cooldown = cmp.getInteger(TAG_COOLDOWN);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(toChunkCoordinates(), RANGE);
	}

	@Override
	public int getMaxMana() {
		return 8000;
	}

	@Override
	public int getColor() {
		return 0xD3D604;
	}

	@Override
	public LexiconEntry getEntry() {
		return LexiconData.gourmaryllis;
	}

}
