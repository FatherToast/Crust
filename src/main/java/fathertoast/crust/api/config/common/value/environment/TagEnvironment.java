package fathertoast.crust.api.config.common.value.environment;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public abstract class TagEnvironment<T> extends AbstractEnvironment {
    
    /** Strips out the ! and # characters as appropriate, returns only the actual resource location string. */
    private static String tagSignFixer( boolean invert, String line ) {
        if( invert ) return line.substring( line.startsWith( "!#" ) ? 2 : 1 );
        if( line.startsWith( "#" ) ) return line.substring( 1 );
        return line;
    }
    
    /** If true, the condition is inverted. */
    protected final boolean INVERT;
    /** The tag key for this environment. */
    private final TagKey<T> TAG_KEY;
    
    public TagEnvironment( TagKey<T> tagKey, boolean invert ) {
        INVERT = invert;
        TAG_KEY = tagKey;
    }
    
    public TagEnvironment( AbstractConfigField field, String value ) {
        INVERT = value.startsWith( "!" );
        ResourceLocation resLoc = ResourceLocation.tryParse( tagSignFixer( INVERT, value ) );
        if( resLoc == null ) {
            TAG_KEY = TagKey.create( getRegistry(), ResourceLocation.withDefaultNamespace( "" ) );
            ConfigUtil.warnFor( field );
            ConfigUtil.LOG.warn( "Environment entry has invalid tag! Ignoring. Entry: {}", name() + " " + value );
        }
        else {
            TAG_KEY = TagKey.create( getRegistry(), resLoc );
        }
    }
    
    /** @return The string value of this environment, as it would appear in a config file. */
    @Override
    public final String value() { return (INVERT ? "!#" : "#") + TAG_KEY.location(); }
    
    /** @return The registry used. */
    public abstract ResourceKey<Registry<T>> getRegistry();
    
    /** @return Returns true if this environment matches the provided environment. */
    @Override
    public boolean matches( Level level, @Nullable BlockPos pos ) {
        Holder<T> actual = getActual( level, pos );
        return (actual != null && actual.is( TAG_KEY )) != INVERT;
    }
    
    /** @return Returns the actual environment to compare, or null if there isn't enough information. */
    @Nullable
    public abstract Holder<T> getActual( Level level, @Nullable BlockPos pos );
}