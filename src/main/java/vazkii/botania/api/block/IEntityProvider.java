package vazkii.botania.api.block;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@FunctionalInterface
public interface IEntityProvider {
    Entity createEntity(World world);
}
