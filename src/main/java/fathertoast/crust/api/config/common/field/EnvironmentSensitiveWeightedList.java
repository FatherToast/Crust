package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import fathertoast.crust.api.config.common.value.environment.EnvironmentContext;
import fathertoast.crust.api.util.JavaRandomSource;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * Represents an environment-sensitive list of weighted values. Unlike the normal weighted list, this is just a simple
 * wrapper class for multiple independent fields.
 * It sacrifices flexibility for automation, largely to help with the craziness of environment list fields.
 */
public class EnvironmentSensitiveWeightedList<T> {
    
    private final List<Entry<T>> UNDERLYING_LIST;
    
    /** Links an array of values to two arrays of fields as base weights and exceptions. */
    public EnvironmentSensitiveWeightedList( T[] values, IntField[] baseWeights, EnvironmentListField<Integer>[] weightExceptions ) {
        if( values.length != baseWeights.length || values.length != weightExceptions.length )
            throw new IllegalArgumentException( "All arrays must be equal length!" );
        
        final ArrayList<Entry<T>> list = new ArrayList<>();
        for( int i = 0; i < values.length; i++ ) {
            list.add( new Entry<>( values[i], new IntField.EnvironmentSensitive( baseWeights[i], weightExceptions[i] ) ) );
            
            // Do a bit of error checking; allows us to ignore the possibility of negative weights
            if( baseWeights[i].minValue() < 0.0 || weightExceptions[i].getDefaultValue().codec() != IntValueCodec.NON_NEGATIVE ) {
                throw new IllegalArgumentException( "Weight is not allowed to be negative! See " +
                        baseWeights[i].getKey() + " and/or " + weightExceptions[i].getKey() );
            }
        }
        list.trimToSize();
        UNDERLYING_LIST = Collections.unmodifiableList( list );
    }
    
    /** @return Returns a random item from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public T next( Random random, EnvironmentContext context ) {
        return next( JavaRandomSource.of( random ), context );
    }
    
    /** @return Returns a random item from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public T next( Random random, EnvironmentContext context, @Nullable Predicate<T> selector ) {
        return next( JavaRandomSource.of( random ), context, selector );
    }
    
    /** @return Returns a random item from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public T next( RandomSource random, EnvironmentContext context ) { return next( random, context, null ); }
    
    /** @return Returns a random item from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public T next( RandomSource random, EnvironmentContext context, @Nullable Predicate<T> selector ) {
        // Due to the 'nebulous' nature of environment-based weights, we must recalculate weights for EVERY call
        int[] weights = new int[UNDERLYING_LIST.size()];
        int totalWeight = calculateWeights( weights, context, selector );
        
        return next( random, weights, totalWeight );
    }
    
    /** @return Returns a specified number of random items from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public List<T> next( Random random, int count, EnvironmentContext context ) {
        return next( JavaRandomSource.of( random ), count, context );
    }
    
    /** @return Returns a specified number of random items from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public List<T> next( Random random, int count, EnvironmentContext context, @Nullable Predicate<T> selector ) {
        return next( JavaRandomSource.of( random ), count, context, selector );
    }
    
    /** @return Returns a specified number of random items from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public List<T> next( RandomSource random, int count, EnvironmentContext context ) {
        return next( random, count, context, null );
    }
    
    /** @return Returns a specified number of random items from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    public List<T> next( RandomSource random, int count, EnvironmentContext context, @Nullable Predicate<T> selector ) {
        // Due to the 'nebulous' nature of environment-based weights, we must recalculate weights for EVERY call
        int[] weights = new int[UNDERLYING_LIST.size()];
        int totalWeight = calculateWeights( weights, context, selector );
        if( totalWeight <= 0 ) return null;
        
        List<T> items = new ArrayList<>( count );
        for( int i = 0; i < count; i++ ) {
            items.add( next( random, weights, totalWeight ) );
        }
        return items;
    }
    
    /** Calculates the current weights, fills the provided weights array, and returns the total weight. */
    private int calculateWeights( int[] weights, EnvironmentContext context, @Nullable Predicate<T> selector ) {
        int totalWeight = 0;
        for( int i = 0; i < weights.length; i++ ) {
            final Entry<T> entry = UNDERLYING_LIST.get( i );
            if( selector == null || selector.test( entry.VALUE ) ) {
                totalWeight += weights[i] = entry.WEIGHT.getInt( context );
            }
        }
        return totalWeight;
    }
    
    /** Returns a random item from this weighted list. Null if none of the items have a positive weight. */
    @Nullable
    private T next( RandomSource random, int[] weights, int totalWeight ) {
        if( totalWeight <= 0 ) return null;
        
        // Now we pick a random value between zero and the total weight
        int targetWeight = random.nextInt( totalWeight );
        for( int i = 0; i < weights.length; i++ ) {
            targetWeight -= weights[i];
            if( targetWeight < 0 ) return UNDERLYING_LIST.get( i ).VALUE;
        }
        
        ConfigUtil.LOG.error( "Error for weighted list including {}:", UNDERLYING_LIST.get( 0 ).WEIGHT.base().describeLocation() );
        ConfigUtil.LOG.error( "Environment-sensitive weight list was unable to return a value when it should have! " +
                "This is probably due to error in floating point calculations, perhaps try changing the scale of weights." );
        return null;
    }
    
    private record Entry<T>( T VALUE, IntField.EnvironmentSensitive WEIGHT ) {}
}