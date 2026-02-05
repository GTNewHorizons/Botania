package vazkii.botania.client.gui.lexicon.button;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.gui.lexicon.GuiLexicon;

public class GuiButtonBookmark extends GuiButtonLexicon {

	GuiLexicon gui;

	public GuiButtonBookmark(int id, int xPos, int yPos, GuiLexicon gui, String text) {
		super(id, xPos, yPos, gui.bookmarkWidth(text) + 5, 11, text);
		this.gui = gui;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		gui.drawBookmark(xPosition, yPosition, displayString, false);
		field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		int k = getHoverState(field_146123_n);

		List<String> tooltip = new ArrayList<>();
		if(displayString.equals("+"))
			tooltip.add(StatCollector.translateToLocal("botaniamisc.clickToAdd"));
		else {
			tooltip.add(String.format(StatCollector.translateToLocal("botaniamisc.bookmark"), id - GuiLexicon.BOOKMARK_START + 1));
			tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("botaniamisc.clickToSee"));
			tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("botaniamisc.shiftToRemove"));
		}

		int tooltipY = (tooltip.size() + 1) * 5;
		if(k == 2)
			RenderHelper.renderTooltip(mouseX, mouseY + tooltipY, tooltip);
	}

}
