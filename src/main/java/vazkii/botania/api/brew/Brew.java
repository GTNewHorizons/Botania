/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 1, 2014, 6:22:54 PM (GMT)]
 */
package vazkii.botania.api.brew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

/**
 * A type of brew that can be created in the Botanical Brewery.
 *
 * <p>
 *     Each instance of this class represents a single <i>type</i> of brew that can be created in the brewery, similarly
 *     to how {@link net.minecraft.item.Item} and {@link net.minecraft.block.Block} work.
 * </p>
 */
public class Brew {

	String key;
	String name;
	int color;
	int cost;
	List<PotionEffect> effects;
	boolean canInfuseBloodPendant = true;
	boolean canInfuseIncense = true;

	/**
     * @param key The key for this brew, used in NBT and the global map.
	 * @param name The unlocalized name of this potion.
	 * @param color The color for the potion to be rendered in the bottle. Note that it will get
	 *              changed a bit when it renders (for more or less brightness) to give a fancy effect.
	 * @param cost The cost, in Mana for this brew. Must be positive.
	 * @param effects A list of potion effects to apply to the player when they drink it.
	 */
	public Brew(String key, String name, int color, int cost, PotionEffect... effects) {
		this.key = key;
		this.name = name;
		this.color = color;
		this.cost = cost;
		this.effects = new ArrayList<>(Arrays.asList(effects));
	}

	/**
	 * Sets this brew to not be able to be infused onto the Tainted Blood Pendant.
	 */
	public Brew setNotBloodPendantInfusable() {
		canInfuseBloodPendant = false;
		return this;
	}

	/**
	 * Sets this brew to not be able to be infused onto Incense Sticks.
	 */
	public Brew setNotIncenseInfusable() {
		canInfuseIncense = false;
		return this;
	}

	public boolean canInfuseBloodPendant() {
		return canInfuseBloodPendant;
	}

	public boolean canInfuseIncense() {
		return canInfuseIncense;
	}

	/**
	 * Returns the key for this brew, for it to be found in the map in the API.
	 * This should ALWAYS return the same result.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the insensitive unlocalized name. This is used for the lexicon.
	 */
	public String getUnlocalizedName() {
		return name;
	}

	/**
	 * Gets the unlocalized name for the ItemStack passed in.
	 */
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName();
	}

	/**
	 * Gets the display color for the ItemStack passed in.  Note that for
	 * the lexicon, this passes in a botania Managlass Vial or an
	 * Alfglass Flask at all times.
	 */
	public int getColor(ItemStack stack) {
		return color;
	}

	/**
	 * Get the item-insensitive mana cost. This is used for the lexicon and for crafting costs.
	 */
	public int getManaCost() {
		return cost;
	}

	/**
	 * Gets the mana cost for the ItemStack passed in. In standard Botania, this is used only by the Cursed Blood Pendant.
	 */
	public int getManaCost(ItemStack stack) {
		return getManaCost();
	}

	/**
	 * Gets the list of potion effects for the ItemStack passed in.
	 * Note that for the lexicon, this passes in a botania Managlass
	 * Vial or an Alfglass Flask at all times.
	 */
	public List<PotionEffect> getPotionEffects(ItemStack stack) {
		return effects;
	}

}
