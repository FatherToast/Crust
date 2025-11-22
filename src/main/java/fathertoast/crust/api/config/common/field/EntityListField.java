package fathertoast.crust.api.config.common.field;

import fathertoast.crust.api.config.client.gui.widget.provider.IConfigFieldWidgetProvider;
import fathertoast.crust.api.config.client.gui.widget.provider.StringListFieldWidgetProvider;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a config field with an entity list value.
 */
@SuppressWarnings( "unused" )
public class EntityListField extends GenericField<EntityList> implements IStringListScreenEditable {
    
    /** Provides a detailed description of how to use entity lists. Recommended to put at the top of any file using entity lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Entity List fields: General format = [ \"namespace:entity_type value1 value2 ...\", ... ]" );
        comment.add( "  Entity lists are arrays of entity types. Some entity lists specify a number of values linked " +
                "to each entity type." );
        comment.add( "  Entity types are defined by their key in the entity registry, usually following the pattern " +
                "'namespace:entity_name'." );
        comment.add( "  '" + DefaultValueEntry.KEY_DEFAULT + "' can be used instead of an entity type registry key to provide " +
                "default values for all entities." );
        comment.add( "  List entries by default match any entity type derived from (i.e. based on) their entity type. " +
                "For example, '~minecraft:zombie'." );
        comment.add( "    There is no steadfast rule about extending, even in vanilla, but the hope is that mod-added " +
                "mobs will extend their base mob." );
        comment.add( "  An asterisk '*' can be used to match all registry entries belonging to X namespace. For example, 'minecraft:*' will " +
                "match all vanilla entries." );
        comment.add( "  Entity type tags can also be used here. To declare a tag, start with a '#' followed by the rest of the tag path." );
        comment.add( "  Tag example: '#minecraft:beehive_inhabitors'" );
        comment.add( "      Priority order: specific entries > tag entries > namespace entries > default" );
        return comment;
    }
    
    /** Creates a new field. */
    public EntityListField( String key, EntityList defaultValue, @Nullable String... description ) {
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
            fieldFormat = "[ \"namespace:entity_type value1 value2 ...\", ... ]";
        }
        else {
            // Specific number of values
            StringBuilder format = new StringBuilder( "[ \"namespace:entity_type " );
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
        comment.add( TomlHelper.fieldInfoFormat( "Entity List", valueDefault, fieldFormat ) );
        
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
        
        if( raw instanceof EntityList ) {
            value = (EntityList) raw;
        }
        else {
            value = parse( TomlHelper.parseStringList( raw ) );
        }
    }
    
    private EntityList parse( List<String> list ) {
        final List<EntityEntry> entryList = new ArrayList<>();
        final List<EntityTagEntry> tagEntries = new ArrayList<>();
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
            if( args[0].endsWith( "*" ) ) {
                NamespaceRegistryEntry entry = parseNamespaceEntry( line );
                if( entry != null ) namespaceEntries.add( entry );
            }
            // Check for entity type tags
            else if( line.startsWith( "#" ) ) {
                EntityTagEntry entry = parseTagEntry( line );
                if( entry != null ) tagEntries.add( entry );
            }
            // Try parse as normal entry
            else {
                EntityEntry entry = parseEntry( line );
                if( entry != null ) entryList.add( entry );
            }
        }
        
