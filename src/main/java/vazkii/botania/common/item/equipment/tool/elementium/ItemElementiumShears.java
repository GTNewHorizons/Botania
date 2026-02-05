package vazkii.botania.common.item.equipment.tool.elementium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import vazkii.botania.client.core.helper.IconHelper;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.manasteel.ItemManasteelShears;
import vazkii.botania.common.lib.LibItemNames;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElementiumShears extends ItemManasteelShears {

	IIcon dammitReddit;

	public ItemElementiumShears() {
		super(LibItemNames.ELEMENTIUM_SHEARS);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.setItemInUse(stack, getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		dammitReddit = IconHelper.forName(register, "dammitReddit");
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return stack.getDisplayName().equalsIgnoreCase("dammit reddit") ? dammitReddit : super.getIconIndex(stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
		if(player.worldObj.isRemote)
			return;

		if(count != getMaxItemUseDuration(stack) && count % 5 == 0) {
			int range = 12;
			List<IShearable> sheep = player.worldObj.getEntitiesWithinAABB(IShearable.class, AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range));
			if(sheep.size() > 0) {
				for(IShearable target : sheep) {
					Entity entity = (Entity) target;
					if(target.isShearable(stack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
						ArrayList<ItemStack> drops = target.onSheared(stack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack));

						Random rand = new Random();
						for(ItemStack drop : drops) {
							EntityItem ent = entity.entityDropItem(drop, 1.0F);
							ent.motionY += rand.nextFloat() * 0.05F;
							ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
							ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						}

						ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE);
						break;
					}
				}
			}
		}
	}

}
