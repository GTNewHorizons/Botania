/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jun 29, 2015, 4:24:07 PM (GMT)]
 */
package vazkii.botania.client.gui.lexicon;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import vazkii.botania.client.challenge.Challenge;
import vazkii.botania.client.challenge.EnumChallengeLevel;
import vazkii.botania.client.challenge.ModChallenges;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.gui.lexicon.button.GuiButtonBack;
import vazkii.botania.client.gui.lexicon.button.GuiButtonChallengeIcon;

public class GuiLexiconChallengesList extends GuiLexicon implements IParented {

	GuiLexicon parent;
	GuiButton backButton;

	public GuiLexiconChallengesList() {
		parent = new GuiLexicon();
		title = StatCollector.translateToLocal("botaniamisc.challenges");
	}

	@Override
	public void onInitGui() {
		super.onInitGui();
		title = StatCollector.translateToLocal("botaniamisc.challenges");

		buttonList.add(backButton = new GuiButtonBack(12, left + guiWidth / 2 - 8, top + guiHeight + 2));

		int perline = 6;
		int i = 13;
		int y = top + 20;
		for(EnumChallengeLevel level : EnumChallengeLevel.class.getEnumConstants()) {
			int j = 0;
			for(Challenge c : ModChallenges.challenges.get(level)) {
				buttonList.add(new GuiButtonChallengeIcon(i, left + 20 + j % perline * 18, y + j / perline * 17, c));
				i++;
				j++;
			}
			y += 44;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		boolean unicode = fontRendererObj.getUnicodeFlag();
		fontRendererObj.setUnicodeFlag(true);
		for(EnumChallengeLevel level : EnumChallengeLevel.class.getEnumConstants()) {
			List<Challenge> list = ModChallenges.challenges.get(level);
			int complete = 0;
			for(Challenge c : list)
				if(c.complete)
					complete++;

			fontRendererObj.drawString(EnumChatFormatting.BOLD + StatCollector.translateToLocal(level.getName()) + EnumChatFormatting.RESET + " (" + complete + "/" + list.size() + ")", left + 20, top + 11 + level.ordinal() * 44, 0);
		}
		fontRendererObj.setUnicodeFlag(unicode);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if(keyCode == 14 && !notesEnabled) // Backspace
			back();
		else if(keyCode == 199) { // Home
			mc.displayGuiScreen(new GuiLexicon());
			ClientTickHandler.notifyPageChange();
		}

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if(mouseButton == 1)
			back();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id >= BOOKMARK_START)
			super.actionPerformed(button);
		else if(button.id == 12) {
			mc.displayGuiScreen(parent);
			ClientTickHandler.notifyPageChange();
		} else if(button instanceof GuiButtonChallengeIcon) {
			GuiButtonChallengeIcon cbutton = (GuiButtonChallengeIcon) button;
			mc.displayGuiScreen(new GuiLexiconChallenge(this, cbutton.challenge));
		} else if(button.id == NOTES_BUTTON_ID)
			notesEnabled = !notesEnabled;
	}

	void back() {
		if(backButton.enabled) {
			actionPerformed(backButton);
			backButton.func_146113_a(mc.getSoundHandler());
		}
	}

	@Override
	public void setParent(GuiLexicon gui) {
		parent = gui;
	}

	@Override
	boolean isMainPage() {
		return false;
	}

	@Override
	String getTitle() {
		return title;
	}

	@Override
	boolean isChallenge() {
		return true;
	}

	@Override
	boolean isCategoryIndex() {
		return false;
	}

	@Override
	public GuiLexicon copy() {
		return new GuiLexiconChallengesList();
	}

	@Override
	public String getNotesKey() {
		return "challengelist";
	}

}
