/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 25, 2015, 6:42:47 PM (GMT)]
 */
package vazkii.botania.common.item;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.achievement.ICraftAchievement;
import vazkii.botania.common.achievement.ModAchievements;
import vazkii.botania.common.entity.EntityThornChakram;
import vazkii.botania.common.lib.LibItemNames;

public class ItemThornChakram extends ItemMod implements ICraftAchievement {

	IIcon iconFire;

	public ItemThornChakram() {
		setUnlocalizedName(LibItemNames.THORN_CHAKRAM);
		setMaxStackSize(6);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for(int i = 0; i < 2; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		itemIcon = IconHelper.forItem(par1IconRegister, this, 0);
		iconFire = IconHelper.forItem(par1IconRegister, this, 1);
	}

	@Override
	public IIcon getIconFromDamage(int dmg) {
		return dmg == 0 ? itemIcon : iconFire;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + stack.getItemDamage();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player)  {
		--itemStackIn.stackSize;

		worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isRemote) {
			EntityThornChakram c = new EntityThornChakram(worldIn, player);
			c.setFire(itemStackIn.getItemDamage() != 0);
			worldIn.spawnEntityInWorld(c);
		}


		return itemStackIn;
	}

	@Override
	public Achievement getAchievementOnCraft(ItemStack stack, EntityPlayer player, IInventory matrix) {
		return ModAchievements.terrasteelWeaponCraft;
	}

}
