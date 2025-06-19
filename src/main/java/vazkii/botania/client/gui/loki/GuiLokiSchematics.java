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
import com.cleanroommc.modularui.widget.scroll.ScrollArea;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import vazkii.botania.client.gui.mui2.Textures;
import vazkii.botania.common.item.relic.ItemLokiRing;
import vazkii.botania.common.network.PacketHandler;
import vazkii.botania.common.network.PacketLokiChangeSchematic;
import vazkii.botania.common.network.PacketLokiDeleteSchematic;

import java.util.ArrayList;

public class GuiLokiSchematics {

    private static final int WINDOW_WIDTH = 350, WINDOW_HEIGHT = 225, SCROLL_AREA_WIDTH = 350, SCROLL_AREA_HEIGHT = 200, SAVED_SCHEMATICS_HEADER_HEIGHT = 25, X_PADDING = 5, Y_PADDING = 5;
    private ArrayList<String> schematicNames;

    public static void open(ItemStack ring) {
        ClientGUI.open(new ModularScreen(new GuiLokiSchematics().buildUI(ring)));
    }

    private String selectedSchematic;

    private GuiLokiSchematics() {
    }

    public ModularPanel buildUI(ItemStack itemStack) {
        if (! ItemLokiRing.isLokiRing(itemStack)) {
            return null;
        }

        selectedSchematic = ItemLokiRing.getSelectedSchematic(itemStack);
        schematicNames = new ArrayList<>(ItemLokiRing.getSchematicNames(itemStack));

        final ScrollWidget<?> scrollWidget = new ScrollWidget<>(new VerticalScrollData());
        setScrollArea(scrollWidget.getScrollArea());
        for (int i = 0; i < schematicNames.size(); i++) {
            // Final value that can be captured by lambdas
            final int rowId = i;
            Flow row = new Row();
            row.sizeRel(1f, 0.2f)
                    .pos(0, rowId * (SCROLL_AREA_HEIGHT / 5 + 1))
                    .padding(5, 0, 5, 5)
                    .setEnabledIf((w) -> rowId < schematicNames.size())
                    .background(new Rectangle()
                            .setColor(i % 2 == 0 ? Color.ORANGE.brighter(3) : Color.ORANGE.brighter(2)))
                    .child(
                            new TextWidget(IKey.dynamic(()-> rowId < schematicNames.size() ? schematicNames.get(rowId) : ""))
                                    .widthRel(0.72f))
                    .child(
                            new ParentWidget<>()
                                    .tooltip(new RichTooltip()
                                            .add("Activate Schematic"))
                                    .widthRel(0.083f)
                                    .heightRel(1f)
                                    .setEnabledIf((w) -> rowId < schematicNames.size() && !schematicNames.get(rowId).equals(selectedSchematic))
                                    .child(
                                            new ButtonWidget<>()
                                                    .onMousePressed(ignored -> {
                                                        String key = schematicNames.get(rowId);
                                                        PacketHandler.INSTANCE.sendToServer(new PacketLokiChangeSchematic(key));
                                                        selectedSchematic = key;
                                                        return true;
                                                    })
                                                    .background(Textures.CHECK_ICON)
                                                    .hoverBackground(Textures.CHECK_ICON)
                                                    .size(20, 20)
                                                    .center()))
                    .child(
                            new ParentWidget<>()
                                    .widthRel(0.083f)
                                    .heightRel(1f)
                                    .child(
                                            new ButtonWidget<>()
                                                    .tooltip(new RichTooltip()
                                                            .add("Rename Schematic"))
                                                    .onMousePressed(ignored -> {
                                                        ClientGUI.open(new RenameGui(schematicNames.get(rowId)));
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
                                                    .tooltip(new RichTooltip()
                                                            .add("Delete Schematic"))
                                                    .onMousePressed(ignored -> {
                                                        String key = schematicNames.get(rowId);
                                                        PacketHandler.INSTANCE.sendToServer(new PacketLokiDeleteSchematic(key));
                                                        schematicNames.remove(rowId);
                                                        setScrollArea(scrollWidget.getScrollArea());
                                                        if(key.equals(selectedSchematic))
                                                            selectedSchematic = null;
                                                        return true;
                                                    })
                                                    .background(Textures.DELETE_ICON)
                                                    .hoverBackground(Textures.DELETE_ICON)
                                                    .size(20, 20)
                                                    .center()));
            scrollWidget.child(
                    row);
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

    private void setScrollArea(ScrollArea scrollWidget) {
        scrollWidget.getScrollY().setScrollSize(schematicNames.size() * (SCROLL_AREA_HEIGHT / 5 + 1));
    }

}
