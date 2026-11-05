package vazkii.botania.api.brew;

import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;

/**
 * Utility methods for checking the properties of {@link ItemStack}s related to Brews.
 */
public final class BrewUtilities {

    /**
     * Check if an {@link ItemStack} represents a filled Brew.
     *
     * @param stack The stack to check
     * @return Whether this item contains a Brew.
     */
    public static boolean isFilledBrew(ItemStack stack) {
        if (!(stack.getItem() instanceof IBrewItem brewItem)) return false;
        final Brew brew = brewItem.getBrew(stack);
        return brew != null && brew != BotaniaAPI.fallbackBrew;
    }

    /**
     * Check if an {@link ItemStack} is an empty Brew Container - an item that can go on the middle pedestal of the Botanical Brewery.
     *
     * @param stack The stack to check
     * @return Whether this item is a Brew Container that does not contain a Brew.
     */
    public static boolean isEmptyBrewContainer(ItemStack stack) {
        return stack.getItem() instanceof IBrewContainer && !isFilledBrew(stack);
    }
}
