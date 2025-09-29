/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Sep 3, 2014, 6:31:10 PM (GMT)]
 */
package vazkii.botania.common.item;

import baubles.common.lib.PlayerHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.crafting.recipe.RegenIvyRecipe;
import vazkii.botania.common.lib.LibItemNames;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegenIvy extends ItemMod {

	public static final String TAG_REGEN = "Botania_regenIvy";
	private static final int MANA_PER_DAMAGE = 200;

	public ItemRegenIvy() {
		setUnlocalizedName(LibItemNames.REGEN_IVY);
		GameRegistry.addRecipe(new RegenIvyRecipe());
		RecipeSorter.register("botania:regenIvy", RegenIvyRecipe.class, Category.SHAPELESS, "");
		FMLCommonHandler.instance().bus().register(new EventHandler());
	}

	public void onTick(PlayerTickEvent event) {
        if (event.phase != Phase.END || event.player.worldObj.isRemote) {
            return;
        }

        for (int i = 0; i < event.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = event.player.inventory.getStackInSlot(i);
            if (canBeRepaired(event, stack)) stack.setItemDamage(stack.getItemDamage() - 1);
        }

        ItemStack[] baubles = PlayerHandler.getPlayerBaubles(event.player).stackList;
		if (baubles != null) {
			for (int i = 0; i < baubles.length; i++) { // Basic for loop to avoid iterator in onTick method
				ItemStack bauble = baubles[i];
				if (canBeRepaired(event, bauble)) bauble.setItemDamage(bauble.getItemDamage() - 1);
			}
		}
    }

	private static boolean canBeRepaired(PlayerTickEvent event, ItemStack stack) {
		return stack != null && ItemNBTHelper.detectNBT(stack) && ItemNBTHelper.getBoolean(stack, TAG_REGEN, false) && stack.getItemDamage() > 0 && ManaItemHandler.requestManaExact(stack, event.player, MANA_PER_DAMAGE, true);
	}

	public class EventHandler {
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public void onTickWrapper(PlayerTickEvent event) {
			ItemRegenIvy.this.onTick(event);
		}
	}
}
