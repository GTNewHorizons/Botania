package vazkii.botania.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import vazkii.botania.common.lib.LibMisc;

public class PacketHandler {

    private PacketHandler() {}

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(LibMisc.MOD_ID.toLowerCase());

    public static void initPackets() {
        INSTANCE.registerMessage(PacketLokiToggle.class, PacketLokiToggle.class, 1, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiClear.class, PacketLokiClear.class, 2, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiMirror.class, PacketLokiMirror.class, 3, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiSaveSchematic.class, PacketLokiSaveSchematic.class, 4, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiChangeSchematic.class, PacketLokiChangeSchematic.class, 5, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiDeleteSchematic.class, PacketLokiDeleteSchematic.class, 6, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiRenameSchematic.class, PacketLokiRenameSchematic.class, 7, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiHudNotificationAck.class, PacketLokiHudNotificationAck.class, 8, Side.CLIENT);
    }
}
