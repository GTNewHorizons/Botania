/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 3, 2014, 12:13:09 AM (GMT)]
 */
package vazkii.botania.common.brew.potion;

import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import vazkii.botania.common.brew.ModPotions;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.lib.LibPotionNames;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PotionBloodthirst extends PotionMod {

	private static final int RANGE = 64;

	public PotionBloodthirst() {
		super(ConfigHandler.potionIDBloodthirst, LibPotionNames.BLOODTHIRST, false, 0xC30000, 3);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public class EventHandler {
		@SubscribeEvent
		public void onSpawn(LivingSpawnEvent.CheckSpawn event) {
			if (event.getResult() != Result.ALLOW && event.entityLiving instanceof IMob) {
				double rangeSq = RANGE * RANGE;
				for (Object o : event.world.playerEntities) {
                    EntityPlayer player = (EntityPlayer) o;
                    if (player.getDistanceSq(event.x, event.y, event.z) <= rangeSq
                        && hasEffect(player) && !hasEffect(player, ModPotions.emptiness)) {
                        event.setResult(Result.ALLOW);
                        return;
					}
				}
			}
		}
	}
}
