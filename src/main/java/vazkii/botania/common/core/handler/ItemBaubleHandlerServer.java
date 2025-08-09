package vazkii.botania.common.core.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import vazkii.botania.common.item.equipment.bauble.ItemBauble;

public class ItemBaubleHandlerServer {

    @SubscribeEvent
    public void playerConnectServer(PlayerEvent.PlayerLoggedInEvent event) {
        ItemBauble.playerLoggedIn(event.player);
    }
}
