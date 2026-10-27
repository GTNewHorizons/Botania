/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.subtile;

/**
 * Relative strength shown in Mana info tooltips when exact numbers are turned off.
 */
public enum ManaInfoTier {

	VERY_LOW("veryLow"),
	LOW("low"),
	MODERATE("moderate"),
	HIGH("high"),
	VERY_HIGH("veryHigh");

	private final String name;

	ManaInfoTier(String name) {
		this.name = name;
	}

	/**
	 * Returns the lang key for this tier's localized label.
	 */
	public String getUnlocalizedName() {
		return "botania.manaInfo.tier." + name;
	}

}
