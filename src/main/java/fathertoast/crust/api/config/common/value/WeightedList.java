package fathertoast.crust.api.config.common.value;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Represents a list of weighted items that can be chosen at random.
 * <p>
 * Creates a config field for each item so weights can be defined by the user.
 */
@SuppressWarnings( "unused" )
public class WeightedList<T extends WeightedList.Value> {
    
    /** The weighted entries in this list. */
    private final List<Entry<T>> ENTRIES;
    /** The total weight of all entries in this list. */
    private int totalWeight;
    
    /**
     * Creates a new weighted list config option and registers it and any needed definitions with the spec.
     */
    public WeightedList( CrustConfigSpec SPEC, String key, T[] values, @Nullable String... description ) {
        this( SPEC, key, Arrays.asList( values ), description );
    }
    
    /**
     * Creates a new weighted list config option and registers it and any needed definitions with the spec.
     */
    public WeightedList( CrustConfigSpec SPEC, String key, Iterable<T> values, @Nullable String... description ) {
        String name = ConfigUtil.camelCaseToLowerSpace( (key.startsWith( SPEC.loadingCategory ) ?
                key.substring( SPEC.loadingCategory.length() ) : key)
                .replace( '_', ' ' ).replace( ".", " > " ) );
        final IntField.Range fieldRange = IntField.Range.NON_NEGATIVE;
        if( description != null ) {
            List<String> comment = TomlHelper.newComment( description );
            comment.add( TextFormatting.GRAY + TomlHelper.multiFieldInfo( fieldRange ) );
            SPEC.titledComment( name, comment );
        }
        else {
            SPEC.titledComment( name );
        }
        
        // Define each value's weight field and connect the value to its weight in an entry
        List<Entry<T>> list = new ArrayList<>();
        for( T value : values ) {
            list.add( new Entry<>( value, SPEC.define( new IntField(
                    key + "." + value.getKey(), value.getDefaultWeight(), fieldRange, value.getComment()
            ) ) ) );
        }
        ENTRIES = Collections.unmodifiableList( list );
        SPEC.callback( this::recalculateTotalWeight );
    }
    
    /**
     * @param random The RNG to use for rolling the item.
     * @return Returns a random item from this weighted list. Null if none of the items have a positive weight.
     */
    @Nullable
    public T next( Random random ) {
        if( isDisabled() ) return null;
        
        int choice = random.nextInt( totalWeight );
        for( Entry<T> entry : ENTRIES ) {
            choice -= entry.getWeight();
            if( choice < 0 ) return entry.getValue();
        }
        
        ConfigUtil.LOG.error( "Weighting error occurred while rolling random item! " +
                "This may have been caused by configs reloading during random roll (comod). Otherwise, it is very bad. :(" );
        return null;
    }
    
    /** @return Returns true if this list was implicitly disabled by setting all weights to 0. */
    public boolean isDisabled() { return totalWeight <= 0; }
    
    /** Recalculates the total weight of all entries in this list. */
    public void recalculateTotalWeight() {
        int weight = 0;
        for( Entry<T> entry : ENTRIES ) {
            weight += entry.getWeight();
        }
        totalWeight = weight;
    }
    
    /** An entry links a single value with its config-defined weight. */
    private static class Entry<T extends Value> {
        /** @see #getValue() */
        private final T VALUE;
        /** @see #getWeight() */
        private final IntField WEIGHT;
        
        private Entry( T value, IntField weight ) {
            VALUE = value;
            WEIGHT = weight;
        }
        
        /** The entry's underlying value. */
        T getValue() { return VALUE; }
        
        /** The config field that defines the weight of this entry. */
        int getWeight() { return WEIGHT.get(); }
    }
    
    /** Values have a unique key and may optionally provide their own comment and default weight. */
    public interface Value {
        /** @return Returns the unique key for this object. */
        String getKey();
        
        /** @return Returns the default weight for this object. */
        default int getDefaultWeight() { return 1; }
        
        /** @return Returns the comment for this object. */
        @Nullable
        default String[] getComment() { return null; }
    }
}