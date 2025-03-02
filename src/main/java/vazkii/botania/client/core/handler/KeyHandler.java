package vazkii.botania.client.core.handler;

import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.factory.GuiFactories;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import vazkii.botania.client.gui.loki.GuiLokiSchematics;
import vazkii.botania.common.item.relic.ItemLokiRing;
import vazkii.botania.common.network.*;


public class KeyHandler {
    private static final KeyBinding keyToggleRingOfLoki= new KeyBinding("botaniamisc.toggleLoki", 0, "botaniamisc.keyCategory");
    private static final KeyBinding keyRingOfLokiClear= new KeyBinding("botaniamisc.ringOfLokiClear", 0, "botaniamisc.keyCategory");
    private static final KeyBinding keyRingOfLokiMirror= new KeyBinding("botaniamisc.ringOfLokiMirror", 0, "botaniamisc.keyCategory");
    private static final KeyBinding keyRingOfLokiSaveSchematic= new KeyBinding("botaniamisc.ringOfLokiSaveSchematic", 0, "botaniamisc.keyCategory");
    private static final KeyBinding keyRingOfLokiOpenUI= new KeyBinding("botaniamisc.ringOfLokiOpenUI", 0, "botaniamisc.keyCategory");

    public KeyHandler() {
        FMLCommonHandler.instance().bus().register(this);
        ClientRegistry.registerKeyBinding(keyToggleRingOfLoki);
        ClientRegistry.registerKeyBinding(keyRingOfLokiClear);
        ClientRegistry.registerKeyBinding(keyRingOfLokiMirror);
        ClientRegistry.registerKeyBinding(keyRingOfLokiSaveSchematic);
        ClientRegistry.registerKeyBinding(keyRingOfLokiOpenUI);

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
        if(keyRingOfLokiSaveSchematic.isPressed()){
            ringOfLokiSaveSchematic();
        }
        if(keyRingOfLokiOpenUI.isPressed()) {
            ringOfLokiOpenUI();
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
    private static void ringOfLokiSaveSchematic() {
        PacketHandler.INSTANCE.sendToServer(new PacketLokiSaveSchematic());
    }
    private static void ringOfLokiOpenUI() {
        if(ItemLokiRing.isModularUIEnabled) {
            ItemStack ring = ItemLokiRing.getLokiRing(Minecraft.getMinecraft().thePlayer);
            GuiLokiSchematics.open(ring);
        }
    }
}
