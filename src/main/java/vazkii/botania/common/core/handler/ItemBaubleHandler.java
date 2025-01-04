package vazkii.botania.common.core.handler;

import net.minecraft.client.Minecraft;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemBaubleHandler {

    @SubscribeEvent
    public void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if(Minecraft.getMinecraft().thePlayer != null) {
            ItemBauble.removePlayer(Minecraft.getMinecraft().thePlayer.getUniqueID());
        }
    }

    @SubscribeEvent
    public void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        ItemBauble.removePlayer(event.player.getUniqueID());
    }
}
