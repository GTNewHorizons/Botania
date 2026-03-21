package vazkii.botania.client.integration.nei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;

public class NEIHelper {
    public static final RenderItem renderItem = new RenderItem();
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final TextureManager textureManager = mc.getTextureManager();
    public static final FontRenderer font = mc.fontRenderer;
}
