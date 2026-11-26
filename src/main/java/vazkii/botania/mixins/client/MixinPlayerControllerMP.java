package vazkii.botania.mixins.client;

import net.minecraft.client.multiplayer.PlayerControllerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import vazkii.botania.api.item.IExtendedPlayerController;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IExtendedPlayerController {

	@Unique
	private float botania$reachDistanceExtension;

	@ModifyReturnValue(method = "getBlockReachDistance", at = @At("RETURN"))
	private float botania$extendReach(float original) {
		return original + botania$reachDistanceExtension;
	}

	@Override
	public void setReachDistanceExtension(float f) {
		botania$reachDistanceExtension = f;
	}

	@Override
	public float getReachDistanceExtension() {
		return botania$reachDistanceExtension;
	}

}
