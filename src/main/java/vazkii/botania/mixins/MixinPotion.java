package vazkii.botania.mixins;

import java.util.Arrays;

import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Potion.class)
public abstract class MixinPotion {

	@Shadow
	@Final
	@Mutable
	public static Potion[] potionTypes;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void botania$extendPotionArray(CallbackInfo ci) {
		final int size = 256;
		if (potionTypes.length < size) {
			potionTypes = Arrays.copyOf(potionTypes, size);
		}
	}

}
