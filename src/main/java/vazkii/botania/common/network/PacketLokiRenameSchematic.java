package vazkii.botania.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import vazkii.botania.common.item.relic.ItemLokiRing;

public class PacketLokiRenameSchematic implements IMessage, IMessageHandler<PacketLokiRenameSchematic, IMessage> {

    private String schematicToBeRenamed;
    private String newName;

    public PacketLokiRenameSchematic() {
    }

    public PacketLokiRenameSchematic(String schematicToBeRenamed, String newValue) {
        this.schematicToBeRenamed = schematicToBeRenamed;
        this.newName = newValue;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int schematicToBeRenamedLength = buf.readInt();
        this.schematicToBeRenamed = new String(buf.readBytes(schematicToBeRenamedLength).array());
        if(schematicToBeRenamed.equals("null")) {
            this.schematicToBeRenamed = null;
        }
        int newNameLength = buf.readInt();
        this.newName = new String(buf.readBytes(newNameLength).array());
        if(newName.equals("null")) {
            this.newName = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        schematicToBeRenamed = schematicToBeRenamed == null ? "null" : schematicToBeRenamed;
        buf.writeInt(schematicToBeRenamed.length());
        buf.writeBytes(schematicToBeRenamed.getBytes());
        newName = newName == null ? "null" : newName;
        buf.writeInt(newName.length());
        buf.writeBytes(newName.getBytes());
    }


    @Override
    public IMessage onMessage(PacketLokiRenameSchematic message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        ItemStack lokiStack = ItemLokiRing.getLokiRing(player);
        if(lokiStack != null && lokiStack.getItem() instanceof ItemLokiRing) {
            ItemLokiRing.renameSchematic(lokiStack, message.schematicToBeRenamed, message.newName);
            ItemLokiRing.syncLokiRing(player);
        }
        return null;
    }
}
