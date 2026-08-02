/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Feb 16, 2014, 3:36:26 PM (GMT)]
 */
package vazkii.botania.common.block.subtile.functional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;
import vazkii.botania.common.lexicon.LexiconData;
import vazkii.botania.common.lib.LibObfuscation;
import vazkii.botania.utils.ReflectionUtils;

public class SubTileTigerseye extends SubTileFunctional {

	private static final Field TIME_SINCE_IGNITED = ReflectionUtils.findField(EntityCreeper.class, LibObfuscation.TIME_SINCE_IGNITED);
	private static final Field TARGET_ENTITY_CLASS = ReflectionUtils.findField(EntityAIAvoidEntity.class, LibObfuscation.TARGET_ENTITY_CLASS);
	private static final Field TARGET_CLASS = ReflectionUtils.findField(EntityAINearestAttackableTarget.class, LibObfuscation.TARGET_CLASS);

	private static final int RANGE = 10;
	private static final int RANGE_Y = 4;

	@Override
	public void onUpdate() {
		super.onUpdate();
		final int cost = 70;

		boolean shouldAfffect = mana >= cost;

		List<EntityLiving> entities = supertile.getWorldObj().getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(supertile.xCoord - RANGE, supertile.yCoord - RANGE_Y, supertile.zCoord - RANGE, supertile.xCoord + RANGE + 1, supertile.yCoord + RANGE_Y + 1, supertile.zCoord + RANGE + 1));

		for(EntityLiving entity : entities) {
			List<EntityAITaskEntry> entries = new ArrayList<>(entity.tasks.taskEntries);
			entries.addAll(new ArrayList<>(entity.targetTasks.taskEntries));

			boolean avoidsOcelots = false;
			if(shouldAfffect)
				for(EntityAITaskEntry entry : entries) {
					if(entry.action instanceof EntityAIAvoidEntity)
						avoidsOcelots = messWithRunAwayAI((EntityAIAvoidEntity) entry.action) || avoidsOcelots;

					if(entry.action instanceof EntityAINearestAttackableTarget)
						messWithGetTargetAI((EntityAINearestAttackableTarget) entry.action);
				}

			if(entity instanceof EntityCreeper) {
				ReflectionUtils.setInt(TIME_SINCE_IGNITED, entity, 2);
				entity.setAttackTarget(null);
			}

			if(avoidsOcelots) {
				mana -= cost;
				sync();
				shouldAfffect = false;
			}
		}
	}

	private boolean messWithRunAwayAI(EntityAIAvoidEntity aiEntry) {
		if(ReflectionUtils.getObject(TARGET_ENTITY_CLASS, aiEntry) == EntityOcelot.class) {
			ReflectionUtils.setObject(TARGET_ENTITY_CLASS, aiEntry, EntityPlayer.class);
			return true;
		}
		return false;
	}

	private void messWithGetTargetAI(EntityAINearestAttackableTarget aiEntry) {
		if(ReflectionUtils.getObject(TARGET_CLASS, aiEntry) == EntityPlayer.class)
			ReflectionUtils.setObject(TARGET_CLASS, aiEntry, EntityEnderCrystal.class); // Something random that won't be around
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(toChunkCoordinates(), RANGE);
	}

	@Override
	public int getColor() {
		return 0xB1A618;
	}

	@Override
	public int getMaxMana() {
		return 1000;
	}

	@Override
	public LexiconEntry getEntry() {
		return LexiconData.tigerseye;
	}

}
