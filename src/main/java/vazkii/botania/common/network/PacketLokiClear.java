package vazkii.botania.common.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.relic.ItemLokiRing;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketLokiClear implements IMessage, IMessageHandler<PacketLokiClear, IMessage> {
    @Override
    public void fromBytes(ByteBuf byteBuf) {
        // not needed
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        // not needed
    }

    @Override
    public IMessage onMessage(PacketLokiClear message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final ItemStack aRing = ItemLokiRing.getLokiRing(player);
        if (aRing != null) {
            ItemLokiRing.clearCursors(aRing);
            ItemLokiRing.clearMasterCursor(aRing);
            ItemLokiRing.syncLokiRing(player);
            PacketHandler.INSTANCE.sendTo(new PacketLokiHudNotificationAck(ItemLokiRing.HUD_MESSAGE.CLEAR), player);
        }
        return null;
    }
}
