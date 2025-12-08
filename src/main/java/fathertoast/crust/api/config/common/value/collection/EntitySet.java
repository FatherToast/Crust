package fathertoast.crust.api.config.common.value.collection;


import fathertoast.crust.api.config.common.value.collection.key.EntityKey;
import fathertoast.crust.api.config.common.value.collection.key.FuzzyKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

/**
 * A fuzzy set used to match entities.
 *
 * @see EntityKey
 * @see fathertoast.crust.api.config.common.field.collection.EntitySetField
 * @see EntityMap EntityMap - A similar collection that allows values
 */
@ApiStatus.Experimental
public class EntitySet extends FuzzySet<Entity> {
    
    /** Constructs an empty set. Use this if you want to {@link #load} a set from file/NBT. */
    public EntitySet() { super( EntityKey.PARSER ); }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link EntitySet.Builder} is much easier.
     */
    @SafeVarargs
    public EntitySet( FuzzyKey<Entity>... keys ) { super( EntityKey.PARSER, keys ); }
    
    /**
     * Constructs a set containing the keys provided. You may use this for creating default values
     * during config definition, however the {@link EntitySet.Builder} is much easier.
     */
    public EntitySet( Collection<FuzzyKey<Entity>> keys ) { super( EntityKey.PARSER, keys ); }
    
    /** @return A fresh, empty collection of the same type as this one. */
    @Override
    public EntitySet makeNew() { return new EntitySet(); }
    
    
    // ---- Builder Implementation ---- //
    
    /** Builder to make constructing entity sets smoother. */
    @ApiStatus.Experimental
    public static class Builder<B extends Builder<B>> extends AbstractBuilder<Entity, EntitySet, B> {
        
        /** @return A new fuzzy set reflecting the current state of this builder. */
        @Override
        public EntitySet build() { return new EntitySet( list ); }
        
        
        // ---- Extends Keys ---- //
        
        /** Adds an extends key based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B addExtends( String resLoc ) { return add( EntityKey.extending( resLoc, false ) ); }
        
        /** Adds a blacklist extends key based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B addExtendsBlacklist( String resLoc ) { return add( EntityKey.extending( resLoc, true ) ); }
        
        /** Adds an extends key based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B addExtends( ResourceLocation resLoc ) { return add( EntityKey.extending( resLoc, false ) ); }
        
        /** Adds a blacklist extends key based on the resource location. Matches the provided entity type and any entities that extend its class. */
        public B addExtendsBlacklist( ResourceLocation resLoc ) { return add( EntityKey.extending( resLoc, true ) ); }
        
        /** Adds an extends key based on the registry object. Matches the provided entity type and any entities that extend its class. */
        public B addExtends( RegistryObject<? extends EntityType<?>> regObj ) { return add( EntityKey.extending( regObj, false ) ); }
        
        /** Adds a blacklist extends key based on the registry object. Matches the provided entity type and any entities that extend its class. */
        public B addExtendsBlacklist( RegistryObject<? extends EntityType<?>> regObj ) { return add( EntityKey.extending( regObj, true ) ); }
        
        /** Adds an extends key based on the resource key. Matches the provided entity type and any entities that extend its class. */
        public B addExtends( ResourceKey<? extends EntityType<?>> resKey ) { return add( EntityKey.extending( resKey, false ) ); }
        
        /** Adds a blacklist extends key based on the resource key. Matches the provided entity type and any entities that extend its class. */
        public B addExtendsBlacklist( ResourceKey<? extends EntityType<?>> resKey ) { return add( EntityKey.extending( resKey, true ) ); }
        
        /** Adds an extends key based on the registered object. Only suitable for vanilla stuff. Matches the provided entity type and any entities that extend its class. */
        public B addExtends( EntityType<?> obj ) { return add( EntityKey.extending( obj, false ) ); }
        
        /** Adds a blacklist extends key based on the registered object. Only suitable for vanilla stuff. Matches the provided entity type and any entities that extend its class. */
        public B addExtendsBlacklist( EntityType<?> obj ) { return add( EntityKey.extending( obj, true ) ); }
        
        
        // ---- Basic Keys ---- //
        
        /** Adds a key based on the resource location. Matches only the provided entity type. */
        public B add( String resLoc ) { return add( EntityKey.of( resLoc, false ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided entity type. */
        public B addBlacklist( String resLoc ) { return add( EntityKey.of( resLoc, true ) ); }
        
        /** Adds a key based on the resource location. Matches only the provided entity type. */
        public B add( ResourceLocation resLoc ) { return add( EntityKey.of( resLoc, false ) ); }
        
        /** Adds a blacklist key based on the resource location. Matches only the provided entity type. */
        public B addBlacklist( ResourceLocation resLoc ) { return add( EntityKey.of( resLoc, true ) ); }
        
        /** Adds a key based on the registry object. Matches only the provided entity type. */
        public B add( RegistryObject<? extends EntityType<?>> regObj ) { return add( EntityKey.of( regObj, false ) ); }
        
        /** Adds a blacklist key based on the registry object. Matches only the provided entity type. */
        public B addBlacklist( RegistryObject<? extends EntityType<?>> regObj ) { return add( EntityKey.of( regObj, true ) ); }
        
        /** Adds a key based on the resource key. Matches only the provided entity type. */
        public B add( ResourceKey<? extends EntityType<?>> resKey ) { return add( EntityKey.of( resKey, false ) ); }
        
        /** Adds a blacklist key based on the resource key. Matches only the provided entity type. */
        public B addBlacklist( ResourceKey<? extends EntityType<?>> resKey ) { return add( EntityKey.of( resKey, true ) ); }
        
        /** Adds a key based on the registered object. Only suitable for vanilla stuff. Matches only the provided entity type. */
        public B add( EntityType<?> obj ) { return add( EntityKey.of( obj, false ) ); }
        
        /** Adds a blacklist key based on the registered object. Only suitable for vanilla stuff. Matches only the provided entity type. */
        public B addBlacklist( EntityType<?> obj ) { return add( EntityKey.of( obj, true ) ); }
        
        
        // ---- Wildcard Keys ---- //
        
        /** Adds a wildcard key based on the partial resource location. Matches every entity type in the namespace that starts with the partial path. */
        public B addWildcard( ResourceLocation partialResLoc ) { return add( EntityKey.ofWildcard( partialResLoc, false ) ); }
        
        /** Adds a blacklist wildcard key based on the partial resource location. Matches every entity type in the namespace that starts with the partial path. */
        public B addWildcardBlacklist( ResourceLocation partialResLoc ) { return add( EntityKey.ofWildcard( partialResLoc, true ) ); }
        
        /** Adds a wildcard key based on the namespace. Matches every entity type in the namespace. */
        public B addWildcard( String namespace ) { return add( EntityKey.ofWildcard( namespace, false ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace. Matches every entity type in the namespace. */
        public B addWildcardBlacklist( String namespace ) { return add( EntityKey.ofWildcard( namespace, true ) ); }
        
        /** Adds a wildcard key based on the namespace and partial path. Matches every entity type in the namespace that starts with the partial path. */
        public B addWildcard( String namespace, String partialPath ) { return add( EntityKey.ofWildcard( namespace, partialPath, false ) ); }
        
        /** Adds a blacklist wildcard key based on the namespace and partial path. Matches every entity type in the namespace that starts with the partial path. */
        public B addWildcardBlacklist( String namespace, String partialPath ) { return add( EntityKey.ofWildcard( namespace, partialPath, true ) ); }
        
        
        // ---- Tag Keys ---- //
        
        /** Adds a tag key based on the resource location. Matches every entity type in the tag. */
        public B addTag( String resLoc ) { return add( EntityKey.ofTag( resLoc, false ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every entity type in the tag. */
        public B addTagBlacklist( String resLoc ) { return add( EntityKey.ofTag( resLoc, true ) ); }
        
        /** Adds a tag key based on the resource location. Matches every entity type in the tag. */
        public B addTag( ResourceLocation resLoc ) { return add( EntityKey.ofTag( resLoc, false ) ); }
        
        /** Adds a blacklist tag key based on the resource location. Matches every entity type in the tag. */
        public B addTagBlacklist( ResourceLocation resLoc ) { return add( EntityKey.ofTag( resLoc, true ) ); }
        
        /** Adds a tag key based on the tag. Matches every entity type in the tag. */
        public B addTag( TagKey<EntityType<?>> tag ) { return add( EntityKey.ofTag( tag, false ) ); }
        
        /** Adds a blacklist tag key based on the tag. Matches every entity type in the tag. */
        public B addTagBlacklist( TagKey<EntityType<?>> tag ) { return add( EntityKey.ofTag( tag, true ) ); }
    }
}