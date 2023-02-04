package fathertoast.crust.api.portal;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;

/**
 *  This class represents a portal builder.
 *  Portal builders are used by Crust to generate
 *  a specific type of portal with the <strong>/crustportal</strong>
 *  command, or by binding the command to a Crust inventory button.<p></p>
 *  To register your own portal builder, create a DeferredRegister with PortalBuilder.class as the base class.
 */
public abstract class PortalBuilder implements IForgeRegistryEntry<PortalBuilder> {

    private ResourceLocation registryName;
    @Nullable
    private final ResourceLocation textureLocation;

    public PortalBuilder(@Nullable ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }


    @Override
    public PortalBuilder setRegistryName(ResourceLocation name) {
        registryName = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public Class<PortalBuilder> getRegistryType() {
        return PortalBuilder.class;
    }

    /** @return A ResourceLocation pointing to this portal builder's button icon (can be null). */
    @Nullable
    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    /**
     *  @return A List of ResourceLocations representing the dimensions/worlds
     *  that this portal builder can be used in.
     */
    public abstract List<ResourceLocation> getValidDimensions();

    /** Generates the portal in the world. */
    public abstract void generate(World world, BlockPos.Mutable currentPos, Direction forward);
}
