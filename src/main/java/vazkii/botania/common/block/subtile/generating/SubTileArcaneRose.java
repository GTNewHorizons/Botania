/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 18, 2014, 8:45:25 PM (GMT)]
 */
package vazkii.botania.common.block.subtile.generating;

import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileGenerating;
import vazkii.botania.common.core.helper.ExperienceHelper;
import vazkii.botania.common.lexicon.LexiconData;

public class SubTileArcaneRose extends SubTileGenerating {

	private static final int MANA_PER_XP = 50;
	private static final int RANGE = 1;

	@Override
	public void onUpdate() {
		super.onUpdate();

		World world = supertile.getWorldObj();

		if (mana >= getMaxMana() || world.isRemote) {
			return;
		}

		AxisAlignedBB effectBounds = AxisAlignedBB.getBoundingBox(supertile.xCoord - RANGE, supertile.yCoord, supertile.zCoord - RANGE, supertile.xCoord + RANGE + 1, supertile.yCoord + 1, supertile.zCoord + RANGE + 1);
		List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, effectBounds);
		for (EntityPlayer player : players) {
            if (ExperienceHelper.getPlayerXP(player) <= 0 || !player.onGround) continue;
			ExperienceHelper.drainPlayerXP(player, 1);
			addMana(MANA_PER_XP);
            return;
        }

		List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(EntityXPOrb.class, effectBounds);
		for (EntityXPOrb orb : orbs) {
            if (orb.isDead) continue;
            addMana(orb.getXpValue() * MANA_PER_XP);
            orb.setDead();
            float pitch = (world.rand.nextFloat() - world.rand.nextFloat()) * 0.35F + 0.9F;
            world.playSoundAtEntity(orb, "random.orb", 0.1F, pitch);
            return;
        }

		List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, effectBounds);
		for (EntityItem entity : items) {
			ItemStack oldStack = entity.getEntityItem();
			if (entity.isDead || oldStack == null) continue;
            if (oldStack.getItem() != Items.enchanted_book && !oldStack.isItemEnchanted()) {
                continue;
            }
            int xp = getEnchantmentXpValue(oldStack);
            if (xp <= 0) continue;
            ItemStack newStack = disenchantAndSplit(oldStack);
			if (oldStack.stackSize <= 0) entity.setDead();

            EntityItem newEntity = new EntityItem(world, entity.posX, entity.posY, entity.posZ, newStack);
			newEntity.motionX = entity.motionX;
			newEntity.motionY = entity.motionY;
			newEntity.motionZ = entity.motionZ;
			newEntity.age = entity.age;
			newEntity.delayBeforeCanPickup = entity.delayBeforeCanPickup;
            world.spawnEntityInWorld(newEntity);

            while (xp > 0) {
                int i = EntityXPOrb.getXPSplit(xp);
                xp -= i;
                world.spawnEntityInWorld(new EntityXPOrb(world, newEntity.posX, newEntity.posY, newEntity.posZ, i));
            }
            return;
        }
	}

	/**
     * Modified method from GrindstoneMenu
     * Removed check for curses since they don't exist in this version
     */
    private static int getEnchantmentXpValue(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			// Don't remove "enchants" from Tinkers' Construct tools
			if (tag.hasKey("InfiTool")) return 0;
		}

		int ret = 0;
		Map<Integer, Integer> map = EnchantmentHelper.getEnchantments(stack);

		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			Enchantment enchantment = Enchantment.enchantmentsList[entry.getKey()];
			Integer integer = entry.getValue();
			ret += enchantment.getMinEnchantability(integer);
		}

		return ret;
	}

	/**
     * Modified method from GrindstoneMenu, no damage and count setting
     * Removed check for curses since they don't exist in this version
     */
    private static ItemStack disenchantAndSplit(ItemStack stack) {
		ItemStack newStack = stack.splitStack(1);

		if (newStack.hasTagCompound()) {
			NBTTagCompound tag = newStack.getTagCompound();
			tag.removeTag("ench");
			tag.removeTag("RepairCost");
			if (tag.hasNoTags()) {
				newStack.setTagCompound(null);
			}
		}

		if (stack.getItem() == Items.enchanted_book) {
			newStack = new ItemStack(Items.book);
			if (stack.hasDisplayName()) {
				newStack.setStackDisplayName(stack.getDisplayName());
			}
		}

		return newStack;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(toChunkCoordinates(), RANGE);
	}

	@Override
	public int getColor() {
		return 0xFF8EF8;
	}

	@Override
	public int getMaxMana() {
		return 6000;
	}

	@Override
	public LexiconEntry getEntry() {
		return LexiconData.arcaneRose;
	}

}
