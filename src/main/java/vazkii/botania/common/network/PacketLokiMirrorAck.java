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

public class PacketLokiMirrorAck implements IMessage, IMessageHandler<PacketLokiMirrorAck, IMessage> {

    public boolean state;

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        state = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeBoolean(state);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(PacketLokiMirrorAck message, MessageContext ctx) {
        Minecraft mc = Minecraft.getMinecraft();
        final ItemStack aRing = ItemLokiRing.getLokiRing(mc.thePlayer) ;
        if (aRing != null) {
            ItemLokiRing.renderHUDNotification(ItemLokiRing.HUD_MESSAGE.MIRROR);
        }

        return null;
    }
}
