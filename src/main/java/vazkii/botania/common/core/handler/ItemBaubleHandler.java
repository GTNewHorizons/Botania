package vazkii.botania.common.core.handler;

import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemBaubleHandler {

    @SubscribeEvent
    public void playerConnectServer(PlayerEvent.PlayerLoggedInEvent event) {
        ItemBauble.playerLoggedIn(event.player);
    }

    static boolean newServer;

    @SubscribeEvent
    public void playerJoinClient(EntityJoinWorldEvent event) {
        if (newServer && event.world.isRemote) {
            newServer = false;
            ItemBauble.playerLoggedIn(Minecraft.getMinecraft().thePlayer);
        }
    }

    @SubscribeEvent
    public void playerConnectClient(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        // Player is not set here yet
        newServer = true;
    }
}
