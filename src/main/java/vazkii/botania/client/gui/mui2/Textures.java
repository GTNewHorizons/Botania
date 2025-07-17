package vazkii.botania.client.gui.mui2;

import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.UITexture;

public class Textures {
    public static final UITexture BACKGROUND_TEXTURE = AdaptableUITexture
            .builder()
            .location("botania:textures/gui/croppedPaper")
            .imageSize(330, 252)
            .adaptable(12)
            .build();
    public static final UITexture CHECK_ICON = UITexture
            .builder()
            .location("botania:textures/gui/check")
            .imageSize(20, 20)
            .build();
    public static final UITexture DELETE_ICON = UITexture
            .builder()
            .location("botania:textures/gui/delete")
            .imageSize(20, 20)
            .build();
    public static final UITexture RENAME_ICON = UITexture
            .builder()
            .location("botania:textures/gui/rename")
            .imageSize(20, 20)
            .build();
}
