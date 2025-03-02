package vazkii.botania.client.gui.loki;

import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.UIInfos;
import com.gtnewhorizons.modularui.api.drawable.AdaptableUITexture;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.Text;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.api.widget.IWidgetParent;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;
import cpw.mods.fml.common.Optional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import vazkii.botania.common.item.relic.ItemLokiRing;
import vazkii.botania.common.network.PacketHandler;
import vazkii.botania.common.network.PacketLokiChangeSchematic;
import vazkii.botania.common.network.PacketLokiDeleteSchematic;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiLokiSchematics {

    private static Object selectedSchematic;

    private static String newSchematicName = "";

    private static final int WINDOW_WIDTH = 350, WINDOW_HEIGHT = 225, SCROLL_AREA_WIDTH = 350, SCROLL_AREA_HEIGHT = 200, SAVED_SCHEMATICS_HEADER_HEIGHT = 25, X_PADDING = 5, Y_PADDING = 5;

    @Optional.Method(modid = "modularui")
    public static ModularWindow createWindow(Object buildContext, ItemStack heldStack) {
        ItemLokiRing loki = (ItemLokiRing) heldStack.getItem();
        loki.window = GuiLokiSchematics.getWindow((UIBuildContext) buildContext, heldStack);
        loki.schematicNames = new ArrayList<>(heldStack.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).tagMap.keySet());
        return (ModularWindow) loki.window;
    }

    @Optional.Method(modid = "modularui")
    public static void openUI(EntityPlayer player, ItemStack stack) {
        if(ItemLokiRing.isLokiRing(stack) && stack.getItem() instanceof ItemLokiRing) {
            UIInfos.openClientUI(player, uiBuildContext -> GuiLokiSchematics.createWindow(uiBuildContext, stack));
        }
    }

    @Optional.Method(modid = "modularui")
    public static ModularWindow getWindow(UIBuildContext buildContext, ItemStack lokiRing) {
        final AdaptableUITexture BACKGROUND = AdaptableUITexture
                .of("botania:textures/gui/croppedPaper", 330, 252, 12);
        final AdaptableUITexture DELETE = AdaptableUITexture
                .of("botania:textures/gui/lokiDelete", 500, 500, 0);
        final AdaptableUITexture RENAME = AdaptableUITexture
                .of("botania:textures/gui/lokiRename", 500, 500, 0);
        if(lokiRing.getTagCompound().tagMap.containsKey(ItemLokiRing.TAG_CURRENT_SCHEMATIC)) {
            // Substring to remove the leading and ending double quotes
            selectedSchematic = lokiRing.stackTagCompound.getString(ItemLokiRing.TAG_CURRENT_SCHEMATIC).substring(1);
            selectedSchematic = selectedSchematic.toString().substring(0, selectedSchematic.toString().length() - 1);
        }
        ItemLokiRing ring = (ItemLokiRing) lokiRing.getItem();
        ModularWindow.Builder builder = ModularWindow.builder(WINDOW_WIDTH, WINDOW_HEIGHT);
        buildContext.setShowNEI(false);
        builder.setBackground(ModularUITextures.VANILLA_BACKGROUND);
        Scrollable scrollArea =
                new Scrollable()
                        .setVerticalScroll();
        if(lokiRing.getTagCompound().tagMap.containsKey(ItemLokiRing.TAG_SAVED_SCHEMATICS) && !lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).tagMap.keySet().isEmpty()) {
            final AtomicInteger offset = new AtomicInteger();
            final AtomicInteger disabled = new AtomicInteger();
            for(Object schematicName : lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).tagMap.keySet()) {
                scrollArea = scrollArea.widget(new ButtonWidget()
                        .setOnClick((clickData, widget) -> {
                            selectedSchematic = schematicName;
                            PacketHandler.INSTANCE.sendToServer(new PacketLokiChangeSchematic(schematicName.toString()));
                        })
                        .setBackground(() -> new IDrawable[]{new Text(schematicName.toString()).format(selectedSchematic.toString().equals(schematicName.toString()) ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.BLACK)})
                        .setSize(SCROLL_AREA_WIDTH - 60, SCROLL_AREA_HEIGHT / 10)
                        .setEnabled((widget) -> {
                            if(lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).hasKey(schematicName.toString())) {
                                return true;
                            }
                            disabled.getAndIncrement();
                            return false;
                        })
                        .setPosProvider((Size screenSize, ModularWindow window, IWidgetParent parent) -> new Pos2d(0, ring.schematicNames.indexOf(schematicName) * (SCROLL_AREA_HEIGHT / 10 + 1))));
                scrollArea = scrollArea.widget(new ButtonWidget()
                        .setOnClick((clickData, widget) -> {
                            UIInfos.openClientUI(buildContext.getPlayer(), (context) -> GuiLokiSchematics.getRenameWindow(context, lokiRing, schematicName));
                        })
                        .setBackground(RENAME)
                        .setPosProvider((Size screenSize, ModularWindow window, IWidgetParent parent) -> new Pos2d(SCROLL_AREA_WIDTH - 55, ring.schematicNames.indexOf(schematicName) * (SCROLL_AREA_HEIGHT / 10 + 1)))
                        .setEnabled((widget) -> lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).hasKey(schematicName.toString()))
                        .setSize(20, 20));
                scrollArea = scrollArea.widget(new ButtonWidget()
                        .setOnClick((clickData, widget) -> {
                            PacketHandler.INSTANCE.sendToServer(new PacketLokiDeleteSchematic(schematicName.toString()));
                            buildContext.setValidator(() -> false);
                        })
                        .setBackground(DELETE)
                        .setPosProvider((Size screenSize, ModularWindow window, IWidgetParent parent) -> new Pos2d(SCROLL_AREA_WIDTH - 30, ring.schematicNames.indexOf(schematicName) * (SCROLL_AREA_HEIGHT / 10 + 1)))
                        .setEnabled((widget) -> lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).hasKey(schematicName.toString()))
                        .setSize(20, 20));
                offset.addAndGet((SCROLL_AREA_HEIGHT / 10) + 1);
            }
        }
        scrollArea = scrollArea.widget(new TextWidget("No saved schematics!")
                .setEnabled((widget) -> !lokiRing.getTagCompound().hasKey(ItemLokiRing.TAG_SAVED_SCHEMATICS) || lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).tagMap.keySet().isEmpty())
                .setSize(SCROLL_AREA_WIDTH, SCROLL_AREA_HEIGHT)
                .setPos(0, 0));
        return builder
                .widget(
                        new MultiChildWidget()
                                .addChild(
                                        new TextWidget()
                                                .setScale(0.5F)
                                                .setTextSupplier(() -> new Text(selectedSchematic == null ? StatCollector.translateToLocal("botaniamisc.select_schematic") : StatCollector.translateToLocal("botaniamisc.selected_schematic") + " " + selectedSchematic)
                                                        .format(selectedSchematic == null ? EnumChatFormatting.RED : EnumChatFormatting.BLACK)
                                                        .alignment(Alignment.Center))
                                                .setSize(SCROLL_AREA_WIDTH, SAVED_SCHEMATICS_HEADER_HEIGHT))
                                .addChild(
                                        scrollArea
                                                .setSize(SCROLL_AREA_WIDTH, SCROLL_AREA_HEIGHT - SAVED_SCHEMATICS_HEADER_HEIGHT - Y_PADDING * 2)
                                                .setPos(0, SAVED_SCHEMATICS_HEADER_HEIGHT))
                                .setPos(WINDOW_WIDTH - SCROLL_AREA_WIDTH - X_PADDING, 0))
                .setBackground(BACKGROUND)
                .build();
    }

    @Optional.Method(modid = "modularui")
    public static ModularWindow getRenameWindow(UIBuildContext buildContext, ItemStack lokiRing, Object schematicName) {
        final AdaptableUITexture BACKGROUND = AdaptableUITexture
                .of("botania:textures/gui/croppedPaper", 330, 252, 12);
        ModularWindow.Builder builder = ModularWindow.builder(200, 100);

        buildContext.addCloseListener(() -> {
            if(lokiRing.getTagCompound().getString(ItemLokiRing.TAG_CURRENT_SCHEMATIC).equals(schematicName)) {
                lokiRing.getTagCompound().setString(ItemLokiRing.TAG_CURRENT_SCHEMATIC, newSchematicName);
            }
            NBTBase nbt = lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).getTag(schematicName.toString());
            lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).removeTag(schematicName.toString());
            lokiRing.getTagCompound().getCompoundTag(ItemLokiRing.TAG_SAVED_SCHEMATICS).setTag(newSchematicName, nbt);
        });

        return builder
                .widget(new TextWidget()
                        .setTextSupplier(() -> new Text("Schematic Name:").format(EnumChatFormatting.BLACK))
                        .setPos(10, 10)
                        .setSize(180, 20))
                .widget(new TextFieldWidget()
                        .setGetter(() -> newSchematicName)
                        .setSetter((newValue) -> newSchematicName = newValue)
                        .setSize(150, 20)
                        .setPos(25, 40))
                .setBackground(BACKGROUND)
                .build();
    }
}
