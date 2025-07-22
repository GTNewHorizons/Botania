package vazkii.botania.client.core.handler;

import net.minecraft.client.settings.KeyBinding;
import vazkii.botania.common.network.PacketHandler;
import vazkii.botania.common.network.PacketLokiClear;
import vazkii.botania.common.network.PacketLokiMirror;
import vazkii.botania.common.network.PacketLokiToggle;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class KeyHandler {
    private static final KeyBinding keyToggleRingOfLoki= new KeyBinding("botaniamisc.toggleLoki", 0, "botaniamisc.keyCategory");
    private static final KeyBinding keyRingOfLokiClear= new KeyBinding("botaniamisc.ringOfLokiClear", 0, "botaniamisc.keyCategory");
    private static final KeyBinding keyRingOfLokiMirror= new KeyBinding("botaniamisc.ringOfLokiMirror", 0, "botaniamisc.keyCategory");

    public KeyHandler() {
        FMLCommonHandler.instance().bus().register(this);
        ClientRegistry.registerKeyBinding(keyToggleRingOfLoki);
        ClientRegistry.registerKeyBinding(keyRingOfLokiClear);
        ClientRegistry.registerKeyBinding(keyRingOfLokiMirror);

    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event){
        checkAndPerformKeyActions();
    }

    private void checkAndPerformKeyActions(){
        if (keyToggleRingOfLoki.isPressed()){
            toggleRingLoki();
        }
        if(keyRingOfLokiClear.isPressed()){
            ringOfLokiClear();
        }
        if(keyRingOfLokiMirror.isPressed()){
            toggleRingLokiMirroring();
        }
    }

    private static void toggleRingLoki() {
        PacketHandler.INSTANCE.sendToServer(new PacketLokiToggle());
    }

    private static void ringOfLokiClear() {
        PacketHandler.INSTANCE.sendToServer(new PacketLokiClear());
    }
    private static void toggleRingLokiMirroring() {
        PacketHandler.INSTANCE.sendToServer(new PacketLokiMirror());
    }
}