        final EntityList entityList = new EntityList( defaultEntry, entryList );
        entityList.addNamespaceEntries( namespaceEntries );
        entityList.addTagEntries( tagEntries );
        return entityList;
    }
    
    /** Parses a single entry line and returns the result. */
    @Nullable
    private EntityEntry parseEntry( final String line ) {
        String modifiedLine = line;
        
        // Check if the entry should be "specific", i.e. check for entity class equality rather than instanceof
        final boolean extendable;
        if( line.startsWith( "~" ) ) {
            modifiedLine = line.substring( 1 );
            extendable = false;
        }
        else {
            extendable = true;
        }
        
        // Parse the entity-value array
        final String[] args = modifiedLine.split( " " );
        final ResourceLocation regKey = ResourceLocation.tryParse( args[0].trim() );
        if( regKey == null ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Entity entry has invalid resource location! Skipping. Entry: {}", line );
            return null;
        }
        double[] values = parseValues( line, args );
        
        return new EntityEntry( this, regKey, extendable, values );
    }
    
    /** Parses a single entry line as a tag entry and returns it. */
    @Nullable
    private EntityTagEntry parseTagEntry( String line ) {
        String[] args = line.split( " " );
        String tag = args[0].substring( 1 );
        
        ResourceLocation tagLocation = ResourceLocation.tryParse( tag );
        if( tagLocation == null ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Entity entry has invalid tag key! Skipping. Entry: {}", line );
            return null;
        }
        double[] values = parseValues( line, args );
        
        return new EntityTagEntry( this, TagKey.create( Registries.ENTITY_TYPE, tagLocation ), values );
    }
    
    /**
     * Attempts to fetch every entity type from the registry belonging to
     * a specific namespace and adds new entries for them to the given entry list.
     */
    @Nullable
    private NamespaceRegistryEntry parseNamespaceEntry( String line ) {
        String[] args = line.split( " " );
        String namespace = args[0].split( ":" )[0];
        
        if( namespace == null || namespace.isEmpty() ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Entity entry has invalid namespace key! Must follow pattern \"namespace:*\". Skipping. Entry: {}",
                    line );
            return null;
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
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Entity entry has too few values! Must have at least one value. Replacing missing value with 0. Entry: {}",
                        line );
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
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Entity entry has too few values! Expected {} values, but found {}. Replacing missing values with 0. Entry: {}",
                        reqValues, actualValues, line );
            }
            else if( reqValues < actualValues ) {
                ConfigUtil.warnFor( this );
                ConfigUtil.LOG.warn( "Entity entry has too many values! Expected {} values, but found {}. Deleting excess values. Entry: {}",
                        reqValues, actualValues, line );
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
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Entity entry has invalid value ({})! Falling back to 0. Entry: {}",
                    arg, line );
            value = 0.0;
        }
        // Verify value is within range
        if( value < valueDefault.getMinValue() ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Entity entry value is below the minimum! Adjusting from {} to {}. Entry: {}",
                    value, valueDefault.getMinValue(), line );
            value = valueDefault.getMinValue();
        }
        else if( value > valueDefault.getMaxValue() ) {
            ConfigUtil.warnFor( this );
            ConfigUtil.LOG.warn( "Entity entry value is above the maximum! Adjusting from {} to {}. Entry: {}",
                    value, valueDefault.getMaxValue(), line );
            value = valueDefault.getMaxValue();
        }
        return value;
    }
    
    /** @return This field's gui component provider. */
    @Override
    public IConfigFieldWidgetProvider getWidgetProvider() { return new StringListFieldWidgetProvider<>( this ); }
    
    /** Converts the displayable string list to a field value. */
    @Override // IStringListScreenEditable
    public Object stringListToValue( List<String> value ) {
        return parse( value );
    }
    
    /** @return This field's line validator, or null if any string is allowed. */
    @Override // IStringListScreenEditable
    public Predicate<String> getLineValidator() {
        return null;//TODO
    }
    
    
    // Convenience methods
    
    /** @return True if the entity is contained in this list. */
    public boolean contains( @Nullable Entity entity ) { return get().contains( entity ); }
    
    /**
     * @param entity The entity to retrieve values for.
     * @return The array of values of the best-match entry. Returns null if the entity is not contained in this entity list.
     */
    @Nullable
    public double[] getValues( @Nullable Entity entity ) { return get().getValues( entity ); }
    
    /**
     * @param entity The entity to retrieve a value for.
     * @return The first value in the best-match entry's value array. Returns 0 if the entity is not contained in this
     * entity list or has no values specified. This should only be used for 'single value' lists.
     * @see EntityList#setSingleValue()
     * @see EntityList#setSinglePercent()
     */
    public double getValue( @Nullable Entity entity ) { return get().getValue( entity ); }
    
    /**
     * @param entity The entity to roll a value for.
     * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the entity
     * is not contained in this entity list or has no values specified. This should only be used for 'single percent' lists.
     * @see EntityList#setSinglePercent()
     */
    public boolean rollChance( @Nullable LivingEntity entity ) { return get().rollChance( entity ); }
    
    /**
     * Represents two entity list fields, a blacklist and a whitelist, combined into one.
     * The blacklist cannot contain values, but the whitelist can have any settings.
     */
    public static class Combined {
        
        /** The whitelist. To match, the entry must be present here. */
        private final EntityListField WHITELIST;
        /** The blacklist. Entries present here are ignored entirely. */
        private final EntityListField BLACKLIST;
        
        /** Links two lists together as blacklist and whitelist. */
        public Combined( EntityListField whitelist, EntityListField blacklist ) {
            WHITELIST = whitelist;
            BLACKLIST = blacklist;
            if( blacklist.valueDefault.getRequiredValues() != 0 ) {
                throw new IllegalArgumentException( "Blacklists cannot have values! See: " + blacklist.getKey() );
            }
        }
        
        
        // Convenience methods
        
        /** @return True if the entity is contained in this list. */
        public boolean contains( @Nullable Entity entity ) {
            return entity != null && !BLACKLIST.contains( entity ) && WHITELIST.contains( entity );
        }
        
        /**
         * @param entity The entity to retrieve values for.
         * @return The array of values of the best-match entry. Returns null if the entity is not contained in this entity list.
         */
        @Nullable
        public double[] getValues( @Nullable Entity entity ) {
            return entity != null && !BLACKLIST.contains( entity ) ? WHITELIST.getValues( entity ) : null;
        }
        
        /**
         * @param entity The entity to retrieve a value for.
         * @return The first value in the best-match entry's value array. Returns 0 if the entity is not contained in this
         * entity list or has no values specified. This should only be used for 'single value' lists.
         * @see EntityList#setSingleValue()
         * @see EntityList#setSinglePercent()
         */
        public double getValue( @Nullable Entity entity ) {
            return entity != null && !BLACKLIST.contains( entity ) ? WHITELIST.getValue( entity ) : 0.0;
        }
        
        /**
         * @param entity The entity to roll a value for.
         * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the entity
         * is not contained in this entity list or has no values specified. This should only be used for 'single percent' lists.
         * @see EntityList#setSinglePercent()
         */
        public boolean rollChance( @Nullable LivingEntity entity ) {
            return entity != null && !BLACKLIST.contains( entity ) && WHITELIST.rollChance( entity );
        }
    }
}