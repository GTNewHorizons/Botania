package vazkii.botania.client.gui.loki;

import com.cleanroommc.modularui.api.value.IStringValue;
import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import vazkii.botania.client.gui.mui2.Textures;
import vazkii.botania.common.network.PacketHandler;
import vazkii.botania.common.network.PacketLokiRenameSchematic;

class RenameGui extends CustomModularScreen {

    private final String schematicToBeRenamed;

    public RenameGui(String schematicToBeRenamed) {
        this.schematicToBeRenamed = schematicToBeRenamed;
    }

    private String newValue = "";

    @Override
    public void close() {
        close(false);
    }

    @Override
    public void close(boolean force) {
        super.close(force);
        this.persist();
    }

    public void persist() {
        PacketHandler.INSTANCE.sendToServer(new PacketLokiRenameSchematic(schematicToBeRenamed, newValue));
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {

        InteractionSyncHandler handler = new InteractionSyncHandler();

        handler.setOnKeyPressed((key) -> {
            System.out.println(key.character);
        });

        return ModularPanel.defaultPanel("rename")
                .child(new TextWidget("Schematic Name:")
                        .pos(10, 10)
                        .size(180, 20))
                .child(new TextFieldWidget()
                        .value(new IStringValue<String>() {
                            @Override
                            public String getStringValue() {
                                return newValue;
                            }

                            @Override
                            public void setStringValue(String val) {
                                newValue = val;
                            }

                            @Override
                            public String getValue() {
                                return newValue;
                            }

                            @Override
                            public void setValue(String value) {
                                newValue = value;
                            }
                        })
                        .disableHoverBackground()
                        .size(150, 20)
                        .pos(25, 40))
                .background(Textures.BACKGROUND_TEXTURE)
                .size(200, 100);
    }
}
