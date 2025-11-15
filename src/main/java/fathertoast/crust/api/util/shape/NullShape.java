package fathertoast.crust.api.util.shape;

import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.util.IDebugShape;
import net.minecraft.resources.ResourceLocation;

/** A shape that does not render anything. */
public final class NullShape implements IDebugShape {
    
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath( ICrustApi.MOD_ID, "null" );
    
    private static final NullShape INSTANCE = new NullShape();
    
    public static NullShape getInstance() { return INSTANCE; }
    
    /**
     * @return The id that this shape is registered to. Generally, each non-abstract
     * shape class should override this method and return its own unique id.
     */
    @Override
    public ResourceLocation getId() { return ID; }
    
    private NullShape() { }
}