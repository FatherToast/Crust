package fathertoast.crust.api.lib.number;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An iterator that iterates through every number from the specified minimum
 * and maximum (both inclusive). To avoid conversion loss, the minimum and maximum value
 * are converted and stored as longs.
 * <br><br>
 * Floats and doubles are treated as integers while iterating, meaning
 * the number's fraction is effectively ignored.
 */
public class RangedNumberIterator<T extends Number> implements Iterator<T> {
    
    /** The number type of this iterator. */
    private final NumberType type;
    /** The maximum value in this iterator's range. */
    private final long max;
    
    /** The current value the iterator is at. */
    private long current;
    
    
    /**
     * Creates a new instance.
     * <br><br>
     * It is the callers responsibility to ensure a valid
     * range is specified (min cannot exceed max).
     */
    public RangedNumberIterator( NumberType type, T min, T max ) {
        this.type = Objects.requireNonNull( type );
        this.max = max.longValue();
        current = min.longValue();
        
        if( current > this.max ) {
            throw new IllegalArgumentException( "Invalid range for iterator; min cannot exceed max value!" );
        }
    }
    
    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return current < max;
    }
    
    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public T next() {
        ++current;
        
        return switch( type ) {
            case BYTE -> (T) Byte.valueOf( (byte) current );
            case SHORT -> (T) Short.valueOf( (short) current );
            case INT -> (T) Integer.valueOf( (int) current );
            case LONG -> (T) Long.valueOf( current );
            case FLOAT -> (T) Float.valueOf( (float) current );
            case DOUBLE -> (T) Double.valueOf( (double) current );
        };
    }
}