package vazkii.botania.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import vazkii.botania.common.core.handler.ConfigHandler;

import java.util.List;

public class ItemCubeWithMetadataAndName extends ItemBlockWithMetadataAndName  {

    public ItemCubeWithMetadataAndName(Block par2Block) {
        super(par2Block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {
        if (ConfigHandler.noMobSpawnOnBlocks)
            infoList.add(StatCollector.translateToLocal("nomobspawnsonthisblock.tip"));
    }
}
