package vazkii.botania.client.integration.nei.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.mana.TilePool;
import codechicken.nei.PositionedStack;

public class RecipeHandlerRunicAltar extends RecipeHandlerPetalApothecary {

    private static final ItemStack altarStack = new ItemStack(ModBlocks.runeAltar);

    public class CachedRunicAltarRecipe extends CachedPetalApothecaryRecipe {

        public int manaUsage;
		private static final PositionedStack livingrock = new PositionedStack(new ItemStack(ModBlocks.livingrock), 73, 39);

        public CachedRunicAltarRecipe(RecipeRuneAltar recipe) {
            super(recipe, false);
            inputs.add(livingrock);
            manaUsage = recipe.getManaUsage();
            renderItem = true;
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("botania.nei.runicAltar");
    }

    @Override
    public String getOverlayIdentifier() {
        return "botania.runicAltar";
    }

    @Override
    public void drawBackground(int recipe) {
        super.drawBackground(recipe);
        HUDHandler.renderManaBar(32, 113, 0x0000FF, 0.75F, ((CachedRunicAltarRecipe) arecipes.get(recipe)).manaUsage, TilePool.MAX_MANA / 10);
    }

    @Override
    public List<? extends RecipePetals> getRecipes() {
        return BotaniaAPI.runeAltarRecipes;
    }

    @Override
    public CachedPetalApothecaryRecipe getCachedRecipe(RecipePetals recipe) {
        return new CachedRunicAltarRecipe((RecipeRuneAltar) recipe);
    }

	@Override
    protected ItemStack getRenderItem() {
        return altarStack;
    }

}
