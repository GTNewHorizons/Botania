package vazkii.botania.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.relic.ItemLokiRing;

public class PacketLokiSaveSchematic implements IMessage, IMessageHandler<PacketLokiSaveSchematic, IMessage> {
    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public IMessage onMessage(PacketLokiSaveSchematic message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final ItemStack aRing = ItemLokiRing.getLokiRing(player);
        if (aRing != null) {
            ItemLokiRing.saveCurrentSelectionAsSchematic(aRing);
            ItemLokiRing.syncLokiRing(player);
            PacketHandler.INSTANCE.sendTo(new PacketLokiHudNotificationAck(ItemLokiRing.HUD_MESSAGE.SCHEMATIC_SAVED), player);
        }
        return null;
    }
}
