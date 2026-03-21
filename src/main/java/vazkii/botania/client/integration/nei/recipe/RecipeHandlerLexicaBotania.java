/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [12/12/2015, 23:25:47 (GMT)]
 */
package vazkii.botania.client.integration.nei.recipe;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.KnowledgeType;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.lexicon.LexiconRecipeMappings;
import vazkii.botania.client.integration.nei.NEIHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lexicon.page.PageText;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class RecipeHandlerLexicaBotania extends TemplateRecipeHandler {
    private static final ItemStack lexicaStack = new ItemStack(ModItems.lexicon);

    public class CachedLexicaBotaniaRecipe extends CachedRecipe {

        public LexiconEntry entry;
        public PositionedStack item;

        public CachedLexicaBotaniaRecipe(ItemStack stack, LexiconEntry entry) {
            item = new PositionedStack(stack, 91, 5);
            this.entry = entry;
        }

        @Override
        public PositionedStack getResult() {
            return item;
        }

        @Override
        public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
            return ingredient.getItem() == ModItems.lexicon;
        }

    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("botania.nei.lexica");
    }

    @Override
    public String getOverlayIdentifier() {
        return "botania.lexica";
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BLANK;
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(50, 4, 18, 18), getOverlayIdentifier()));
    }

    @Override
    public void drawBackground(int recipe) {
        super.drawBackground(recipe);

        CachedLexicaBotaniaRecipe recipeObj = ((CachedLexicaBotaniaRecipe) arecipes.get(recipe));

        NEIHelper.renderItem.renderItemIntoGUI(NEIHelper.font, NEIHelper.textureManager, lexicaStack, 51, 5);

        GuiDraw.drawStringC(
                EnumChatFormatting.UNDERLINE + StatCollector.translateToLocal(recipeObj.entry.getUnlocalizedName()),
                82,
                30,
                0x404040,
                false);

        KnowledgeType type = recipeObj.entry.getKnowledgeType();
        GuiDraw.drawStringC(
                type.color + StatCollector.translateToLocal(type.getUnlocalizedName()).replaceAll("&.", ""),
                82,
                42,
                0x404040,
                false);

        PageText.renderText(
                5,
                42,
                160,
                200,
                "\"" + StatCollector.translateToLocal(recipeObj.entry.getTagline()) + "\"");

        String key = LexiconRecipeMappings.stackToString(recipeObj.item.item);
        String quickInfo = "botania.nei.quickInfo:" + key;
        String quickInfoLocal = StatCollector.translateToLocal(quickInfo);

        String s;
        if (GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown() && Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
            s = "name: " + key;
        else if (quickInfo.equals(quickInfoLocal))
            s = StatCollector.translateToLocal("botania.nei.lexicaNoInfo");
        else {
            GuiDraw.drawStringC(
                    StatCollector.translateToLocal("botania.nei.lexicaSeparator"),
                    82,
                    80,
                    0x404040,
                    false);
            s = quickInfoLocal;
        }

        PageText.renderText(5, 80, 160, 200, s);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            for (LexiconEntry entry : BotaniaAPI.getAllEntries()) {
                List<ItemStack> stacks = entry.getDisplayedRecipes();
                for (ItemStack stack : stacks)
                    arecipes.add(new CachedLexicaBotaniaRecipe(stack, entry));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (LexiconEntry entry : BotaniaAPI.getAllEntries()) {
            List<ItemStack> stacks = entry.getDisplayedRecipes();
            for (ItemStack stack : stacks) {
                String key1 = LexiconRecipeMappings.stackToString(stack);
                String key2 = LexiconRecipeMappings.stackToString(result);
                if (key1.equals(key2)) {
                    arecipes.add(new CachedLexicaBotaniaRecipe(stack, entry));
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(String outputId, Object... ingredients) {
        loadCraftingRecipes(outputId, ingredients);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        loadCraftingRecipes(ingredient);
    }

}
