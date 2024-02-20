package vazkii.botania.common.network;

import vazkii.botania.common.lib.LibMisc;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    private PacketHandler() {}

    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(LibMisc.MOD_ID.toLowerCase());

    public static void initPackets() {
        INSTANCE.registerMessage(PacketLokiToggle.class, PacketLokiToggle.class, 1, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiClear.class, PacketLokiClear.class, 2, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiMirror.class, PacketLokiMirror.class, 3, Side.SERVER);
        INSTANCE.registerMessage(PacketLokiHudNotificationAck.class, PacketLokiHudNotificationAck.class, 4, Side.CLIENT);
    }
}
