/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 7, 2014, 10:59:44 PM (GMT)]
 */
package vazkii.botania.common.block.subtile.generating;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.Botania;
import vazkii.botania.common.lexicon.LexiconData;
import ic2.core.block.EntityIC2Explosive;

public class SubTileEntropinnyum extends SubTileGenerating {

	private static final int RANGE = 12;

	@Override
	public void onUpdate() {
		super.onUpdate();
    AxisAlignedBB box = AxisAlignedBB.getBoundingBox(supertile.xCoord - RANGE, supertile.yCoord - RANGE, supertile.zCoord - RANGE, supertile.xCoord + RANGE + 1, supertile.yCoord + RANGE + 1, supertile.zCoord + RANGE + 1);

		if(mana == 0) {
			List<EntityTNTPrimed> tnts = supertile.getWorldObj().getEntitiesWithinAABB(EntityTNTPrimed.class, box);
			for(EntityTNTPrimed tnt : tnts) {
				if(tnt.fuse == 1 && canConsume(tnt)) {
					consumeExplosion(tnt);
					showParticles(tnt.worldObj, tnt.posX, tnt.posY, tnt.posZ);
					return;
				}
			}
			List<EntityIC2Explosive> itnts = supertile.getWorldObj().getEntitiesWithinAABB(EntityIC2Explosive.class, box);
			for(EntityIC2Explosive itnt : itnts) {
				if(itnt.fuse == 1 && canConsume(itnt)) {
					consumeExplosion(itnt);
          showParticles(itnt.worldObj, itnt.posX, itnt.posY, itnt.posZ);
					return;
				}
			}
		}
	}

	@Override
	public int getColor() {
		return 0xcb0000;
	}

	@Override
	public int getMaxMana() {
		return 6500;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(toChunkCoordinates(), RANGE);
	}

	@Override
	public LexiconEntry getEntry() {
		return LexiconData.entropinnyum;
	}
  
  private boolean canConsume(Entity e) {
    return !e.isDead && !supertile.getWorldObj().getBlock(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY), MathHelper.floor_double(e.posZ)).getMaterial().isLiquid();
  }
  
  private void consumeExplosion(Entity e) {
    if(!supertile.getWorldObj().isRemote) {
      e.setDead();
      mana += getMaxMana();
      supertile.getWorldObj().playSoundEffect(e.posX, e.posY, e.posZ, "random.explode", 0.2F, (1F + (supertile.getWorldObj().rand.nextFloat() - supertile.getWorldObj().rand.nextFloat()) * 0.2F) * 0.7F);
      sync();
    }
  }
  
  private void showParticles(World world, double x, double y, double z) {
    for(int i = 0; i < 50; i++)
      Botania.proxy.sparkleFX(world, x + Math.random() * 4 - 2, y+ Math.random() * 4 - 2, z + Math.random() * 4 - 2, 1F, (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, (float) (Math.random() * 0.65F + 1.25F), 12);

    supertile.getWorldObj().spawnParticle("hugeexplosion", x, y, z, 1D, 0D, 0D);
    return;
  }

}
