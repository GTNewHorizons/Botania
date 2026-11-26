/**
 * This class was created by <SoundLogic>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [June 8, 2015, 12:55:20 AM (GMT)]
 */
package vazkii.botania.client.core.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.lib.LibObfuscation;
import vazkii.botania.utils.ReflectionUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

@SideOnly(Side.CLIENT)
public final class CorporeaAutoCompleteHandler {

	private static final Field COMPLETE_FLAG = ReflectionUtils.findField(GuiChat.class, LibObfuscation.COMPLETE_FLAG);
	private static final Field INPUT_FIELD = ReflectionUtils.findField(GuiChat.class, LibObfuscation.INPUT_FIELD);

	private final TreeSet<String> itemNames = new TreeSet<>(String::compareToIgnoreCase);
	private boolean hasItemList = false;

	private boolean isAutoCompleted = false;
	private String originalString = "";
	private List<CompletionData> completions = new ArrayList<>();
	private int position;
	private boolean tabLastTick = false;

	private void generateItemList() {
		hasItemList = true;
		//noinspection unchecked
		final Iterator<Item> iterator = Item.itemRegistry.iterator();
		final ArrayList<ItemStack> curList = new ArrayList<>();

		while(iterator.hasNext()) {
			Item item = iterator.next();

			if(item != null && item.getCreativeTab() != null) {
				curList.clear();
				try {
					item.getSubItems(item, (CreativeTabs) null, curList);
					for(ItemStack stack : curList)
						itemNames.add(EnumChatFormatting.getTextWithoutFormattingCodes(stack.getDisplayName().trim()));
				}
				catch (Exception _) {}
			}
		}
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase != Phase.END)
			return;
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(!(screen instanceof GuiChat chat)) {
			isAutoCompleted = false;
			return;
		}
		if(isAutoCompleted) {
			boolean valid = ReflectionUtils.getBoolean(COMPLETE_FLAG, chat);
			if(!valid)
				isAutoCompleted = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			if(tabLastTick)
				return;
			tabLastTick = true;
		} else {
			tabLastTick = false;
			return;
		}

		if(!CorporeaHelper.shouldAutoComplete())
			return;

		GuiTextField inputField = (GuiTextField) ReflectionUtils.getObject(INPUT_FIELD, chat);
		if (inputField != null) {
			if (!isAutoCompleted)
				buildAutoCompletes(inputField, chat);
			if (isAutoCompleted && !completions.isEmpty())
				advanceAutoComplete(inputField, chat);
		}
	}

	private void advanceAutoComplete(GuiTextField inputField, GuiChat chat) {
		position++;
		if(position >= completions.size())
			position -= completions.size();
		CompletionData data = completions.get(position);
		String str = originalString.substring(0, originalString.length() - data.prefixLength) + data.string;
		inputField.setText(str);
	}

	private void buildAutoCompletes(GuiTextField inputField, GuiChat chat) {
		String leftOfCursor;
		if(inputField.getCursorPosition() == 0)
			leftOfCursor = "";
		else
			leftOfCursor = inputField.getText().substring(0, inputField.getCursorPosition());
		if(leftOfCursor.isEmpty() || leftOfCursor.charAt(0) == '/')
			return;
		completions = getNames(leftOfCursor);
		if(completions.isEmpty())
			return;
		position = -1;
		ReflectionUtils.setBoolean(COMPLETE_FLAG, chat, true);
		StringBuilder stringbuilder = new StringBuilder();
		CompletionData data;
		for(Iterator<CompletionData> iterator = completions.iterator(); iterator.hasNext(); stringbuilder.append(data.string)) {
			data = iterator.next();
			if(!stringbuilder.isEmpty())
				stringbuilder.append(", ");
		}

		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
		isAutoCompleted = true;
		originalString = inputField.getText();
	}

	private List<CompletionData> getNames(String prefix) {
		String s = prefix.trim();
		if(s.isEmpty())
			return Collections.emptyList();
		if (!hasItemList) generateItemList();
		TreeSet<CompletionData> result = new TreeSet<>();
		String[] words = s.split(" ");
		int i = words.length - 1;
		StringBuilder curPrefix = new StringBuilder(words[i]);
		while(i >= 0) {
			result.addAll(getNamesStartingWith(curPrefix.toString().toLowerCase()));
			i--;
			if(i >= 0)
				curPrefix.insert(0, words[i] + " ");
		}
		return new ArrayList<>(result);
	}

	private List<CompletionData> getNamesStartingWith(String prefix) {
		ArrayList<CompletionData> result = new ArrayList<>();
		int length = prefix.length();
		SortedSet<String> after = itemNames.tailSet(prefix);
		for(String str : after) {
			if(str.toLowerCase().startsWith(prefix))
				result.add(new CompletionData(str, length));
			else return result;
		}
		return result;
	}

	private static class CompletionData implements Comparable<CompletionData> {

		private final String string;
		private final int prefixLength;

		public CompletionData(String string, int prefixLength) {
			this.string = string;
			this.prefixLength = prefixLength;
		}

		@Override
		public int compareTo(CompletionData arg0) {
			return string.compareTo(arg0.string);
		}
	}

}
