/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 16, 2014, 5:30:52 PM (GMT)]
 */
package vazkii.botania.client.core.helper;

public final class FontHelper {

	public static boolean isFormatColor(char letter) {
		return letter >= 48 && letter <= 57 || letter >= 97 && letter <= 102 || letter >= 65 && letter <= 70;
	}

	public static boolean isFormatSpecial(char letter) {
		return letter >= 107 && letter <= 111 || letter >= 75 && letter <= 79 || letter == 114 || letter == 82;
	}

	public static String getFormatFromString(String par0Str) {
		String s1 = "";
		int i = -1;
		int j = par0Str.length();

		while ((i = par0Str.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				char c0 = par0Str.charAt(i + 1);

				if (isFormatColor(c0))
					s1 = "\u00a7" + c0;
				else if (isFormatSpecial(c0))
					s1 = s1 + "\u00a7" + c0;
			}
		}

		return s1;
	}

}
