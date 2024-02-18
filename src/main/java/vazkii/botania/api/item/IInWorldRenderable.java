package vazkii.botania.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * An item that implements this will allow some custom render. This will be called while the item is in the inventory,
 * armor slot or bauble slots.
 */
public interface IInWorldRenderable
{
    public void renderInWorld(EntityPlayer player, ItemStack stack);
}
