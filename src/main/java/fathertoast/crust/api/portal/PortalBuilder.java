package fathertoast.crust.api.portal;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * This class represents a portal builder.
 * Portal builders are used by Crust to generate
 * a specific type of portal with the <strong>/crustportal</strong>
 * command, or by binding the command to a Crust inventory button.<p></p>
 * To register your own portal builder, create a DeferredRegister with PortalBuilder.class as the base class.
 */
public abstract class PortalBuilder implements IForgeRegistryEntry<PortalBuilder> {
    
    /** @return True if this portal builder can be used in the provided dimension. */
    public boolean isValidDimension( World world ) { return isValidDimension( world.dimension().location() ); }
    
    /** @return True if this portal builder can be used in the provided dimension. */
    public abstract boolean isValidDimension( ResourceLocation dimension );
    
    /**
     * Generates the portal in the world with a particular position and direction.
     *
     * @param world      The world to generate in.
     * @param currentPos The front-center position of the portal. This is often a block position directly
     *                   above a solid 'floor block'. This is mutable so that you can #move() it rather than
     *                   create numerous BlockPos objects.
     * @param forward    Horizontal facing of the portal; defined as the direction that a player is facing to
     *                   see the portal. By convention, the transverse direction is forward.getClockWise().
     */
    public abstract void generate( World world, BlockPos.Mutable currentPos, Direction forward );
    
    
    // ---- Forge Registry Entry Impl ---- //
    
    private ResourceLocation registryName;
    
    /**
     * Sets a unique name for this Item. This should be used for uniquely identify the instance of the Item.
     * This is the valid replacement for the atrocious 'getUnlocalizedName().substring(6)' stuff that everyone does.
     * Unlocalized names have NOTHING to do with unique identifiers. As demonstrated by vanilla blocks and items.
     * <p>
     * The supplied name will be prefixed with the currently active mod's modId.
     * If the supplied name already has a prefix that is different, it will be used and a warning will be logged.
     * <p>
     * If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.
     * <p>
     * Returns 'this' to allow for chaining.
     *
     * @param name Unique registry name
     * @return This instance
     */
    @Override
    public final PortalBuilder setRegistryName( ResourceLocation name ) {
        registryName = name;
        return this;
    }
    
    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * Otherwise it will return the name set in setRegistryName().
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    @Override
    public final ResourceLocation getRegistryName() { return registryName; }
    
    /**
     * Determines the type for this entry, used to look up the correct registry in the global registries list as there can only be one
     * registry per concrete class.
     *
     * @return Root registry type.
     */
    @Override
    public final Class<PortalBuilder> getRegistryType() { return PortalBuilder.class; }
}