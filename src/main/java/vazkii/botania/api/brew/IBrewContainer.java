/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 1, 2014, 6:26:40 PM (GMT)]
 */
package vazkii.botania.api.brew;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * An {@link Item} that implements this counts as a Brew Container, which allows it to be used a center item in the Botanical Brewery.
 */
public interface IBrewContainer {

	/**
	 * Fills an {@link ItemStack} of this container with a {@link Brew}.
     * If the brew cannot be applied to this container, returns null instead.
     *
     * @param brew The brew which may or may not be applicable to this container.
     * @param stack The stack of this item which is being filled. This stack is not mutated.
     * @return A <i>new</i> {@link ItemStack} containing an {@link IBrewItem}, or null if this brew is not applicable.
     * @apiNote The returned stack does not necessarily have to be the same {@link Item} as the empty container.
	 */
    ItemStack getItemForBrew(Brew brew, ItemStack stack);

	/**
	 * Gets the cost to add this brew onto this container. The cost must be a positive number.
     *
     * @param brew The brew being applied, which must be applicable to this container. {@link IBrewContainer#getItemForBrew(Brew, ItemStack)} can be used to determine whether the brew is applicable.
     * @param stack A stack of this empty container. This stack is not mutated.
     * @return The mana cost for this recipe, which is a positive number.
	 */
	int getManaCost(Brew brew, ItemStack stack);

}
