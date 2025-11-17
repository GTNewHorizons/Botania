/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Aug 7, 2014, 5:53:16 PM (GMT)]
 */
package vazkii.botania.client.gui.lexicon.button;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiButtonLexicon extends GuiButton {

	public GuiButtonLexicon(int id, int xPos, int yPos, int width, int height, String text) {
		super(id, xPos, yPos, width, height, text);
	}

	@Override
	public void func_146113_a(SoundHandler soundHandlerIn)  {
		soundHandlerIn.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("botania:lexiconPage"), 1.0F));
	}

}
