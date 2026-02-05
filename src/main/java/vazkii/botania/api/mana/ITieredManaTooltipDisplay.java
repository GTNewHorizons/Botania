package vazkii.botania.api.mana;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface ITieredManaTooltipDisplay extends IManaTooltipDisplay {
    /**
     * Returns the text to use for the top-left side of the mana bar
     * @param stack the item being rendered
     */
    @SideOnly(Side.CLIENT)
    @Nonnull
    String getLeftManaLabel(@Nonnull ItemStack stack);

    /**
     * Returns the text to use for the top-right side of the mana bar
     * @param stack the item being rendered
     */
    @SideOnly(Side.CLIENT)
    @Nonnull
    String getRightManaLabel(@Nonnull ItemStack stack);

    /**
     * Should the mana bar use the rainbow effect
     * @param stack the item being rendered
     */
    @SideOnly(Side.CLIENT)
    boolean isRainbowEffect(@Nonnull ItemStack stack);

    /**
     * Indicate the mana level boundaries for this item.
     */
    @Nonnull
    int[] getManaTiers(@Nonnull ItemStack stack);
}
