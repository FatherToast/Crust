package fathertoast.crust.api.config.common.value.collection;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.value.collection.key.*;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * An ordered set of entries represented in files by a string array. The primary way
 * to use this is by iterating through its {@link #entries()}.
 * <p>
 * Fuzzy lists are intended to allow users to define a list of things that should be iterated through
 * to do something for each. The main benefit of this is just so you don't have to make your own
 * custom parser for list types that already have a fuzzy key parser.
 * <p>
 * This implementation is semi-fixed to protect against inadvertent modification, but allows
 * direct {@link #load} operations to make it easier to use in non-config applications (e.g., NBT).
 *
 * @param <T> The type of list.
 * @see FuzzyKey
 * @see IFuzzyKeyParser
 * @see fathertoast.crust.api.config.common.field.collection.FuzzyListField
 * @see FuzzyValueList FuzzyValueList - A similar collection that allows values
 */
@ApiStatus.Experimental
public class FuzzyList<T> extends AbstractFuzzyCollection<T, FuzzyKey<T>> {
    
    /** Constructs an empty list. Use this if you want to {@link #load} a set from file/NBT. */
    public FuzzyList( IFuzzyKeyParser<T> parser ) { super( parser ); }
    
    /** Constructs a list containing the keys provided. Use this for creating default values during config definition. */
    @SafeVarargs
    public FuzzyList( IFuzzyKeyParser<T> parser, FuzzyKey<T>... keys ) { super( parser, keys ); }
    
    /** Constructs a list containing the keys provided. Use this for creating default values during config definition. */
    public FuzzyList( IFuzzyKeyParser<T> parser, Collection<? extends FuzzyKey<T>> keys ) { super( parser, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public FuzzyList<T> makeNew() { return new FuzzyList<>( keyParser ); }
    
    
    /** @return How this fuzzy collection intends to use its keys. */
    @Override
    public KeyUsage keyUsage() { return KeyUsage.ITERATE; }
    
    /** @return The freshly loaded entry, or null if the line should be deleted. */
    @Override
    @Nullable
    public FuzzyKey<T> loadLine( @Nullable AbstractConfigField field, String line ) {
        return keyUsage().ifAllowed( FuzzyKey.parseLine( keyParser, field, line ) );
    }
    
    
    /**
     * @return An iterator over the objects represented by the keys in this list that can be used in an
     * enhanced for loop. The iterator skips over null objects, but it may still return null in some cases.
     */
    public KeyIterator<T> entries() { return new KeyIterator<>( this ); }
    
    
    /** Boilerplate builder class for fuzzy lists. */
    @ApiStatus.Experimental
    public static abstract class AbstractBuilder<T, C extends FuzzyList<T>, B extends AbstractBuilder<T, C, B>>
            extends AbstractFuzzyCollection.AbstractBuilder<T, FuzzyKey<T>, C, B> {
        
        /** Adds a pre-constructed key. */
        @Override
        public B add( FuzzyKey<T> key ) {
            if( KeyUsage.ITERATE.allowsKey( key ) ) return super.add( key );
            throw new IllegalArgumentException( "Key type not allowed for this usage! " + key );
        }
    }
    
    /** Builder class for a generic fuzzy list. */
    @ApiStatus.Experimental
    public static class Builder<T, B extends Builder<T, B>> extends AbstractBuilder<T, FuzzyList<T>, B> {
        
        public final IFuzzyKeyParser<T> keyParser;
        
        public Builder( IFuzzyKeyParser<T> parser ) { keyParser = parser; }
        
        /** @return A new fuzzy list reflecting the current state of this builder. */
        @Override
        public FuzzyList<T> build() { return new FuzzyList<>( keyParser, list ); }
        
        /** Adds a parsed key. */
        public B add( String key ) { return add( Objects.requireNonNull( keyParser.parseKeyString( null, key, key, false ) ) ); }
    }
    
    /** Builder class for a fuzzy string list. */
    @ApiStatus.Experimental
    public static class StrBuilder extends Builder<String, StrBuilder> {
        
        public StrBuilder() { super( StringKey.PARSER ); }
        
        /** @return A new fuzzy list reflecting the current state of this builder. */
        @Override
        public FuzzyList<String> build() { return new FuzzyList<>( keyParser, list ); }
    }
    
    
    /** A simple iterator over the objects represented by the keys, rather than over the keys themselves. */
    public static final class KeyIterator<T> implements Iterator<T>, Iterable<T> {
        
        private final Iterator<FuzzyKey<T>> keyIterator;
        
        private Iterator<T> subIterator;
        
        private KeyIterator( FuzzyList<T> list ) { keyIterator = list.getList().listIterator(); }
        
        @Override // Iterable
        public Iterator<T> iterator() { return this; }
        
        @Override
        public boolean hasNext() { return keyIterator.hasNext() || subIterator != null && subIterator.hasNext(); }
        
        @Override
        @Nullable
        public T next() {
            // Use the sub-iterator, if one is active
            if( subIterator != null ) {
                if( subIterator.hasNext() ) return subIterator.next();
                subIterator = null;
            }
            // Otherwise, churn until we hit something
            do {
                FuzzyKey<T> key = keyIterator.next();
                // See if we should open a new sub-iterator
                if( key instanceof IMultiKey<?> ) {
                    @SuppressWarnings( "unchecked" )
                    Iterator<T> sub = ((IMultiKey<T>) key).getValueIterator();
                    if( sub != null && sub.hasNext() ) {
                        subIterator = sub;
                        return sub.next();
                    }
                }
                // Otherwise, assume it's a reverse key
                T t = tryCast( key );
                if( t != null ) return t;
            }
            while( hasNext() );
            return null;
        }
        
        @Nullable
        private T tryCast( FuzzyKey<T> key ) {
            try {
                //noinspection unchecked
                return ((IReverseKey<T>) key).asValue();
            }
            catch( ClassCastException ex ) {
                ConfigUtil.LOG.error( "Somehow, an invalid iteration key was iterated! Entry: \"{}\", Fuzzy list: {}",
                        key, this, ex );
            }
            return null;
        }
    }
}