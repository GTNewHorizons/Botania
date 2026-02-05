/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 29, 2015, 7:42:52 PM (GMT)]
 */
package vazkii.botania.client.gui.lexicon.button;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.gui.lexicon.GuiLexicon;

public class GuiButtonChallengeInfo extends GuiButtonLexicon {

	GuiLexicon gui;

	public GuiButtonChallengeInfo(int id, int xPos, int yPos, String text, GuiLexicon gui) {
		super(id, xPos, yPos, gui.bookmarkWidth(text) + 5, 11, text);
		this.gui = gui;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		gui.drawBookmark(xPosition, yPosition, displayString, false);
		field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		int k = getHoverState(field_146123_n);

		List<String> tooltip = new ArrayList<>();
		tooltip.add(StatCollector.translateToLocal("botaniamisc.challengeInfo"));

		int tooltipY = (tooltip.size() + 1) * 5;
		if(k == 2)
			RenderHelper.renderTooltip(mouseX, mouseY + tooltipY, tooltip);
	}

}