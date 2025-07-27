package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a config field with a registry entry-value list value.
 */
public class RegistryEntryValueListField<T> extends GenericField<RegistryEntryValueList<T>> {
    
    /** Provides a detailed description of how to use registry entry value lists. Recommended to put at the top of any file using them. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Registry entry-value list fields: General format = [ \"namespace:registry_name value1 value2 ...\", ... ]" );
        comment.add( "  Registry entry-value lists are arrays of registry keys. Many things in the game, such as blocks or " +
                "potions, are defined by their registry key within a registry. For example, all items are registered " +
                "in the \"minecraft:item\" registry." );
        comment.add( "  Registry entry-value lists may have one or multiple numeric values linked to each entry." );
        comment.add( "  '" + DefaultValueEntry.KEY_DEFAULT + "' can be used instead of a registry key to provide " +
                "default values." );
        comment.add( "  An asterisk '*' can be used to match all registry entries/keys belonging to X namespace. For example, 'minecraft:*' will " +
                "match all vanilla entries." );
        comment.add( "  Tags can also be used here. To declare a tag, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:oak_logs'" );
        comment.add( "      Priority order: specific entries > tag entries > namespace entries > default" );
        return comment;
    }
    
    /** Creates a new field. */
    public RegistryEntryValueListField( String key, RegistryEntryValueList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        // Number of values to include
        final int reqValues = valueDefault.getRequiredValues();
        final String fieldFormat;
        
        if( reqValues < 0 ) {
            // Variable number of values
            fieldFormat = "[ \"namespace:registry_name value1 value2 ...\", ... ]";
        }
        else {
            // Specific number of values
            StringBuilder format = new StringBuilder( "[ \"namespace:registry_name " );
            for( int i = 1; i <= reqValues; i++ ) {
                format.append( "value" );
                if( reqValues > 1 ) {
                    format.append( i );
                }
                format.append( " " );
            }
            format.deleteCharAt( format.length() - 1 ).append( "\", ... ]" );
            fieldFormat = format.toString();
        }
        comment.add( TomlHelper.fieldInfoFormat( "Registry Entry-value List", valueDefault, fieldFormat ) );
        comment.add( "   Target registry: " + valueDefault.getRegistry().get().getRegistryKey().toString() );
        
        // Range for values, if applicable
        if( reqValues != 0 ) {
            comment.add( "   Range for Values: " + TomlHelper.fieldRange( valueDefault.getMinValue(), valueDefault.getMaxValue() ) );
        }
    }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        
        if( raw instanceof RegistryEntryValueList<?> ) {
            try {
                value = (RegistryEntryValueList<T>) raw;
            }
            catch( Exception e ) {
                ConfigUtil.LOG.error( "Attempted to cast registry entry value list with wrong generics type!" );
            }
        }
        else {
            final List<String> list = TomlHelper.parseStringList( raw );
            final List<RegistryValueEntry<T>> entryList = new ArrayList<>();
            final List<RegistryValueTagEntry<T>> tagEntries = new ArrayList<>();
            final List<NamespaceRegistryEntry> namespaceEntries = new ArrayList<>();
            DefaultValueEntry defaultEntry = null;
            
            for( String line : list ) {
                String[] args = line.split( " " );
                
                // Check for default entry
                if( defaultEntry == null ) {
                    if( args[0].equals( "default" ) ) {
                        double[] values = parseValues( line, args );
                        defaultEntry = new DefaultValueEntry( values );
                        continue;
                    }
                }
                // Check for namespace entries
                if( line.split( " " )[0].endsWith( "*" ) ) {
                    namespaceEntries.add( parseNamespaceEntry( line ) );
                }
                // Check for entity type tags
                else if( line.startsWith( "#" ) ) {
                    tagEntries.add( parseTagEntry( line ) );
                }
                else {
                    entryList.add( parseEntry( line ) );
                }
            }
            value = new RegistryEntryValueList<>( defaultEntry, valueDefault.getRegistry(), entryList );
            value.addNamespaceEntries( namespaceEntries );
            value.addTagEntries( tagEntries );
        }
    }
    
    /** Parses a single entry line and returns the result. */
    private RegistryValueEntry<T> parseEntry( final String line ) {
        // Parse the entry-value array
        final String[] args = line.split( " " );
        final ResourceLocation regKey = ResourceLocation.parse( args[0].trim() );
        double[] values = parseValues( line, args );
        
        return new RegistryValueEntry<>( this, regKey, values );
    }
    
    /** Parses a single entry line as a tag entry and returns it. */
    private RegistryValueTagEntry<T> parseTagEntry( String line ) {
        String[] args = line.split( " " );
        String tag = args[0].substring( 1 );
        
        if( tag.isEmpty() ) {
            ConfigUtil.LOG.error( "Tried to parse tag key in RegistryEntryValueList \"{}\", but it was malformed! Expected the format \"#namespace:path\" but got \"{}\"!",
                    getKey(), line );
            
            throw new IllegalArgumentException();
        }
        ResourceLocation tagLocation = ResourceLocation.tryParse( tag );
        
        if( tagLocation == null ) {
            ConfigUtil.LOG.error( "Tried to parse entity tag in RegistryEntryValueList \"{}\", but it could not be read as a ResourceLocation! Expected the format \"#namespace:path\" but got \"{}\"!",
                    getKey(), line );
            
            throw new IllegalArgumentException();
        }
        double[] values = parseValues( line, args );
        
        return new RegistryValueTagEntry<>( this, new TagKey<>( valueDefault.getRegistry().get().getRegistryKey(), tagLocation ), values );
    }
    
    /**
     * Attempts to fetch every registry key from the registry belonging to
     * a specific namespace and adds new entries for them to the given entry list.
     *
     * @throws IllegalArgumentException if the first argument of the line doesn't contain a namespace
     */
    private NamespaceRegistryEntry parseNamespaceEntry( String line ) {
        String[] args = line.split( " " );
        String namespace = args[0].split( ":" )[0];
        
        if( namespace == null || namespace.isEmpty() ) {
            ConfigUtil.LOG.error( "Tried to parse namespace entry in RegistryEntryValueList \"{}\", but it was malformed! Expected the format \"namespace:*\" but got \"{}\"!",
                    getKey(), line );
            
            throw new IllegalArgumentException();
        }
        double[] values = parseValues( line, args );
        return new NamespaceRegistryEntry( this, namespace, values );
    }
    
    /**
     * Parses the value arguments and returns an array of values.
     */
    private double[] parseValues( String line, String[] args ) {
        final List<Double> valuesList = new ArrayList<>();
        final int reqValues = valueDefault.getRequiredValues();
        final int actualValues = args.length - 1;
        
        // Variable-value; just needs at least one value
        if( reqValues < 0 ) {
            if( actualValues < 1 ) {
                ConfigUtil.LOG.warn( "Entry has too few values for {} \"{}\"! Expected at least one value. " +
                                "Replacing missing value with 0. Invalid entry: {}",
                        getClass(), getKey(), line );
                valuesList.add( 0.0 );
            }
            else {
                // Parse all values
                for( int i = 1; i < args.length; i++ ) {
                    valuesList.add( parseValue( args[i], line ) );
                }
            }
        }
        // Specified value; must have the exact number of values
        else {
            if( reqValues > actualValues ) {
                ConfigUtil.LOG.warn( "Entry has too few values for {} \"{}\"! " +
                                "Expected {} values, but detected {}. Replacing missing values with 0. Invalid entry: {}",
                        getClass(), getKey(), reqValues, actualValues, line );
            }
            else if( reqValues < actualValues ) {
                ConfigUtil.LOG.warn( "Entry has too many values for {} \"{}\"! " +
                                "Expected {} values, but detected {}. Deleting additional values. Invalid entry: {}",
                        getClass(), getKey(), reqValues, actualValues, line );
            }
            
            // Parse all values
            for( int i = 1; i < reqValues + 1; i++ ) {
                if( i < args.length ) {
                    valuesList.add( parseValue( args[i], line ) );
                }
                else {
                    valuesList.add( 0.0 );
                }
            }
        }
        
        // Convert to array
        final double[] values = new double[valuesList.size()];
        for( int i = 0; i < values.length; i++ ) {
            values[i] = valuesList.get( i );
        }
        return values;
    }
    
    /** Parses a single value argument and returns a valid result. */
    private double parseValue( final String arg, final String line ) {
        // Try to parse the value
        double value;
        try {
            value = Double.parseDouble( arg );
        }
        catch( NumberFormatException ex ) {
            // This is thrown if the string is not a parsable number
            ConfigUtil.LOG.warn( "Invalid value for {} \"{}\"! Falling back to 0. Invalid entry: {}",
                    getClass(), getKey(), line );
            value = 0.0;
        }
        // Verify value is within range
        if( value < valueDefault.getMinValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is below the minimum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMinValue(), value );
            value = valueDefault.getMinValue();
        }
        else if( value > valueDefault.getMaxValue() ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is above the maximum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMaxValue(), value );
            value = valueDefault.getMaxValue();
        }
        return value;
    }
    
    
    // Convenience methods
    
    /** @return True if the registry object is contained in this list. */
    public boolean contains( @Nullable T regObject ) { return get().contains( regObject, null ); }
    
    /** @return True if the registry object is contained in this list. Allows checking tags. */
    public boolean contains( @Nullable T regObject, Predicate<TagKey<T>> tagCheck ) { return get().contains( regObject, tagCheck ); }
    
    /**
     * @param regObject The registry object to retrieve values for.
     * @return The array of values of the best-match entry. Returns null if the registry object is not contained in this entity list.
     */
    @Nullable
    public double[] getValues( @Nullable T regObject ) { return get().getValues( regObject, null ); }
    
    /**
     * @param regObject The registry object to retrieve values for. Allows checking tags.
     * @return The array of values of the best-match entry. Returns null if the registry object is not contained in this entity list.
     */
    @Nullable
    public double[] getValues( @Nullable T regObject, Predicate<TagKey<T>> tagCheck ) { return get().getValues( regObject, tagCheck ); }
    
    /**
     * @param regObject The registry object to retrieve a value for.
     * @return The first value in the best-match entry's value array. Returns 0 if the registry object is not contained in this
     * list or has no values specified. This should only be used for 'single value' lists.
     * @see RegistryEntryValueList#setSingleValue()
     * @see RegistryEntryValueList#setSinglePercent()
     */
    public double getValue( @Nullable T regObject ) { return get().getValue( regObject, null ); }
    
    /**
     * @param regObject The registry object to retrieve a value for.
     * @param tagCheck  Predicate for checking against tags in this list.
     * @return The first value in the best-match entry's value array. Returns 0 if the registry object is not contained in this
     * list or has no values specified. This should only be used for 'single value' lists.
     * @see RegistryEntryValueList#setSingleValue()
     * @see RegistryEntryValueList#setSinglePercent()
     */
    public double getValue( @Nullable T regObject, Predicate<TagKey<T>> tagCheck ) { return get().getValue( regObject, tagCheck ); }
    
    /**
     * @param regObject The registry object to roll a value for.
     * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the registry object
     * is not contained in this list or has no values specified. This should only be used for 'single percent' lists.
     * @see EntityList#setSinglePercent()
     */
    public boolean rollChance( @Nullable T regObject, RandomSource randomSource ) { return get().rollChance( regObject, randomSource, null ); }
    
    /**
     * @param regObject The registry object to roll a value for.
     * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the registry object
     * is not contained in this list or has no values specified. This should only be used for 'single percent' lists.
     * @see EntityList#setSinglePercent()
     */
    public boolean rollChance( @Nullable T regObject, RandomSource randomSource, Predicate<TagKey<T>> tagCheck ) { return get().rollChance( regObject, randomSource, tagCheck ); }
    
    /**
     * Represents two registry entry-value list fields, a blacklist and a whitelist, combined into one.
     * The blacklist cannot contain values, but the whitelist can have any settings.
     */
    public static class Combined<T> {
        
        /** The whitelist. To match, the entry must be present here. */
        private final RegistryEntryValueListField<T> WHITELIST;
        /** The blacklist. Entries present here are ignored entirely. */
        private final RegistryEntryValueListField<T> BLACKLIST;
        
        /** Links two lists together as blacklist and whitelist. */
        public Combined( RegistryEntryValueListField<T> whitelist, RegistryEntryValueListField<T> blacklist ) {
            WHITELIST = whitelist;
            BLACKLIST = blacklist;
            if( blacklist.valueDefault.getRequiredValues() != 0 ) {
                throw new IllegalArgumentException( "Blacklists cannot have values! See: " + blacklist.getKey() );
            }
        }
        
        
        // Convenience methods
        
        /** @return True if the registry object is contained in this list. */
        public boolean contains( @Nullable T regObject ) {
            return regObject != null && !BLACKLIST.contains( regObject ) && WHITELIST.contains( regObject );
        }
        
        /** @return True if the registry object is contained in this list. Allows checking tags. */
        public boolean contains( @Nullable T regObject, Predicate<TagKey<T>> tagCheck ) {
            return regObject != null && !BLACKLIST.contains( regObject, tagCheck ) && WHITELIST.contains( regObject, tagCheck );
        }
        
        /**
         * @param regObject The registry object to retrieve values for.
         * @return The array of values of the best-match entry. Returns null if the registry object is not contained in this list.
         */
        @Nullable
        public double[] getValues( @Nullable T regObject ) {
            return regObject != null && !BLACKLIST.contains( regObject ) ? WHITELIST.getValues( regObject ) : null;
        }
        
        /**
         * @param regObject The registry object to retrieve values for. Allows checking tags.
         * @param tagCheck  Predicate for checking against any tags in the list.
         * @return The array of values of the best-match entry. Returns null if the registry object is not contained in this list.
         */
        @Nullable
        public double[] getValues( @Nullable T regObject, Predicate<TagKey<T>> tagCheck ) {
            return regObject != null && !BLACKLIST.contains( regObject, tagCheck ) ? WHITELIST.getValues( regObject, tagCheck ) : null;
        }
        
        
        /**
         * @param regObject The registry object to retrieve a value for.
         * @return The first value in the best-match entry's value array. Returns 0 if the registry object is not contained in this
         * list or has no values specified. This should only be used for 'single value' lists.
         * @see RegistryEntryValueList#setSingleValue()
         * @see RegistryEntryValueList#setSinglePercent()
         */
        public double getValue( @Nullable T regObject ) {
            return regObject != null && !BLACKLIST.contains( regObject ) ? WHITELIST.getValue( regObject ) : 0.0;
        }
        
        /**
         * @param regObject The registry object to retrieve a value for.
         * @param tagCheck  Predicate for checking against any tags in the list.
         * @return The first value in the best-match entry's value array. Returns 0 if the registry object is not contained in this
         * list or has no values specified. This should only be used for 'single value' lists.
         * @see RegistryEntryValueList#setSingleValue()
         * @see RegistryEntryValueList#setSinglePercent()
         */
        public double getValue( @Nullable T regObject, Predicate<TagKey<T>> tagCheck ) {
            return regObject != null && !BLACKLIST.contains( regObject, tagCheck ) ? WHITELIST.getValue( regObject, tagCheck ) : 0.0;
        }
        
        /**
         * @param regObject The registry object to roll a value for.
         * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the registry object
         * is not contained in this list or has no values specified. This should only be used for 'single percent' lists.
         * @see RegistryEntryValueList#setSinglePercent()
         */
        public boolean rollChance( @Nullable T regObject, RandomSource randomSource ) {
            return regObject != null && !BLACKLIST.contains( regObject ) && WHITELIST.rollChance( regObject, randomSource );
        }
        
        /**
         * @param regObject The entity to roll a value for.
         * @param tagCheck  Predicate for checking against any tags in the list.
         * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the entity
         * is not contained in this entity list or has no values specified. This should only be used for 'single percent' lists.
         * @see RegistryEntryValueList#setSinglePercent()
         */
        public boolean rollChance( @Nullable T regObject, RandomSource randomSource, Predicate<TagKey<T>> tagCheck ) {
            return regObject != null && !BLACKLIST.contains( regObject, tagCheck ) && WHITELIST.rollChance( regObject, randomSource, tagCheck );
        }
    }
}