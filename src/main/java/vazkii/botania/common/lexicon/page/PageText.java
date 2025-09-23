/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 14, 2014, 6:45:33 PM (GMT)]
 */
package vazkii.botania.common.lexicon.page;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PageText extends LexiconPage {

	public PageText(String unlocalizedName) {
		super(unlocalizedName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderScreen(IGuiLexiconEntry gui, int mx, int my) {
		int width = gui.getWidth() - 30;
		int x = gui.getLeft() + 16;
		int y = gui.getTop() + 2;

		renderText(x, y, width, gui.getHeight(), getUnlocalizedName());
	}

	@SideOnly(Side.CLIENT)
	public static void renderText(int x, int y, int width, int paragraphSize, String unlocalizedText) {
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		boolean unicode = font.getUnicodeFlag();
		font.setUnicodeFlag(true);

		String text = StatCollector.translateToLocal(unlocalizedText).replaceAll("&", "\u00a7");
		String[] paragraphs = text.split("<br>");

		for (String paragraph : paragraphs) {
			StringBuilder processed = new StringBuilder();
			for (int i = 0; i < paragraph.length(); i++) {
				char c = paragraph.charAt(i);
				if (c == ' ' && i > 0 && i < paragraph.length() - 1) {
					char prev = paragraph.charAt(i - 1);
					char next = paragraph.charAt(i + 1);
					if (isCJK(prev) && isCJK(next)) {
						processed.append('\n');
						continue;
					}
				}
				processed.append(c);
			}

			String[] preSplit = processed.toString().split("\n");
			for (String block : preSplit) {
				List<String> lines = font.listFormattedStringToWidth(block, width - 4);
				for (String line : lines) {
					font.drawString(line, x + 2, y + 10, 0x000000);
					y += 10;
				}
			}
			y += 10;
		}

		font.setUnicodeFlag(unicode);
	}

	public static boolean isCJK(char c) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
		return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| block == Character.UnicodeBlock.HIRAGANA
				|| block == Character.UnicodeBlock.KATAKANA
				|| block == Character.UnicodeBlock.HANGUL_SYLLABLES
				|| block == Character.UnicodeBlock.HANGUL_JAMO
				|| block == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO;
	}
}
