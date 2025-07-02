package vazkii.botania.common.core.helper;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;


public class ItemHelper {
    public static boolean canPlaceBlock(World world, int x, int y, int z, int xAt, int yAt, int zAt, EntityPlayer player){
        Block block = world.getBlock(x,y,z);
        Block blockAt= world.getBlock(xAt,zAt,yAt);
        int meta = world.getBlockMetadata(x,y,z);
        BlockSnapshot snapshot = new BlockSnapshot(world,x,y,z,block,meta);
        BlockEvent.PlaceEvent event = new BlockEvent.PlaceEvent(snapshot,blockAt,player);
        MinecraftForge.EVENT_BUS.post(event);

        return !event.isCanceled();
    }

    public static boolean canBreakBlock(World world,int x, int y, int z,EntityPlayer player){
        Block block = world.getBlock(x,y,z);
        int meta = world.getBlockMetadata(x,y,z);
        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(x, y, z, world, block, meta, player);
        MinecraftForge.EVENT_BUS.post(event);
        boolean canMineBlock = world.canMineBlock(player,x,y,z);

        return !event.isCanceled() && canMineBlock;
    }
}
