package vazkii.botania.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.relic.ItemLokiRing;

public class PacketLokiDeleteSchematic implements IMessage, IMessageHandler<PacketLokiDeleteSchematic, IMessage> {

    private String schematicName;

    public PacketLokiDeleteSchematic() {

    }

    public PacketLokiDeleteSchematic(String schematicName) {
        this.schematicName = schematicName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int schematicNameLength = buf.readInt();
        this.schematicName = new String(buf.readBytes(schematicNameLength).array());
        if(schematicName.equals("null")) {
            schematicName = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        schematicName = schematicName == null ? "null" : schematicName;
        buf.writeInt(schematicName.length());
        buf.writeBytes(schematicName.getBytes());
    }

    @Override
    public IMessage onMessage(PacketLokiDeleteSchematic message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        ItemStack lokiStack = ItemLokiRing.getLokiRing(player);
        if(lokiStack != null && lokiStack.getItem() instanceof ItemLokiRing) {
            ItemLokiRing.deleteSchematic(lokiStack, message.schematicName);
            ItemLokiRing.syncLokiRing(player);
        }
        return null;
    }
}
