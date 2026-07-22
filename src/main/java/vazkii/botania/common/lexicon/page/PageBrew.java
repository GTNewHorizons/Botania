/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Nov 2, 2014, 5:17:46 PM (GMT)]
 */
package vazkii.botania.common.lexicon.page;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.internal.IGuiLexiconEntry;
import vazkii.botania.api.lexicon.ILexicon;
import vazkii.botania.api.lexicon.ITwoNamedPage;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.api.recipe.RecipeBrew;
import vazkii.botania.client.core.helper.BrewHelper;
import vazkii.botania.common.item.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PageBrew extends PageRecipe implements ITwoNamedPage {

	RecipeBrew recipe;
	String text;

	public PageBrew(RecipeBrew recipe, String unlocalizedName, String bottomText) {
		super(bottomText);
		this.recipe = recipe;
		text = unlocalizedName;
	}

	@Override
	public void renderRecipe(IGuiLexiconEntry gui, int mx, int my) {
		int width = gui.getWidth() - 30;
		int height = gui.getHeight();
		int x = gui.getLeft() + 16;
		int y = gui.getTop() + 12;

		Brew brew = recipe.getBrew();
		FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
		boolean unicode = renderer.getUnicodeFlag();
		renderer.setUnicodeFlag(true);
		String s = EnumChatFormatting.BOLD + StatCollector.translateToLocalFormatted("botaniamisc.brewOf", StatCollector.translateToLocal(brew.getUnlocalizedName()));
		renderer.drawString(s, gui.getLeft() + gui.getWidth() / 2 - renderer.getStringWidth(s) / 2, y, 0x222222);
		renderer.setUnicodeFlag(unicode);
		PageText.renderText(x, y + 22, width, height, text);

		ItemStack book = Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem();
		renderItemRow(gui, getOutputs(book), y + 12);

        y = gui.getTop() + gui.getHeight() - 54;
		List<Object> inputs = new ArrayList<>(recipe.getInputs());

        renderItemRow(gui, inputs, y);

        super.renderRecipe(gui, mx, my);
	}

    @SideOnly(Side.CLIENT)
    private void renderItemRow(IGuiLexiconEntry gui, List<?> inputs, int y) {
        int spacing = Math.min(gui.getWidth() / inputs.size(), 18);
        int x = gui.getLeft() + (gui.getWidth() - spacing * inputs.size()) / 2;

        for (Object input : inputs) {
            ItemStack stack;
            if (input instanceof String) {
                stack = OreDictionary.getOres((String) input).get(0);

                if (stack.getItemDamage() == Short.MAX_VALUE || stack.getItemDamage() == -1) {
                    ItemStack newStack = new ItemStack(stack.getItem(), stack.stackSize, 0);
                    newStack.stackTagCompound = stack.stackTagCompound;
                    stack = newStack;
                }
            } else {
                stack = (ItemStack) input;
            }

            if (stack != null && stack.getItem() != null) {
                renderItem(gui, x, y, stack, false);
            }

            x += spacing;
        }
    }

	@Override
	public List<ItemStack> getDisplayedRecipes() {
		ArrayList<ItemStack> list = new ArrayList<>();
		if (recipe != null) {
			list.add(recipe.getOutput(new ItemStack(ModItems.vial)));
		}
		return list;
	}

    private List<ItemStack> getOutputs(ItemStack lexicon) {
        ArrayList<ItemStack> list = new ArrayList<>();

        for (ItemStack empty : BrewHelper.getBrewContainers()) {
            if (lexicon != null && lexicon.getItem() instanceof ILexicon lexItem) {
                // Check if the lexicon has this item unlocked.
                LexiconRecipeMappings.EntryData entry = LexiconRecipeMappings.getDataForStack(empty);
                if (entry != null && (!entry.entry.isVisible() || !lexItem.isKnowledgeUnlocked(lexicon, entry.entry.getKnowledgeType()))) {
                    continue;
                }
            }

            ItemStack filled = recipe.getOutput(empty);
            if (filled != null) {
                list.add(filled);
            }
        }

        return list;
    }

	@Override
	public void setSecondUnlocalizedName(String name) {
		text = name;
	}

	@Override
	public String getSecondUnlocalizedName() {
		return text;
	}

}
