/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Jan 17, 2014, 4:52:37 PM (GMT)]
 */
package vazkii.botania.common.core.helper;

import java.lang.reflect.Field;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.common.lib.LibObfuscation;
import vazkii.botania.utils.ReflectionUtils;

public class ObfuscationHelper {

	private static Field particleTextureField;
	private static boolean particleTextureResolved = false;

	public static ResourceLocation getParticleTexture() {
		if(!particleTextureResolved) {
			particleTextureField = ReflectionUtils.findField(EffectRenderer.class, LibObfuscation.PARTICLE_TEXTURES);
			particleTextureResolved = true;
		}

		return (ResourceLocation) ReflectionUtils.getObject(particleTextureField, null);
	}
}
