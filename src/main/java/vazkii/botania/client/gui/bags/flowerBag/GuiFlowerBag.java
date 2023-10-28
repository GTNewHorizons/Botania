package vazkii.botania.client.gui.bags.flowerBag;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.client.gui.bags.GuiMagicPlantBag;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.block.ModBlocks;

public class GuiFlowerBag extends GuiMagicPlantBag {

    private static final ResourceLocation texture = new ResourceLocation(LibResources.GUI_FLOWER_BAG);

    public GuiFlowerBag(EntityPlayer player) {
        super(new ContainerFlowerBag(player), texture);
    }

    @Override
    protected Block itemTypeAllowed() {
        return ModBlocks.flower;
    }

    @Override
    protected String getLocalizedNameKey() {
        return "item.botania:flowerBag.name";
    }
}
