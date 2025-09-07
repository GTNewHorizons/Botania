package vazkii.botania.common.item.block;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import vazkii.botania.common.core.handler.ConfigHandler;

import java.util.List;

public class ItemCubeMod extends ItemBlockMod{
    public ItemCubeMod(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> infoList, boolean advancedTooltips) {
        if (ConfigHandler.noMobSpawnOnBlocks)
            infoList.add(StatCollector.translateToLocal("nomobspawnsonthisblock.tip"));
    }

}
