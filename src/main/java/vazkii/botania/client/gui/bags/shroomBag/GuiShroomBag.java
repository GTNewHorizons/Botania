package vazkii.botania.client.gui.bags.shroomBag;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.client.gui.bags.GuiMagicPlantBag;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.block.ModBlocks;

public class GuiShroomBag extends GuiMagicPlantBag {

    private static final ResourceLocation texture = new ResourceLocation(LibResources.GUI_SHROOM_BAG);

    public GuiShroomBag(EntityPlayer player) {
        super(new ContainerShroomBag(player), texture);
    }

    @Override
    protected Block itemTypeAllowed() {
        return ModBlocks.mushroom;
    }

    @Override
    protected String getLocalizedNameKey() {
        return "item.botania:shroomBag.name";
    }
}
