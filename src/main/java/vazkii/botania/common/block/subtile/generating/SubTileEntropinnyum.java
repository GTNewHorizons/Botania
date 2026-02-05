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

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.Botania;
import vazkii.botania.common.lexicon.LexiconData;

public class SubTileEntropinnyum extends SubTileGenerating {

    public SubTileEntropinnyum() {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

	private static final int RANGE = 12;

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(mana == 0) {
			List<EntityTNTPrimed> tnts = supertile.getWorldObj().getEntitiesWithinAABB(EntityTNTPrimed.class, AxisAlignedBB.getBoundingBox(supertile.xCoord - RANGE, supertile.yCoord - RANGE, supertile.zCoord - RANGE, supertile.xCoord + RANGE + 1, supertile.yCoord + RANGE + 1, supertile.zCoord + RANGE + 1));
			for(EntityTNTPrimed tnt : tnts) {
				if(tnt.fuse == 1 && !tnt.isDead && !supertile.getWorldObj().getBlock(MathHelper.floor_double(tnt.posX), MathHelper.floor_double(tnt.posY), MathHelper.floor_double(tnt.posZ)).getMaterial().isLiquid()) {
					if(!supertile.getWorldObj().isRemote) {
						tnt.setDead();
						mana += getMaxMana();
						supertile.getWorldObj().playSoundEffect(tnt.posX, tnt.posY, tnt.posZ, "random.explode", 0.2F, (1F + (supertile.getWorldObj().rand.nextFloat() - supertile.getWorldObj().rand.nextFloat()) * 0.2F) * 0.7F);
						sync();
					}

					for(int i = 0; i < 50; i++)
						Botania.proxy.sparkleFX(tnt.worldObj, tnt.posX + Math.random() * 4 - 2, tnt.posY + Math.random() * 4 - 2, tnt.posZ + Math.random() * 4 - 2, 1F, (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, (float) (Math.random() * 0.65F + 1.25F), 12);

					supertile.getWorldObj().spawnParticle("hugeexplosion", tnt.posX, tnt.posY, tnt.posZ, 1D, 0D, 0D);
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
	};

	@Override
	public LexiconEntry getEntry() {
		return LexiconData.entropinnyum;
	}

    public class EventHandler {

        @SubscribeEvent
        public void consumeExplosion(ExplosionEvent.Start event) {
            if(processExplosion(event.world, event.explosion.exploder, event.explosion.explosionX, event.explosion.explosionY, event.explosion.explosionZ)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        @Optional.Method(modid = "IC2")
        public void consumeExplosionIC2(ic2.api.event.ExplosionEvent event) {
            if(processExplosion(event.world, event.entity, event.x, event.y, event.z)) {
                event.setCanceled(true);
            }
        }

        private boolean processExplosion(World world, Entity explosionSource, double posX, double posY, double posZ) {
            if (world.isRemote || mana != 0 || !(Math.abs(supertile.xCoord - posX) <= RANGE && Math.abs(supertile.yCoord - posY) <= RANGE && Math.abs(supertile.zCoord - posZ) <= RANGE)) {
                return false;
            }

            explosionSource.setDead();
            mana += getMaxMana();
            supertile.getWorldObj().playSoundEffect(posX, posY, posZ, "random.explode", 0.2F, (1F + (supertile.getWorldObj().rand.nextFloat() - supertile.getWorldObj().rand.nextFloat()) * 0.2F) * 0.7F);
            sync();

            for(int i = 0; i < 50; i++) {
                Botania.proxy.sparkleFX(world, posX + Math.random() * 4 - 2, posY + Math.random() * 4 - 2, posZ + Math.random() * 4 - 2, 1F, (float) Math.random() * 0.25F, (float) Math.random() * 0.25F, (float) (Math.random() * 0.65F + 1.25F), 12);
            }

            supertile.getWorldObj().spawnParticle("hugeexplosion", posX, posY, posZ, 1D, 0D, 0D);
            return true;
        }
    }
}
