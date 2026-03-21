package vazkii.botania.client.integration.nei.recipe;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.client.integration.nei.NEIHelper;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.block.ModBlocks;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import vazkii.botania.common.core.helper.ItemNBTHelper;

public class RecipeHandlerPetalApothecary extends TemplateRecipeHandler {
    private static final ItemStack altarStack = new ItemStack(ModBlocks.altar);

    public class CachedPetalApothecaryRecipe extends CachedRecipe {
        public List<PositionedStack> inputs = new ArrayList<>();
        public PositionedStack output;
        public boolean renderItem;

        public CachedPetalApothecaryRecipe(RecipePetals recipe, boolean addCenterItem) {
            setIngredients(recipe.getInputs());
            output = new PositionedStack(recipe.getOutput(), 111, 21);
            renderItem = addCenterItem;
            if (addCenterItem) {
                ItemStack seedStack = new ItemStack(Items.wheat_seeds);
                seedStack.setStackDisplayName(StatCollector.translateToLocal("botania.nei.anySeed"));
                inputs.add(new PositionedStack(seedStack, 73, 39));
            }
        }

        public CachedPetalApothecaryRecipe(RecipePetals recipe) {
            this(recipe, true);
        }

        public void setIngredients(List<Object> inputs) {
            float degreePerInput = 360F / inputs.size();
            float currentDegree = -90F;

            for (Object o : inputs) {
                int posX = (int) Math.round(73 + Math.cos(currentDegree * Math.PI / 180D) * 32);
                int posY = (int) Math.round(55 + Math.sin(currentDegree * Math.PI / 180D) * 32);

                if (o instanceof String) {
                    this.inputs.add(new PositionedStack(OreDictionary.getOres((String) o), posX, posY));
                } else {
                    this.inputs.add(new PositionedStack(o, posX, posY));
                }
                currentDegree += degreePerInput;
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
        return StatCollector.translateToLocal("botania.nei.petalApothecary");
    }

    @Override
    public String getOverlayIdentifier() {
        return getRecipeID();
    }

    // Used by the Alfheim addon
    @Deprecated
    public String getRecipeID() {
        return "botania.petalApothecary";
    }

    @Override
    public String getGuiTexture() {
        return LibResources.GUI_NEI_BLANK;
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(73, 56, 16, 16), getOverlayIdentifier()));
    }

    @Override
    public void drawBackground(int recipe) {
        super.drawBackground(recipe);
        // Background circle graphic
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        GuiDraw.changeTexture(LibResources.GUI_PETAL_OVERLAY);
        GuiDraw.drawTexturedModalRect(45, 10, 38, 7, 92, 92);
        // Item
        if (!((CachedPetalApothecaryRecipe) arecipes.get(recipe)).renderItem) return;
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        NEIHelper.renderItemIntoGUI(getRenderItem(), 73, 55);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderHelper.disableStandardItemLighting();
    }

    protected ItemStack getRenderItem() {
        return altarStack;
    }

    public List<? extends RecipePetals> getRecipes() {
        return BotaniaAPI.petalRecipes;
    }

    public CachedPetalApothecaryRecipe getCachedRecipe(RecipePetals recipe) {
        return new CachedPetalApothecaryRecipe(recipe, true);
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(getOverlayIdentifier())) {
            for (RecipePetals recipe : getRecipes())
                if (recipe.getOutput().getItem() != Items.skull)
                    arecipes.add(getCachedRecipe(recipe));
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (RecipePetals recipe : getRecipes()) {
            if (recipe == null) continue;

            if (recipe.getOutput().stackTagCompound != null && ItemNBTHelper.areStacksSameTypeWithNBT(recipe.getOutput(), result) || recipe.getOutput().stackTagCompound == null && NEIServerUtils.areStacksSameTypeCrafting(recipe.getOutput(), result) && recipe.getOutput().getItem() != Items.skull) {
                arecipes.add(getCachedRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (RecipePetals recipe : getRecipes()) {
            if (recipe == null) continue;

            CachedPetalApothecaryRecipe crecipe = getCachedRecipe(recipe);
            if (ItemNBTHelper.cachedRecipeContainsWithNBT(crecipe.inputs, ingredient) && recipe.getOutput().getItem() != Items.skull) {
                arecipes.add(crecipe);
            }
        }
    }

}
