
package vazkii.botania.common.block.subtile.generating;

import java.util.List;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;

import ic2.core.block.EntityIC2Explosive;

import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.Botania;
import vazkii.botania.common.lexicon.LexiconData;

public class SubTileReiujia extends SubTileEntropinnyum implements ISparkAttachable {
    
    public SubTileReiujia() {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
    
    private static final int RANGE = 12;
    
    @Override
    protected void handleExplosion(Entity tnt) {
        if (!supertile.getWorldObj().isRemote) { 
            tnt.setDead();
            mana += super.getMaxMana();
            supertile.getWorldObj().playSoundEffect( 
                tnt.posX, tnt.posY, tnt.posZ, 
                "random.explode", 
                0.2F, 
                (1F + (supertile.getWorldObj().rand.nextFloat() - supertile.getWorldObj().rand.nextFloat()) * 0.2F) * 0.7F);
            sync(); 
        } 
        for (int i = 0; i < 50; i++){
            Botania.proxy.sparkleFX( 
                tnt.worldObj, 
                tnt.posX + Math.random() * 4 - 2, 
                tnt.posY + Math.random() * 4 - 2, 
                tnt.posZ + Math.random() * 4 - 2, 
                1F, 
                (float) Math.random() * 0.25F, 
                (float) Math.random() * 0.25F, 
                (float) (Math.random() * 0.65F + 1.25F),
                12);
        }
        supertile.getWorldObj().spawnParticle( 
            "hugeexplosion", 
            tnt.posX, tnt.posY, tnt.posZ, 
            1D, 0D, 0D);
    }
        
    @Override
	public int getMaxMana() {
		return 100*super.getMaxMana();
	}
    
    public int getCurrentMana() {
        return mana;
    }
    
    @Override
	public LexiconEntry getEntry() {
		return LexiconData.reiujia;
	}
    
    public class EventHandler {
        
        @SubscribeEvent
        public void consumeExplosion(ExplosionEvent.Start event) {
            // duplicate code from SubTileEntropinnyum.EventHandler.consumeExplosion
            if(processExplosion(event.world, event.explosion.exploder, event.explosion.explosionX, event.explosion.explosionY, event.explosion.explosionZ)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        @Optional.Method(modid = "IC2")
        public void consumeExplosionIC2(ic2.api.event.ExplosionEvent event) {
            if(processExplosion(event.world, event.entity, event.x, event.y, event.z, event.power)) {
                event.setCanceled(true);
            }
        }
        
        private boolean processExplosion(World world, Entity explosionSource, double posX, double posY, double posZ) {
            return processExplosion(world, explosionSource, posX, posY, posZ, 0);
        }
            
        private boolean processExplosion(World world, Entity explosionSource, double posX, double posY, double posZ, double power) {
            /*System.out.println(
                world + " " + 
                explosionSource + " " + 
                posX + " " + 
                posY + " " + 
                posZ + " " + 
                power);*/
            if (world.isRemote ||
                mana != 0 ||
                !(  Math.abs(supertile.xCoord - posX) <= RANGE &&
                    Math.abs(supertile.yCoord - posY) <= RANGE &&
                    Math.abs(supertile.zCoord - posZ) <= RANGE)
            ) {
                return false;
            }
            
            int entropinnyumMaxMana = SubTileReiujia.super.getMaxMana();
            
            if (explosionSource != null){
                explosionSource.setDead();
                mana += entropinnyumMaxMana;
            } else {
                // ic2 reactor meltdowns have no explosionSource entity
                // so assume that that's what happened if we're here
                //
                // regarding power
                // per config nuclear explosion power is capped at 100, where a tnt is 4
                // the factor of 0.25 is not here because stronger flower and radiation I guess
                mana += entropinnyumMaxMana * (int)power;
            }
            // load bearing debug print ???
            System.out.println("mana " + mana);
            
            supertile.getWorldObj().playSoundEffect(
                posX,
                posY,
                posZ,
                "random.explode",
                0.2F,
                (1F + (supertile.getWorldObj().rand.nextFloat() - supertile.getWorldObj().rand.nextFloat()) * 0.2F) * 0.7F);
            sync();

            for(int i = 0; i < 50; i++) {
                Botania.proxy.sparkleFX(
                    world,
                    posX + Math.random() * 4 - 2,
                    posY + Math.random() * 4 - 2,
                    posZ + Math.random() * 4 - 2,
                    1F,
                    (float) Math.random() * 0.25F,
                    (float) Math.random() * 0.25F,
                    (float) (Math.random() * 0.65F + 1.25F),
                    12);
            }

            supertile.getWorldObj().spawnParticle("hugeexplosion", posX, posY, posZ, 1D, 0D, 0D);
            return true;
        }
    }
    
    //// ISparkAttachable
    
    @Override
	public boolean canAttachSpark(ItemStack stack) {
		return true;
	}

	@Override
	public void attachSpark(ISparkEntity entity) {
		// NO-OP
	}

	@Override
	public ISparkEntity getAttachedSpark() {
		List<ISparkEntity> sparks = supertile.getWorldObj().getEntitiesWithinAABB(
            ISparkEntity.class,
            AxisAlignedBB.getBoundingBox(
                supertile.xCoord,
                supertile.yCoord + 1,
                supertile.zCoord,
                supertile.xCoord + 1,
                supertile.yCoord + 2,
                supertile.zCoord + 1));
		if(sparks.size() == 1) {
			Entity e = (Entity) sparks.get(0);
			return (ISparkEntity) e;
		}

		return null;
	}
    
    // generating flower should never accept mana
    @Override
	public int getAvailableSpaceForMana() { return 0; }
    @Override
    public boolean areIncomingTranfersDone() { return true; }
    
    //// IManaReceiver
    
    @Override
    public boolean isFull() { return true; }
    @Override
	public void recieveMana(int mana) {
        // NO-OP
    }
    @Override
	public boolean canRecieveManaFromBursts() { return false; }
}