package vazkii.botania.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.relic.ItemLokiRing;

public class PacketLokiHudNotificationAck implements IMessage, IMessageHandler<PacketLokiHudNotificationAck, IMessage> {


    public ItemLokiRing.HUD_MESSAGE hudMessage;

    public PacketLokiHudNotificationAck(){

    }

    public PacketLokiHudNotificationAck(ItemLokiRing.HUD_MESSAGE aHudMessage){
        hudMessage = aHudMessage;
    }
    @Override
    public void fromBytes(ByteBuf byteBuf) {
        int ordinal = byteBuf.readInt();
        if (ordinal >= 0 && ordinal < ItemLokiRing.HUD_MESSAGE.values().length) {
            hudMessage = ItemLokiRing.HUD_MESSAGE.values()[ordinal];
        } else {
            hudMessage = null;
        }
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeInt(hudMessage.ordinal());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(PacketLokiHudNotificationAck message, MessageContext ctx) {
        Minecraft mc = Minecraft.getMinecraft();
        final ItemStack aRing = ItemLokiRing.getLokiRing(mc.thePlayer) ;
        if (aRing != null && message.hudMessage != null) {
            ItemLokiRing.renderHUDNotification(message.hudMessage);
        }
        return null;
    }
}

