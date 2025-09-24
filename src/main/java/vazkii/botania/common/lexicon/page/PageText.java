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

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.LexiconPage;
import vazkii.botania.common.core.handler.ConfigHandler;

import com.google.common.base.Joiner;

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

	public static void renderText(int x, int y, int width, int height, String unlocalizedText) {
		renderText(x, y, width, height, 10, unlocalizedText);
	}

	@SideOnly(Side.CLIENT)
	public static void renderText(int x, int y, int width, int height, int paragraphSize, String unlocalizedText) {
        y += 10;
        width -= 4;

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        boolean unicode = font.getUnicodeFlag();
        font.setUnicodeFlag(true);
        String text = StatCollector.translateToLocal(unlocalizedText).replaceAll("&", "\u00a7");
        String[] textEntries = text.split("<br>");

        List<List<String>> lines = new ArrayList<>(textEntries.length * 2);

        String controlCodes;
        var lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        var locale = Locale.forLanguageTag(lang.toString().replace(" (", "-").replace(")", ""));
        if (locale == null) {
            locale = Locale.forLanguageTag(lang.getLanguageCode());
        }
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        for (String s : textEntries) {
            List<String> words = new ArrayList<>();
            String lineStr = "";
            var breaker = BreakIterator.getLineInstance(locale);
            breaker.setText(s);
            int start = breaker.first();
            for (int end = breaker.next(); end != BreakIterator.DONE; start = end, end = breaker.next()) {
                String token = s.substring(start, end);
                String prev = lineStr;
                lineStr += token;

                controlCodes = toControlCodes(getControlCodes(prev));
                if (font.getStringWidth(lineStr) > width) {
                    lines.add(words);
                    lineStr = controlCodes + token;
                    words = new ArrayList<>();
                }

                words.add(controlCodes + token);
            }

            if (!lineStr.isEmpty())
                lines.add(words);
            lines.add(new ArrayList<>());
        }

        int i = 0;
        for (List<String> words : lines) {
            int xi = x;

            List<Integer> gapIds= new ArrayList<>(words.size());
            for (int k = 0; k < words.size() - 1; k++) {
                if (font.getStringWidth(words.get(k)) > 0 &&
                        font.getStringWidth(words.get(k + 1)) > 0) {
                    gapIds.add(k);
                }
            }

            boolean nextLineExists = (i + 1) < lines.size();
            boolean justify = ConfigHandler.lexiconJustifiedText
                    && !words.isEmpty()
                    && nextLineExists
                    && !lines.get(i + 1).isEmpty()
                    && !gapIds.isEmpty();

            int spacing = 0;
            int compensation = 0;

            if (justify) {
                String joined = Joiner.on("").join(words);
                int swidth = font.getStringWidth(joined);
                int extraSpace = width - swidth;

                int gaps = gapIds.size();
                spacing = extraSpace / gaps;
                compensation = extraSpace % gaps;
            }

            int gi = 0;
            for (int idx = 0; idx < words.size(); idx++) {
                String tok = words.get(idx);
                int tw = font.getStringWidth(tok);

                font.drawString(tok, xi, y, 0);
                xi += tw;

                if (justify && gi < gapIds.size() && idx == gapIds.get(gi)) {
                    xi += spacing + (compensation > 0 ? 1 : 0);
                    if (compensation > 0) compensation--;
                    gi++;
                }
            }

            y += words.isEmpty() ? paragraphSize : 10;
            i++;
        }

		font.setUnicodeFlag(unicode);
	}

	public static String getControlCodes(String s) {
		String controls = s.replaceAll("(?<!\u00a7)(.)", "");
		String wiped = controls.replaceAll(".*r", "r");
		return wiped;
	}

	public static String toControlCodes(String s) {
		return s.replaceAll(".", "\u00a7$0");
	}

}
