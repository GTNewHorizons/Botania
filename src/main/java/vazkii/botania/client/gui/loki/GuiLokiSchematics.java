package vazkii.botania.client.gui.loki;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Row;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import vazkii.botania.client.gui.mui2.Textures;
import vazkii.botania.common.item.relic.ItemLokiRing;
import vazkii.botania.common.network.PacketHandler;
import vazkii.botania.common.network.PacketLokiChangeSchematic;
import vazkii.botania.common.network.PacketLokiDeleteSchematic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuiLokiSchematics {

    private static final int WINDOW_WIDTH = 350, WINDOW_HEIGHT = 225, SCROLL_AREA_WIDTH = 350, SCROLL_AREA_HEIGHT = 200, SAVED_SCHEMATICS_HEADER_HEIGHT = 25, X_PADDING = 5, Y_PADDING = 5;

    public static void open(ItemStack ring) {
        ClientGUI.open(new ModularScreen(new GuiLokiSchematics().buildUI(ring)));
    }

    private final Set<String> deleted = new HashSet<>();

    private String selectedSchematic;

    private GuiLokiSchematics() {
    }

    public ModularPanel buildUI(ItemStack itemStack) {
        if (! ItemLokiRing.isLokiRing(itemStack)) {
            return null;
        }

        selectedSchematic = ItemLokiRing.getSelectedSchematic(itemStack);
        List<String> schematicNames = ItemLokiRing.getSchematicNames(itemStack);

        final ScrollWidget<?> scrollWidget = new ScrollWidget<>(new VerticalScrollData());
        scrollWidget.getScrollArea().getScrollY().setScrollSize(schematicNames.size() * (SCROLL_AREA_HEIGHT / 5 + 1));
        for (int idx = 0; idx < schematicNames.size(); idx++) {
            String key = schematicNames.get(idx);
            scrollWidget.child(
                    new Row()
                            .sizeRel(1f, 0.2f)
                            .pos(0, schematicNames.indexOf(key) * (SCROLL_AREA_HEIGHT / 5 + 1))
                            .padding(5, 0, 5, 5)
                            .setEnabledIf((w) -> !deleted.contains(key))
                            .background(new Rectangle()
                                    .setColor(idx % 2 == 0 ? Color.ORANGE.brighter(3) : Color.ORANGE.brighter(2)))
                            .child(
                                    new TextWidget(key)
                                            .widthRel(0.72f))
                            .child(
                                    new ParentWidget<>()
                                            .tooltip(new RichTooltip(new TextWidget("Activate Schematic")))
                                            .widthRel(0.083f)
                                            .heightRel(1f)
                                            .setEnabledIf((w) -> !key.equals(selectedSchematic))
                                            .child(
                                                    new ButtonWidget<>()
                                                            .onMousePressed(ignored -> {
                                                                PacketHandler.INSTANCE.sendToServer(new PacketLokiChangeSchematic(key));
                                                                selectedSchematic = key;
                                                                return true;
                                                            })
                                                            .background(Textures.CHECK_ICON)
                                                            .hoverBackground(Textures.CHECK_ICON)
                                                            .size(20,20)
                                                            .center()))
                            .child(
                                    new ParentWidget<>()
                                            .widthRel(0.083f)
                                            .heightRel(1f)
                                            .child(
                                                new ButtonWidget<>()
                                                    .tooltip(new RichTooltip(new TextWidget("Rename Schematic")))
                                                    .onMousePressed(ignored -> {
                                                        ClientGUI.open(new RenameGui(key));
                                                        return true;
                                                    })
                                                    .background(Textures.RENAME_ICON)
                                                    .hoverBackground(Textures.RENAME_ICON)
                                                    .size(20, 20)
                                                    .center()))
                            .child(
                                    new ParentWidget<>()
                                            .widthRel(0.083f)
                                            .heightRel(1f)
                                            .child(
                                                new ButtonWidget<>()
                                                    .tooltip(new RichTooltip(new TextWidget("Delete Schematic")))
                                                    .onMousePressed(ignored -> {
                                                        PacketHandler.INSTANCE.sendToServer(new PacketLokiDeleteSchematic(key));
                                                        deleted.add(key);
                                                        if(key.equals(selectedSchematic))
                                                            selectedSchematic = null;
                                                        return true;
                                                    })
                                                    .background(Textures.DELETE_ICON)
                                                    .hoverBackground(Textures.DELETE_ICON)
                                                    .size(20, 20)
                                                    .center())));
        }

        return ModularPanel.defaultPanel("lokiSchematics")
                .child(
                    new TextWidget(IKey.dynamic(() -> selectedSchematic == null ? StatCollector.translateToLocal("botaniamisc.select_schematic") : StatCollector.translateToLocal("botaniamisc.selected_schematic") + " " + selectedSchematic))
                    .align(Alignment.TopCenter)
                    .marginTop(8)
                    .scale(0.5f))
                .child(
                        scrollWidget
                                .pos(10, 20)
                                .widthRel(SCROLL_AREA_WIDTH - 20)
                                .margin(10, 0)
                                .height(SCROLL_AREA_HEIGHT - 10))
                .background(Textures.BACKGROUND_TEXTURE)
                .size(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

}
