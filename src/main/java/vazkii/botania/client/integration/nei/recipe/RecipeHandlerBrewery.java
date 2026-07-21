package vazkii.botania.client.integration.nei.recipe;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.brew.BrewUtilities;
import vazkii.botania.api.brew.IBrewContainer;
import vazkii.botania.api.brew.IBrewItem;
import vazkii.botania.api.recipe.RecipeBrew;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.integration.nei.NEIUtilities;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ModItems;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class RecipeHandlerBrewery extends TemplateRecipeHandler {

    public static final String OVERLAY = "botania.brewery";

    public class CachedBreweryRecipe extends CachedRecipe {

        public List<PositionedStack> inputs = new ArrayList<>();
        public PositionedStack output;
        public int mana;

        public CachedBreweryRecipe(RecipeBrew recipe, ItemStack vial) {
            if (recipe == null) return;

            setIngredients(recipe.getInputs());
            ItemStack toVial;
            if (vial == null) {
                toVial = new ItemStack(ModItems.vial);
            } else {
                toVial = vial.copy();
                toVial.stackSize = 1;
            }
            inputs.add(new PositionedStack(toVial, 39, 42));

            output = new PositionedStack(recipe.getOutput(toVial), 87, 42);
            mana = ((IBrewContainer) toVial.getItem()).getManaCost(recipe.getBrew(), toVial);
        }

        public CachedBreweryRecipe(RecipeBrew recipe) {
            this(recipe, null);
        }

        public void setIngredients(List<Object> inputs) {
            int left = 96 - inputs.size() * 18 / 2;

            for (int i = 0; i < inputs.size(); i++) {
                Object o = inputs.get(i);
                if (o instanceof String oreName) {
                    o = OreDictionary.getOres(oreName);
                }
                this.inputs.add(new PositionedStack(o, left + i * 18, 6));
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return inputs;
        }

        @Override
        public PositionedStack getResult() {
            return output;
        }

    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("botania.nei.brewery");
    }

    @Override
    public String getOverlayIdentifier() {
        return OVERLAY;
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BREWERY;
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(87, 25, 15, 14), getOverlayIdentifier()));
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Arrows
        GuiDraw.changeTexture(getGuiTexture());
        GuiDraw.drawTexturedModalRect(0, 0, 0, 0, 166, 65);

        // Mana Bar
        HUDHandler.renderManaBar(32, 67, 0x0000FF, 0.75F, ((CachedBreweryRecipe) arecipes.get(recipe)).mana, TilePool.MAX_MANA / 10);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            for (RecipeBrew recipe : BotaniaAPI.brewRecipes) {
                arecipes.add(new CachedBreweryRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (!(result.getItem() instanceof IBrewItem brew)) return;
        final Brew targetBrew = brew.getBrew(result);
        if (targetBrew == null || targetBrew == BotaniaAPI.fallbackBrew) return;

        // Find the container(s) that can be filled to produce this result
        for (RecipeBrew recipe : BotaniaAPI.brewRecipes) {
            if (recipe == null || targetBrew != recipe.getBrew()) continue;

            for (ItemStack emptyContainer : NEIUtilities.getBrewContainers()) {
                final ItemStack filledContainer = recipe.getOutput(emptyContainer);
                if (result.isItemEqual(filledContainer) && Objects.equals(result.stackTagCompound, filledContainer.stackTagCompound)) {
                    arecipes.add(new CachedBreweryRecipe(recipe, emptyContainer));
                }
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (BrewUtilities.isFilledBrew(ingredient)) {
            // Filled brews cannot be inserted into a Botanical Brewery.
            return;
        }

        if (ingredient.getItem() instanceof IBrewContainer) {
            // Show all possible brews that can fill this empty container
            for (RecipeBrew recipe : BotaniaAPI.brewRecipes) {
                if (recipe != null && recipe.getOutput(ingredient) != null) {
                    arecipes.add(new CachedBreweryRecipe(recipe, ingredient));
                }
            }
        } else {
            // Show all brew recipes that can use this ingredient
            for (RecipeBrew recipe : BotaniaAPI.brewRecipes) {
                if (recipe == null) continue;
                CachedBreweryRecipe crecipe = new CachedBreweryRecipe(recipe);
                if (ItemNBTHelper.cachedRecipeContainsWithNBT(crecipe.inputs, ingredient)) {
                    arecipes.add(crecipe);
                }
            }
        }
    }

    @Override
    public int getRecipeHeight(int recipe) {
        return 80;
    }
}
