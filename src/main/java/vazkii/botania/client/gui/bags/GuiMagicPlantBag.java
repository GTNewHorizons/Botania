package vazkii.botania.client.gui.bags;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public abstract class GuiMagicPlantBag extends GuiContainer {

    private final ResourceLocation texture;
    public GuiMagicPlantBag(ContainerMagicPlantBag container, ResourceLocation texture) {
        super(container);
        this.texture = texture;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = StatCollector.translateToLocal(getLocalizedNameKey());
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(texture);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

        List<Slot> slotList = inventorySlots.inventorySlots;
        for(Slot slot : slotList)
            if(slot instanceof SlotMagicPlant) {
                SlotMagicPlant slotf = (SlotMagicPlant) slot;
                if(!slotf.getHasStack()) {
                    ItemStack stack = new ItemStack(itemTypeAllowed(), 0, slotf.color);
                    int x = guiLeft + slotf.xDisplayPosition;
                    int y = guiTop + slotf.yDisplayPosition;
                    RenderHelper.enableGUIStandardItemLighting();
                    RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, stack, x, y);
                    RenderHelper.disableStandardItemLighting();
                    mc.fontRenderer.drawStringWithShadow("0", x + 11, y + 9, 0xFF6666);
                }
            }
    }

    @Override
    protected boolean checkHotbarKeys(int keyCode) {
        return false;
    }

    protected abstract Block itemTypeAllowed();

    protected abstract String getLocalizedNameKey();
}