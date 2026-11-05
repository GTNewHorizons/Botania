/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 1, 2014, 9:20:33 PM (GMT)]
 */
package vazkii.botania.api.brew;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;

/**
 * An {@link Item} that implements this is an item which can contain a {@link Brew}.
 * <p>
 *    This is used in standard Botania to prevent finished brews from going back into the brewery & to enable NEI searches.
 *    However, other mods might use it for other purposes.
 * </p>
 */
public interface IBrewItem {

    /**
     * Get the brew contained within a stack of this item.
     *
     * @param brew A stack of this item whose contents are being queried.
     * @return The brew within this container, or {@link BotaniaAPI#fallbackBrew} or null if the stack does not contain
     *         a valid brew.
     */
    Brew getBrew(ItemStack brew);

}
