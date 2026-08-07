package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.EntityKey;
import fathertoast.crust.api.config.common.value.collection.value.FuzzyEntry;
import fathertoast.crust.api.config.common.value.collection.value.IValueCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;

/**
 * A fuzzy map used to associate values with entities.
 *
 * @param <V> The value type.
 * @see EntityKey
 * @see IValueCodec
 * @see fathertoast.crust.api.config.common.field.collection.EntityMapField
 * @see EntitySet EntitySet - A similar collection that does not allow values
 */
@SuppressWarnings( "unused" )
public class EntityMap<V> extends FuzzyMap<Entity, V> {
    
    /** Constructs an empty map. Use this if you want to {@link #load} a map from file/NBT. */
    public EntityMap( IValueCodec<V> codec ) { super( EntityKey.PARSER, codec ); }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link EntityMap.Builder} is much easier.
     */
    @SafeVarargs
    public EntityMap( IValueCodec<V> codec, FuzzyEntry<Entity, V>... keys ) {
        super( EntityKey.PARSER, codec, keys );
    }
    
    /**
     * Constructs a map containing the entries provided. You may use this for creating default values
     * during config definition, however the {@link EntityMap.Builder} is much easier.
     */
    public EntityMap( IValueCodec<V> codec, Collection<FuzzyEntry<Entity, V>> keys ) {
        super( EntityKey.PARSER, codec, keys );
    }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public EntityMap<V> makeNew() { return new EntityMap<>( valueCodec ); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing entity maps smoother. */
    public static class Builder<V, B extends Builder<V, B>> extends AbstractBuilder<Entity, V, EntityMap<V>, B> {
        
        public Builder( IValueCodec<V> codec ) { super( codec ); }
        
        /** @return A new fuzzy map reflecting the current state of this builder. */
        @Override
        public EntityMap<V> build() { return new EntityMap<>( valueCodec, list ); }
        
        
        // ---- Extends Keys ---- //
        
        /** Adds an extends key-value pair based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B putExtends( String resLoc, V value ) { return put( EntityKey.extending( resLoc, false ), value ); }
        
        /** Adds an extends key-value pair based on the resource location. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtends( String resLoc, int hierarchySteps, V value ) { return put( EntityKey.extending( resLoc, hierarchySteps, false ), value ); }
        
        /** Adds an extends key-value pair based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B putExtends( ResourceLocation resLoc, V value ) { return put( EntityKey.extending( resLoc, false ), value ); }
        
        /** Adds an extends key-value pair based on the resource location. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter */
        public B putExtends( ResourceLocation resLoc, int hierarchySteps, V value ) { return put( EntityKey.extending( resLoc, hierarchySteps, false ), value ); }
        
        /** Adds an extends key-value pair based on the registry object. Matches the provided entity type and any entities that extend its class. */
        public B putExtends( RegistryObject<? extends EntityType<?>> regObj, V value ) { return put( EntityKey.extending( regObj, false ), value ); }
        
        /** Adds an extends key-value pair based on the registry object. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtends( RegistryObject<? extends EntityType<?>> regObj, int hierarchySteps, V value ) { return put( EntityKey.extending( regObj, hierarchySteps, false ), value ); }
        
        /** Adds an extends key-value pair based on the resource key. Matches the provided entity type and any entities that extend its class. */
        public B putExtends( ResourceKey<? extends EntityType<?>> resKey, V value ) { return put( EntityKey.extending( resKey, false ), value ); }
        
        /** Adds an extends key-value pair based on the resource key. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtends( ResourceKey<? extends EntityType<?>> resKey, int hierarchySteps, V value ) { return put( EntityKey.extending( resKey, hierarchySteps, false ), value ); }
        
        /** Adds an extends key-value pair based on the registered object. Only suitable for vanilla stuff. Matches the provided entity type and any entities that extend its class. */
        public B putExtends( EntityType<?> obj, V value ) { return put( EntityKey.extending( obj, false ), value ); }
        
        /** Adds an extends key-value pair based on the registered object. Only suitable for vanilla stuff. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtends( EntityType<?> obj, int hierarchySteps, V value ) { return put( EntityKey.extending( obj, hierarchySteps, false ), value ); }
        
        /** Adds a blacklist extends key based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B putExtendsBlacklist( String resLoc ) { return putBlacklist( EntityKey.extending( resLoc, true ) ); }
        
        /** Adds a blacklist extends key based on the resource location. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtendsBlacklist( String resLoc, int hierarchySteps ) { return putBlacklist( EntityKey.extending( resLoc, hierarchySteps, true ) ); }
        
        /** Adds a blacklist extends key based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B putExtendsBlacklist( ResourceLocation resLoc ) { return putBlacklist( EntityKey.extending( resLoc, true ) ); }
        
        /** Adds a blacklist extends key based on the resource location. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtendsBlacklist( ResourceLocation resLoc, int hierarchySteps ) { return putBlacklist( EntityKey.extending( resLoc, hierarchySteps, true ) ); }
        
        /** Adds a blacklist extends key based on the registry object. Matches the provided entity type and any entities that extend its class. */
        public B putExtendsBlacklist( RegistryObject<? extends EntityType<?>> regObj ) { return putBlacklist( EntityKey.extending( regObj, true ) ); }
        
        /** Adds a blacklist extends key based on the registry object. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtendsBlacklist( RegistryObject<? extends EntityType<?>> regObj, int hierarchySteps ) { return putBlacklist( EntityKey.extending( regObj, hierarchySteps, true ) ); }
        
        /** Adds a blacklist extends key based on the resource key. Matches the provided entity type and any entities that extend its class. */
        public B putExtendsBlacklist( ResourceKey<? extends EntityType<?>> resKey ) { return putBlacklist( EntityKey.extending( resKey, true ) ); }
        
        /** Adds a blacklist extends key based on the resource key. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter */
        public B putExtendsBlacklist( ResourceKey<? extends EntityType<?>> resKey, int hierarchySteps ) { return putBlacklist( EntityKey.extending( resKey, hierarchySteps, true ) ); }
        
        /** Adds a blacklist extends key based on the registered object. Only suitable for vanilla stuff. Matches the provided entity type and any entities that extend its class. */
        public B putExtendsBlacklist( EntityType<?> obj ) { return putBlacklist( EntityKey.extending( obj, true ) ); }
        
        /** Adds a blacklist extends key based on the registered object. Only suitable for vanilla stuff. Matches the provided entity type and any entities that extend the superclass X steps up in the hierarchy as specified by the {@code hierarchySteps} parameter. */
        public B putExtendsBlacklist( EntityType<?> obj, int hierarchySteps ) { return putBlacklist( EntityKey.extending( obj, hierarchySteps, true ) ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key-value pair based on the resource location. Matches only the provided entity type. */
        public B put( String resLoc, V value ) { return put( EntityKey.of( resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the resource location. Matches only the provided entity type. */
        public B put( ResourceLocation resLoc, V value ) { return put( EntityKey.of( resLoc, false ), value ); }
        
        /** Adds a key-value pair based on the registry object. Matches only the provided entity type. */
        public B put( RegistryObject<? extends EntityType<?>> regObj, V value ) { return put( EntityKey.of( regObj, false ), value ); }
        
        /** Adds a key-value pair based on the resource key. Matches only the provided entity type. */
        public B put( ResourceKey<? extends EntityType<?>> resKey, V value ) { return put( EntityKey.of( resKey, false ), value ); }
        
        /** Adds a key-value pair based on the registered object. Only suitable for vanilla stuff. Matches only the provided entity type. */
        public B put( EntityType<?> obj, V value ) { return put( EntityKey.of( obj, false ), value ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided entity type. */
        public B putBlacklist( String resLoc ) { return putBlacklist( EntityKey.of( resLoc, true ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided entity type. */
        public B putBlacklist( ResourceLocation resLoc ) { return putBlacklist( EntityKey.of( resLoc, true ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided entity type. */
        public B putBlacklist( RegistryObject<? extends EntityType<?>> regObj ) { return putBlacklist( EntityKey.of( regObj, true ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided entity type. */
        public B putBlacklist( ResourceKey<? extends EntityType<?>> resKey ) { return putBlacklist( EntityKey.of( resKey, true ) ); }
        
        /** Adds a blacklist key based on the registered object. Only suitable for vanilla stuff. Matches only the provided entity type. */
        public B putBlacklist( EntityType<?> obj ) { return putBlacklist( EntityKey.of( obj, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key-value pair based on the partial resource location. Matches every entity type in the namespace that starts with the partial path. */
        public B putWildcard( ResourceLocation partialResLoc, V value ) { return put( EntityKey.ofWildcard( partialResLoc, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace. Matches every entity type in the namespace. */
        public B putWildcard( String namespace, V value ) { return put( EntityKey.ofWildcard( namespace, false ), value ); }
        
        /** Adds a wildcard key-value pair based on the namespace and partial path. Matches every entity type in the namespace that starts with the partial path. */
        public B putWildcard( String namespace, String partialPath, V value ) { return put( EntityKey.ofWildcard( namespace, partialPath, false ), value ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every entity type in the namespace that starts with the partial path. */
        public B putWildcardBlacklist( ResourceLocation partialResLoc ) { return putBlacklist( EntityKey.ofWildcard( partialResLoc, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every entity type in the namespace. */
        public B putWildcardBlacklist( String namespace ) { return putBlacklist( EntityKey.ofWildcard( namespace, true ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every entity type in the namespace that starts with the partial path. */
        public B putWildcardBlacklist( String namespace, String partialPath ) { return putBlacklist( EntityKey.ofWildcard( namespace, partialPath, true ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key-value pair based on the resource location. Matches every entity type in the tag. */
        public B putTag( String resLoc, V value ) { return put( EntityKey.ofTag( resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the resource location. Matches every entity type in the tag. */
        public B putTag( ResourceLocation resLoc, V value ) { return put( EntityKey.ofTag( resLoc, false ), value ); }
        
        /** Adds a tag key-value pair based on the tag. Matches every entity type in the tag. */
        public B putTag( TagKey<EntityType<?>> tag, V value ) { return put( EntityKey.ofTag( tag, false ), value ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every entity type in the tag. */
        public B putTagBlacklist( String resLoc ) { return putBlacklist( EntityKey.ofTag( resLoc, true ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every entity type in the tag. */
        public B putTagBlacklist( ResourceLocation resLoc ) { return putBlacklist( EntityKey.ofTag( resLoc, true ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every entity type in the tag. */
        public B putTagBlacklist( TagKey<EntityType<?>> tag ) { return putBlacklist( EntityKey.ofTag( tag, true ) ); }
    }
}