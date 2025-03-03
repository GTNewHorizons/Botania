package vazkii.botania.client.gui.loki;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.value.IStringValue;
import com.cleanroommc.modularui.api.widget.IGuiAction;
import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import vazkii.botania.common.item.relic.ItemLokiRing;
import vazkii.botania.common.network.PacketHandler;
import vazkii.botania.common.network.PacketLokiChangeSchematic;
import vazkii.botania.common.network.PacketLokiDeleteSchematic;

import java.util.*;

@Optional.Interface(modid = "modularui2", iface="com.cleanroommc.modularui.screen.CustomModularScreen", striprefs = true)
public class GuiLokiSchematics extends CustomModularScreen {

    public static void open(ItemStack ring) {
        GuiLokiSchematics.itemStack = ring;
        ClientGUI.open(new GuiLokiSchematics(ring));
    }

    public static ItemStack itemStack;

    public GuiLokiSchematics(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private static Object selectedSchematic;

    private static Object schematicToBeRenamed;

    private static final int WINDOW_WIDTH = 350, WINDOW_HEIGHT = 225, SCROLL_AREA_WIDTH = 350, SCROLL_AREA_HEIGHT = 200, SAVED_SCHEMATICS_HEADER_HEIGHT = 25, X_PADDING = 5, Y_PADDING = 5;

    @Override
    @Optional.Method(modid = "modularui2")
    public ModularPanel buildUI(ModularGuiContext context) {

        final UITexture backgroundTexture = AdaptableUITexture
                .builder()
                .location("botania:textures/gui/croppedPaper")
                .imageSize(330, 252)
                .adaptable(12)
                .build();
        final UITexture checkIcon = UITexture
                .builder()
                .location("botania:textures/gui/check")
                .imageSize(20, 20)
                .build();
        final UITexture deleteIcon = UITexture
                .builder()
                .location("botania:textures/gui/delete")
                .imageSize(20, 20)
                .build();
        final UITexture renameIcon = UITexture
                .builder()
                .location("botania:textures/gui/rename")
                .imageSize(20, 20)
                .build();

        selectedSchematic = itemStack.getTagCompound().getString(ItemLokiRing.TAG_CURRENT_SCHEMATIC);

        ItemLokiRing ring = (ItemLokiRing) itemStack.getItem();
        ring.schematicNames = Arrays.asList(itemStack.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).tagMap.keySet().stream().sorted(Comparator.comparing(Object::toString)).toArray());

        Set<Object> deleted = new HashSet<>();

        final ScrollWidget<?> scrollWidget = new ScrollWidget<>(new VerticalScrollData());
        scrollWidget.getScrollArea().getScrollY().setScrollSize(ring.schematicNames.size() * (SCROLL_AREA_HEIGHT / 5 + 1));
        int idx = 0;
        for(Object key : itemStack.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).tagMap.keySet()) {
            scrollWidget.child(
                    new Row()
                            .sizeRel(1f, 0.2f)
                            .pos(0, ring.schematicNames.indexOf(key) * (SCROLL_AREA_HEIGHT / 5 + 1))
                            .padding(5, 0, 5, 5)
                            .setEnabledIf((w) -> !deleted.contains(key))
                            .background(new Rectangle()
                                    .setColor(idx % 2 == 0 ? Color.ORANGE.brighter(3) : Color.ORANGE.brighter(2)))
                            .child(
                                    new TextWidget(key.toString())
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
                                                                PacketHandler.INSTANCE.sendToServer(new PacketLokiChangeSchematic(key.toString()));
                                                                selectedSchematic = key;
                                                                return true;
                                                            })
                                                            .background(checkIcon)
                                                            .hoverBackground(checkIcon)
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
                                                        this.schematicToBeRenamed = key;
                                                        ClientGUI.open(new RenameGui());
                                                        return true;
                                                    })
                                                    .background(renameIcon)
                                                    .hoverBackground(renameIcon)
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
                                                        PacketHandler.INSTANCE.sendToServer(new PacketLokiDeleteSchematic(key.toString()));
                                                        deleted.add(key);
                                                        return true;
                                                    })
                                                    .background(deleteIcon)
                                                    .hoverBackground(deleteIcon)
                                                    .size(20, 20)
                                                    .center())));
            idx++;
        }

        return ModularPanel.defaultPanel("lokiSchematics")
                .child(
                    new TextWidget(IKey.dynamic(() -> selectedSchematic == null ? StatCollector.translateToLocal("botaniamisc.select_schematic") : StatCollector.translateToLocal("botaniamisc.selected_schematic") + " " + selectedSchematic))
                            .style(EnumChatFormatting.BLACK)
                            .align(Alignment.TopCenter)
                            .marginTop(8)
                            .scale(0.75f))
                .child(
                        scrollWidget
                                .pos(10, 20)
                                .widthRel(SCROLL_AREA_WIDTH - 20)
                                .margin(10, 0)
                                .height(SCROLL_AREA_HEIGHT - 10))
                .background(backgroundTexture)
                .size(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public static class RenameGui extends CustomModularScreen {

        private String newValue = "";

        @Override
        public void close() {
            super.close();
            this.persist();
        }

        @Override
        public void close(boolean force) {
            super.close(force);
            this.persist();
        }

        public void persist() {
            if (itemStack.getTagCompound().getString(ItemLokiRing.TAG_CURRENT_SCHEMATIC).equals(schematicToBeRenamed)) {
                itemStack.getTagCompound().setString(ItemLokiRing.TAG_CURRENT_SCHEMATIC, newValue);
            }
            NBTBase nbt = itemStack.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).getTag(schematicToBeRenamed.toString());
            itemStack.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).removeTag(schematicToBeRenamed.toString());
            itemStack.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).setTag(newValue, nbt);
        }

        @Override
        public ModularPanel buildUI(ModularGuiContext context) {
            final UITexture BACKGROUND = AdaptableUITexture.builder()
                    .location("botania:textures/gui/croppedPaper")
                    .imageSize(330, 252)
                    .adaptable(12)
                    .build();

            InteractionSyncHandler handler = new InteractionSyncHandler();

            handler.setOnKeyPressed((key) -> {
                System.out.println(key.character);
            });

            return ModularPanel.defaultPanel("rename")
                    .child(new TextWidget("Schematic Name:")
                            .style(EnumChatFormatting.BLACK)
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
                    .background(BACKGROUND)
                    .size(200, 100);
        }
    }
}
