package fathertoast.crust.test.common;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.field.collection.*;
import fathertoast.crust.api.config.common.value.EnvironmentEntry;
import fathertoast.crust.api.config.common.value.EnvironmentList;
import fathertoast.crust.api.config.common.value.collection.*;
import fathertoast.crust.api.config.common.value.collection.key.IRegWrapper;
import fathertoast.crust.api.config.common.value.collection.key.NumberKey;
import fathertoast.crust.api.config.common.value.collection.key.ResourceLocKey;
import fathertoast.crust.api.config.common.value.collection.value.*;
import fathertoast.crust.api.config.common.value.environment.CrustEnvironmentRegistry;
import fathertoast.crust.api.config.common.value.environment.biome.BiomeCategory;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.api.util.BlockStatePropertyMap;
import fathertoast.crust.api.util.ResourceLocationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.*;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Set;

/**
 * File for configuring the in-game config editor button.
 */
public class TestConfigFile extends AbstractConfigFile {
    
    public final General GENERAL;
    public final Environment ENVIRONMENT;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    TestConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "Test config file." );
        
        GENERAL = new General( this );
        ENVIRONMENT = new Environment( this );
    }
    
    /**
     * Category for testing configs.
     */
    public static class General extends AbstractConfigCategory<TestConfigFile> {
        // Primitives
        public final BooleanField booleanField;
        public final IntField intField;
        public final LongField longField;
        public final ColorIntField colorIntField;
        public final ColorIntField colorIntFieldAlpha;
        public final DoubleField doubleField;
        public final ScaledDoubleField scaledDoubleField;
        public final SqrDoubleField sqrDoubleField;
        // Simple objects
        public final StringField stringField;
        public final EnumField<BiomeCategory> enumField;
        public final BlockStateField blockStateField;
        public final ItemStackField itemStackField;
        //        public final FuzzyKeyField<String> fuzzyKeyField;
        //        public final ValueCodecField<SoundInstanceStats> valueCodecField;
        public final RegObjectField<Block> blockRegObjectField;
        public final RegObjectField<Item> itemRegObjectField;
        public final RegObjectField<MobEffect> mobEffectRegObjectField;
        public final RegObjectField<EntityType<?>> entityTypeRegObjectField;
        public final RegObjectField<SoundEvent> soundRegObjectField;
        // Fuzzy collections
        public final FuzzyListField<Long, FuzzyList<Long>> fuzzyListField;
        public final FuzzySetField<ResourceLocation, FuzzySet<ResourceLocation>> fuzzySetField;
        public final FuzzyMapField<Double, String, FuzzyMap<Double, String>> fuzzyMapField;
        public final EntitySetField entitySetField;
        public final EntityMapField<Double[]> entityMapField;
        public final BlockStateSetField blockStateSetField;
        public final BlockStateMapField<BiomeCategory> blockStateMapField;
        public final BlockStateListField blockStateListField;
        public final BlockStateValueListField<Double> blockStateValueListField;
        public final BlockStateWeightedListField blockStateWeightedListField;
        public final BlockStateWeightedValueListField<Double> blockStateWeightedValueListField;
        public final RegistrySetField<EntityType<?>> registrySetField;
        public final RegistryMapField<EntityType<?>, Integer> registryMapField;
        public final RegistryListField<Instrument> registryListFieldVn;
        public final RegistryValueListField<DamageType, String> registryValueListFieldDn;
        public final RegistryWeightedListField<ConfiguredFeature<?, ?>> registryWeightedListField;
        public final RegistryWeightedValueListField<MobEffect, MobEffectStats> registryWeightedValueListField;
        public final NumberSetField<Integer> numberSetField;
        public final NumberListField<Double> numberListField;
        // Misc collections
        public final AttributeOpListField attributeOpListField;
        public final EnvironmentListField environmentListField;
        public final StringListField stringListField;
        public final PredicateStringListField predicateStringListField;
        // Misc tests
        public final BooleanField longCommentField;
        // Deprecated
        //        public final BlockListField blockListField;
        //        public final EntityListField entityListField;
        //        public final RegistryEntryListField<EntityType<?>> registryEntryListField;
        //        public final RegistryEntryValueListField<MobEffect> registryEntryValueListField;
        //        public final LazyRegistryEntryListField<MobEffect> lazyRegistryEntryListField;
        
        General( TestConfigFile parent ) {
            super( parent, "general", generateFormatTest() );
            
            SPEC.comment( generateFormatTest() );
            
            
            // Tester for each field type, by category
            
            // ---- Primitives ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            booleanField = SPEC.define( new InjectionWrapperField<>(
                    new BooleanField( "boolean", false ), General::testCallback ) ).field();
            
            intField = SPEC.define( new InjectionWrapperField<>(
                    new IntField( "int", 1, IntField.Range.ANY ), General::testCallback ) ).field();
            longField = SPEC.define( new InjectionWrapperField<>(
                    new LongField( "long", Long.MAX_VALUE, LongField.Range.POSITIVE ), General::testCallback ) ).field();
            colorIntField = SPEC.define( new InjectionWrapperField<>(
                    new ColorIntField( "color_int_rgb", 0x00FFFF, false ), General::testCallback ) ).field();
            colorIntFieldAlpha = SPEC.define( new InjectionWrapperField<>(
                    new ColorIntField( "color_int_argb", 0x77FF00FF, true ), General::testCallback ) ).field();
            
            doubleField = SPEC.define( new InjectionWrapperField<>(
                    new DoubleField( "double", 1.0, DoubleField.Range.ANY ), General::testCallback ) ).field();
            scaledDoubleField = SPEC.define( new InjectionWrapperField<>(
                    new ScaledDoubleField( "scaled_double", 1.0, 6.0, DoubleField.Range.ANY ), General::testCallback ) ).field();
            sqrDoubleField = SPEC.define( new InjectionWrapperField<>(
                    new SqrDoubleField( "sqr_double", 1.0, DoubleField.Range.ANY ), General::testCallback ) ).field();
            
            // ---- Simple objects ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            stringField = SPEC.define( new InjectionWrapperField<>(
                    new StringField( "string", "Test!",
                            ( value ) -> value.length() <= 5 ), General::testCallback ) ).field();
            
            enumField = SPEC.define( new InjectionWrapperField<>(
                    new EnumField<>( "enum", BiomeCategory.NONE ), General::testCallback ) ).field();
            
            blockStateField = SPEC.define( new InjectionWrapperField<>(
                    new BlockStateField( "block_state", Blocks.BROWN_CANDLE_CAKE.defaultBlockState() ), General::testCallback ) ).field();
            
            itemStackField = SPEC.define( new InjectionWrapperField<>(
                    new ItemStackField( "item_stack", new ItemStack( Items.BEEF, 22 ) ), General::testCallback ) ).field();
            
            // ---- Single registry objects ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            blockRegObjectField = SPEC.define( new InjectionWrapperField<>(
                    new RegObjectField<>( "block_reg_object", IRegWrapper.of( ForgeRegistries.BLOCKS ),
                            Blocks.ICE ), General::testCallback ) ).field();
            
            itemRegObjectField = SPEC.define( new InjectionWrapperField<>(
                    new RegObjectField<>( "item_reg_object", IRegWrapper.of( ForgeRegistries.ITEMS ),
                            ResourceKey.create( Registries.ITEM, ResourceLocation.withDefaultNamespace( "stick" ) ) ), General::testCallback ) ).field();
            
            mobEffectRegObjectField = SPEC.define( new InjectionWrapperField<>(
                    new RegObjectField<>( "mob_effect_reg_object", IRegWrapper.of( ForgeRegistries.MOB_EFFECTS ),
                            "fire_resistance" ), General::testCallback ) ).field();
            
            entityTypeRegObjectField = SPEC.define( new InjectionWrapperField<>(
                    new RegObjectField<>( "entity_type_reg_object", IRegWrapper.of( ForgeRegistries.ENTITY_TYPES ),
                            ResourceLocation.withDefaultNamespace( "creeper" ) ), General::testCallback ) ).field();
            
            soundRegObjectField = SPEC.define( new InjectionWrapperField<>(
                    new RegObjectField<>( "sound_reg_object", IRegWrapper.of( ForgeRegistries.SOUND_EVENTS ),
                            SoundEvents.GOAT_AMBIENT ), General::testCallback ) ).field();
            
            // ---- Fuzzy collections ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            fuzzyListField = SPEC.define( new InjectionWrapperField<>(
                    new FuzzyListField<>( "fuzzy_list_field", new FuzzyList.Builder<>( NumberKey.longParser( LongValueCodec.ANY ) )
                            .add( NumberKey.exactly( 1L, false ) )
                            .add( NumberKey.exactly( 1234L, false ) )
                            .add( NumberKey.exactly( 1234567890000000000L, false ) )
                            .build() ), General::testCallback ) ).field();
            
            fuzzySetField = SPEC.define( new InjectionWrapperField<>(
                    new FuzzySetField<>( "fuzzy_set_field", new FuzzySet.Builder<>( ResourceLocKey.PARSER )
                            .add( ResourceLocKey.of( BuiltInLootTables.ANCIENT_CITY ) )
                            .add( ResourceLocKey.of( ResourceLocation.fromNamespaceAndPath( "not_a_mod", "not_a_loot_table" ) ) )
                            .add( ResourceLocKey.of( ResourceLocationUtils.EMPTY ) )
                            .add( ResourceLocKey.of( "this_is:a_resource_location" ) )
                            .build() ), General::testCallback ) ).field();
            
            fuzzyMapField = SPEC.define( new InjectionWrapperField<>(
                    new FuzzyMapField<>( "fuzzy_map_field", new FuzzyMap.Builder<>(
                            NumberKey.doubleParser( DoubleValueCodec.NON_NEGATIVE ),
                            StringValueCodec.of( 10 ) )
                            .put( NumberKey.exactly( 0.0, false ), "Binky" )
                            .put( NumberKey.exactly( 1.0, false ), "Bonky" )
                            .put( NumberKey.exactly( 2.0, false ), "Spinky" )
                            .put( NumberKey.exactly( 3.0, false ), "Sponky" )
                            .put( NumberKey.exactly( 4.0, false ), "A very sad $5 sponge" )
                            .build() ), General::testCallback ) ).field();
            
            /// Note: To fully test {@link fathertoast.crust.api.config.common.value.collection.key.RegObjKey}, we
            ///     want to use each {@link fathertoast.crust.api.config.common.value.collection.key.IRegWrapper} type
            ///     (using registries that have tags) with each {@link KeyUsage}.
            /// Forge reg:      {@link ForgeRegistries#BLOCKS};         tags: {@link Tags.Blocks}   (BlockState collections)
            /// Vanilla reg:    {@link BuiltInRegistries#INSTRUMENT};   tags: {@link InstrumentTags}
            /// Dynamic reg:    {@link Registries#DAMAGE_TYPE};         tags: {@link DamageTypeTags}
            //TODO implement the above in various registry collection fields
            final ResourceLocation MISSING_FEATURE = ResourceLocation.fromNamespaceAndPath( "missing", "resource_.-/location" );
            
            entitySetField = SPEC.define( new InjectionWrapperField<>(
                    new EntitySetField( "entity_set_field", new EntitySet.Builder<>()
                            .addTagBlacklist( MISSING_FEATURE )
                            .add( EntityType.CREEPER ).add( TestCrustObjects.Obj.TEST_SKELETON )
                            .add( EntityType.PLAYER )
                            .addWildcard( "uninstalled_mod" ).add( MISSING_FEATURE )
                            .addBlacklist( EntityType.STRAY ).addTag( EntityTypeTags.RAIDERS )
                            .addBlacklist( EntityType.ZOMBIE )
                            .addExtends( EntityType.ZOMBIE ).addExtends( EntityType.GLOW_SQUID, 1 )
                            .addTag( "deadlyworld:mini" )
                            //.add( EntityType.STRAY ) // Should crash - dupes not allowed in set/map builders
                            .addWildcard( "minecraft", "ender" )
                            .addWildcard( "specialmobs", "fire" )
                            .build() ), General::testCallback ) ).field();
            
            entityMapField = SPEC.define( new InjectionWrapperField<>(
                    new EntityMapField<>( "entity_map_field", new EntityMap
                            .Builder<>( ArrayValueCodec.of( 3, Double.class, DoubleValueCodec.SIGNED_PERCENT ) )
                            .put( EntityType.DONKEY, new Double[] { -0.420, 0.0001, 0.42042 } )
                            .put( TestCrustObjects.Obj.TEST_SKELETON, new Double[] { 0.0, -1.0, 1.0 } )
                            .putBlacklist( EntityType.STRAY ).putBlacklist( EntityType.ZOMBIE )
                            .putTag( EntityTypeTags.DISMOUNTS_UNDERWATER, new Double[] { 0.666, 0.666, 0.666 } )
                            .putExtends( EntityType.ZOMBIE, new Double[] { 0.9, 0.6, -0.9 } )
                            .putExtends( EntityType.SKELETON, 1, new Double[] { -0.9, 0.6, 0.9 } )
                            //.put( EntityType.STRAY, new Double[3] ) // Should crash - dupes not allowed in set/map builders
                            .putWildcard( "minecraft", "ender", new Double[] { 0.1, 0.2, 0.3 } )
                            .buildWithDefault( new Double[3] ) ), General::testCallback ) ).field();
            
            blockStateSetField = SPEC.define( new InjectionWrapperField<>(
                    new BlockStateSetField( "block_state_set_field", new BlockStateSet.Builder<>()
                            .add( Blocks.ANVIL )
                            .addBlacklist( Blocks.ICE )
                            .add( MISSING_FEATURE, BlockStatePropertyMap.EMPTY ).add( "oak_log[axis=y]" ).add( Blocks.ACACIA_STAIRS.defaultBlockState() )
                            .addTag( Tags.Blocks.CHESTS ).addTag( "forge:sandstone" )
                            .addTagBlacklist( MISSING_FEATURE )
                            .addWildcard( "minecraft", "oak" )
                            .buildWithDefault() ), General::testCallback ) ).field();
            
            // noinspection deprecation
            blockStateMapField = SPEC.define( new InjectionWrapperField<>(
                    new BlockStateMapField<>( "block_state_map_field", new BlockStateMap.Builder<>( EnumValueCodec.of( BiomeCategory.NONE ) )
                            .put( Blocks.ANVIL, BiomeCategory.BADLANDS )
                            .put( MISSING_FEATURE, BiomeCategory.THE_END )
                            .put( "birch_log[axis=y]", BiomeCategory.THE_END )
                            .putBlacklist( CrustObjects.Blocks.FEATURE_GENERATOR )
                            .putTag( Tags.Blocks.CHESTS, BiomeCategory.HILLS )
                            .putTag( "forge:sandstone", BiomeCategory.HILLS )
                            .putTag( MISSING_FEATURE, BiomeCategory.MUSHROOM )
                            .putTagBlacklist( BlockTags.BAMBOO_PLANTABLE_ON )
                            .putWildcard( "deadlyworld", BiomeCategory.BEACH )
                            .build() ), General::testCallback ) ).field();
            
            blockStateListField = SPEC.define( new InjectionWrapperField<>(
                    new BlockStateListField( "block_state_list_field", new BlockStateList.Builder<>()
                            .add( Blocks.ANVIL ).add( MISSING_FEATURE ).add( "acacia_log[axis=y]" )
                            .add( Blocks.ANDESITE_SLAB, BlockStatePropertyMap.of( "type=top" ) )
                            .add( Blocks.ACACIA_STAIRS.defaultBlockState() )
                            .addTag( Tags.Blocks.CHESTS )
                            .addTag( "forge:sandstone" ).addTag( MISSING_FEATURE )
                            .build() ), General::testCallback ) ).field();
            
            blockStateValueListField = SPEC.define( new InjectionWrapperField<>(
                    new BlockStateValueListField<>( "block_state_value_list_field", new BlockStateValueList.Builder<>( DoubleValueCodec.PERCENT )
                            .put( Blocks.BARREL, 0.0 )
                            .put( MISSING_FEATURE, 0.03 )
                            .put( "jungle_log[axis=y]", 1.0 )
                            .put( Blocks.ACACIA_STAIRS.defaultBlockState(), 0.5 )
                            .putTag( Tags.Blocks.CHESTS, 0.9 )
                            .putTag( "forge:sandstone", 0.004 )
                            .putTag( MISSING_FEATURE, 0.22 )
                            .build() ), General::testCallback ) ).field();
            
            blockStateWeightedListField = SPEC.define( new InjectionWrapperField<>(
                    new BlockStateWeightedListField( "block_state_weighted_list_field", new BlockStateWeightedList.Builder<>()
                            .add( 10, Blocks.CARTOGRAPHY_TABLE )
                            .add( 0, MISSING_FEATURE )
                            .add( 20, "oak_log[axis=y]" )
                            .add( 20, Blocks.ACACIA_STAIRS.defaultBlockState() )
                            .addTag( 90, Tags.Blocks.CHESTS )
                            .addTag( 1000, "forge:sandstone" )
                            .addTag( 0, MISSING_FEATURE )
                            .build() ), General::testCallback ) ).field();
            
            blockStateWeightedValueListField = SPEC.define( new InjectionWrapperField<>(
                    new BlockStateWeightedValueListField<>( "block_state_weighted_value_list_field", new BlockStateWeightedValueList.Builder<>( DoubleValueCodec.ANY )
                            .put( 10, Blocks.FLETCHING_TABLE, -2.0 )
                            .put( 0, MISSING_FEATURE, -100.0 )
                            .put( 5, "spruce_log[axis=y]", 144.4 )
                            .put( 11, Blocks.ACACIA_STAIRS.defaultBlockState(), 0.1 )
                            .putTag( 99, Tags.Blocks.CHESTS, -666.0 )
                            .putTag( 2, "forge:sandstone", 1234.0 )
                            .putTag( 0, MISSING_FEATURE, Double.MIN_VALUE )
                            .build() ), General::testCallback ) ).field();
            SPEC.callback( () -> {
                StringBuilder str = new StringBuilder();
                for( BlockState state : blockStateListField.entries() ) {
                    if( state != null ) {
                        str.append( ", " ).append( state );
                    }
                }
                TestCrust.LOG.info( str.length() > 2 ? "    " + str.substring( 2 ) : str.toString() );
            } );
            
            registrySetField = SPEC.define( new InjectionWrapperField<>(
                    new RegistrySetField<>( "registry_set_field", new RegistrySet
                            .Builder<>( ForgeRegistries.ENTITY_TYPES )
                            .addTagBlacklist( MISSING_FEATURE )
                            .add( EntityType.CREEPER ).add( TestCrustObjects.Obj.TEST_SKELETON )
                            .addWildcard( "uninstalled_mod" ).add( MISSING_FEATURE )
                            .addBlacklist( EntityType.STRAY ).addTag( EntityTypeTags.SKELETONS )
                            .addTag( "deadlyworld:mini" )
                            //.add( EntityType.STRAY ) // Should crash - dupes not allowed in set/map builders
                            .addWildcard( "minecraft", "ender" )
                            .addWildcard( "specialmobs", "fire" )
                            .build() ), General::testCallback ) ).field();
            
            registryMapField = SPEC.define( new InjectionWrapperField<>(
                    new RegistryMapField<>( "registry_map_field", new RegistryMap
                            .Builder<>( ForgeRegistries.ENTITY_TYPES, IntValueCodec.of( 0, IntField.Range.TOKEN_NEGATIVE ) )
                            .put( EntityType.DONKEY, 5 ).put( TestCrustObjects.Obj.TEST_SKELETON, 420 )
                            .putBlacklist( EntityType.STRAY ).putTag( EntityTypeTags.SKELETONS, 666 )
                            //.add( EntityType.STRAY ) // Should crash - dupes not allowed in set/map builders
                            .putWildcard( "minecraft", "ender", 3 )
                            .buildWithDefault( -1 ) ), General::testCallback ) ).field();
            
            // We can test iterator usage (lists) via callback log outputs
            registryListFieldVn = SPEC.define( new InjectionWrapperField<>(
                    new RegistryListField<>( "registry_list_field_v", new RegistryList
                            .Builder<>( BuiltInRegistries.INSTRUMENT )
                            .add( Instruments.FEEL_GOAT_HORN ).add( "sing_goat_horn" ).add( MISSING_FEATURE )
                            .addTag( InstrumentTags.SCREAMING_GOAT_HORNS ).addTag( MISSING_FEATURE )
                            .build() ), General::testCallback ) ).field();
            SPEC.callback( () -> {
                StringBuilder str = new StringBuilder();
                for( Instrument instrument : registryListFieldVn.entries() ) {
                    if( instrument != null ) {
                        //noinspection DataFlowIssue
                        str.append( ", " ).append( registryListFieldVn.getRegistry().getKey( instrument ).getPath() );
                    }
                }
                TestCrust.LOG.info( str.length() > 2 ? "    " + str.substring( 2 ) : str.toString() );
            } );
            
            registryValueListFieldDn = SPEC.define( new InjectionWrapperField<>(
                    new RegistryValueListField<>( "registry_value_list_field_d", new RegistryValueList
                            .Builder<>( Registries.DAMAGE_TYPE, StringValueCodec.of( 16 ) )
                            .put( DamageTypes.CACTUS, "`~!@#$%^&()_+-=*" ).put( MISSING_FEATURE, "???" )
                            .putTag( MISSING_FEATURE, "*wah" ).putTag( DamageTypeTags.BYPASSES_ARMOR, "^yikes!" )
                            .putTag( "always_triggers_silverfish", "*o[ ]Oo*" )
                            .build() ), General::testCallback ) ).field();
            SPEC.callback( () -> {
                StringBuilder str = new StringBuilder();
                for( FuzzyValueList.Pair<DamageType, String> pair : registryValueListFieldDn.entries() ) {
                    if( pair != null ) {
                        str.append( ", (" ).append( pair.key().msgId() ).append( "=" ).append( pair.value() ).append( ")" );
                    }
                }
                TestCrust.LOG.info( str.length() > 2 ? "    " + str.substring( 2 ) : str.toString() );
            } );
            
            registryWeightedListField = SPEC.define( new InjectionWrapperField<>(
                    new RegistryWeightedListField<>( "registry_weighted_list_field", new RegistryWeightedList
                            .Builder<>( Registries.CONFIGURED_FEATURE )
                            .add( 20, TreeFeatures.BIRCH )
                            .add( 6, "deadlyworld:fireball_tower_nether" )
                            .add( 10, MISSING_FEATURE )
                            .addTag( 16, "deadlyworld:overworld" )
                            .addTag( 10, MISSING_FEATURE )
                            .addTag( 5, "deadlyworld:lone_chests" )
                            .build() ), General::testCallback ) ).field();
            
            registryWeightedValueListField = SPEC.define( new InjectionWrapperField<>(
                    new RegistryWeightedValueListField<>( "registry_weighted_value_list_field", new RegistryWeightedValueList
                            .Builder<>( ForgeRegistries.MOB_EFFECTS, MobEffectStats.CODEC )
                            .put( 20, MobEffects.CONFUSION, new MobEffectStats( 100, 0 ) )
                            .put( 10, "glowing", new MobEffectStats( 80, 0 ) )
                            .put( 42, CrustObjects.Effects.WEIGHT, new MobEffectStats( 60, 0 ) )
                            .put( 15, MobEffects.MOVEMENT_SLOWDOWN, new MobEffectStats( 80, 2 ) )
                            .put( 8, CrustObjects.Effects.VULNERABILITY, new MobEffectStats( 200, 1 ) )
                            .put( 6, MISSING_FEATURE, new MobEffectStats( 420, -69 ) )
                            .putTag( 4, MISSING_FEATURE, new MobEffectStats( 0, 666 ) )
                            .buildWithNull( 69 ) ), General::testCallback ) ).field();
            
            numberSetField = SPEC.define( new InjectionWrapperField<>(
                    new NumberSetField<>( "number_set_field", NumberSet.intBuilder()
                            .addBlacklist( 4 )
                            .betweenInclusive( 0, 50 )
                            .build() ), General::testCallback ).field() );
            
            numberListField = SPEC.define( new InjectionWrapperField<>(
                    new NumberListField<>( "number_list_field", NumberList.doubleBuilder()
                            .add( 4.0 )
                            .add( 4.1 )
                            .add( 4.5 )
                            .build() ), General::testCallback ).field() );
            
            // ---- Misc. collections ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            AttributeOpList.Builder<?> attributes = new AttributeOpList.Builder<>();
            for( Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues() )
                attributes.putMultiply( attribute, 1.0 );
            attributeOpListField = SPEC.define( new InjectionWrapperField<>(
                    new AttributeOpListField( "attribute_list", attributes.build() ), General::testCallback ) ).field();
            
            environmentListField = SPEC.define( new InjectionWrapperField<>(
                    new EnvironmentListField( "environment_list_field", new EnvironmentList(
                            EnvironmentEntry.builder( SPEC, 0.0 ).belowSeaLevel().isRaining().build(),
                            EnvironmentEntry.builder( SPEC, 1.0 ).aboveGoldLevel().isRaining().build(),
                            EnvironmentEntry.builder( SPEC, 666.0 ).inBiome( BiomeTags.IS_FOREST ).build(),
                            EnvironmentEntry.builder( SPEC, 20.0 ).afterMonthsOrApocalypseDifficulty( 1 ).build(),
                            EnvironmentEntry.builder( SPEC, 6.9 ).inOverworld().build(),
                            EnvironmentEntry.builder( SPEC, -1.0 ).build() )
                            .setRange( DoubleField.Range.ANY ) ), General::testCallback ) ).field();
            
            stringListField = SPEC.define( new InjectionWrapperField<>(
                    new StringListField( "string_list", Arrays.asList( "test0", "test1", "test2" ) ), General::testCallback ) ).field();
            predicateStringListField = SPEC.define( new InjectionWrapperField<>(
                    new PredicateStringListField( "predicate_string_list", Arrays.asList( "test0", "test1", "test2", "test3" ),
                            ( line ) -> !line.contains( ":" ) ), General::testCallback ) ).field();
            
            // ---- Misc. tests ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            longCommentField = SPEC.define( new BooleanField( "long_comment", true,
                    "Oh boy, this comment sure is long! The reason it is so very very long is because of " +
                            "the sheer length of the comment, which attributes to the comment's general longness.",
                    
                    "Now that we know how long this comment is, let us not make too lengthy commentary on the lengthness.",
                    "Thank you for your longness! So long, and we long to hear from you again! Bye. Im just gonna write " +
                            "some more just in case.",
                    
                    "Gotta make sure the comment is long enough... okay that's enough bye!",
                    
                    "Just kidding. Let's keep going with this long comment action - how about some really long portion " +
                            "that requires some additional wrapping, much like some kind of spaghetti wraps around the " +
                            "utensil traditionally use for spaghetti. Is it a chopstick? Quite possibly. Speaking of " +
                            "chopsticks, did you know that chopsticks are shaped pairs of equal-length sticks that have " +
                            "been used as kitchen and eating utensils in most countries of Sinosphere for over three " +
                            "millennia? They are held in the dominant hand, secured by fingers, and wielded as extensions " +
                            "of the hand, to pick up food. Truly exciting." ) );
            
            // ---- Deprecated ---- //
            
            //            blockListField = SPEC.define( new InjectionWrapperField<>(
            //                    new BlockListField( "block_list", new BlockList(
            //                            List.of( "crust" ),
            //                            List.of( BlockTags.ENDERMAN_HOLDABLE ),
            //                            new BlockEntry( Blocks.GRASS_BLOCK ),
            //                            new BlockEntry( Blocks.FURNACE.defaultBlockState().setValue( AbstractFurnaceBlock.LIT, true ) ) ),
            //                            (String[]) null ), General::testCallback ) ).field();
            //            entityListField = SPEC.define( new InjectionWrapperField<>(
            //                    new EntityListField( "entity_list", new EntityList(
            //                            new DefaultValueEntry( 0.0 ),
            //                            new EntityEntry( EntityType.CREEPER, true, 1.0 ),
            //                            new EntityEntry( EntityType.ZOMBIE, false, 2.0 )
            //                    ).addTagEntries( List.of(
            //                                    new EntityTagEntry( EntityTypeTags.SKELETONS, 2.0 )
            //                            ) )
            //                            .addNamespaceEntries( List.of(
            //                                    new NamespaceRegistryEntry( ICrustApi.MOD_ID, 2.0 ),
            //                                    new NamespaceRegistryEntry( "minecraft", 1.5 )
            //                            ) )
            //                            .setSingleValue().setRange( 0.0, 2.0 ),
            //                            (String[]) null ), General::testCallback ) ).field();
            //            registryEntryListField = SPEC.define( new InjectionWrapperField<>(
            //                    new RegistryEntryListField<>( "registry_entry_list",
            //                            new RegistryEntryList<>( ForgeRegistries.ENTITY_TYPES,
            //                                    List.of( ICrustApi.MOD_ID ),
            //                                    List.of( EntityTypeTags.FALL_DAMAGE_IMMUNE ),
            //                                    EntityType.SHEEP, EntityType.ALLAY ),
            //                            (String[]) null ), General::testCallback ) ).field();
            //            registryEntryValueListField = SPEC.define( new InjectionWrapperField<>(
            //                    new RegistryEntryValueListField<>( "registry_entry_value_list",
            //                            new RegistryEntryValueList<>( new DefaultValueEntry( 0.0 ), () -> ForgeRegistries.MOB_EFFECTS,
            //                                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.CONFUSION ), 1.2 ),
            //                                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.ABSORPTION ), 2.0 )
            //                            ).setSingleValue(),
            //                            (String[]) null ), General::testCallback ) ).field();
            //            lazyRegistryEntryListField = SPEC.define( new InjectionWrapperField<>(
            //                    new LazyRegistryEntryListField<>( "lazy_registry_entry_list",
            //                            new LazyRegistryEntryList<>( ForgeRegistries.MOB_EFFECTS,
            //                                    List.of( "minecraft" ),
            //                                    null,
            //                                    MobEffects.CONFUSION ),
            //                            (String[]) null ), General::testCallback ) ).field();
        }
        
        private static void testCallback( AbstractConfigField field ) {
            TestCrust.LOG.info( "{} = {}", field.getKey(), field.getValue() );
        }
        
        private static void printLine() { TestCrust.LOG.info( "--------" ); }
        
        private static void printList( Iterable<?> itr ) {
            StringBuilder str = new StringBuilder();
            for( Object obj : itr ) if( obj != null ) str.append( ", " ).append( obj );
            TestCrust.LOG.info( str.length() > 2 ? str.substring( 2 ) : str.toString() );
        }
        
        private static String generateFormatTest() {
            StringBuilder str = new StringBuilder( "TEST" );
            for( ChatFormatting format : ChatFormatting.values() ) {
                str.append( ' ' ).append( format ).append( format.name() ).append( ChatFormatting.RESET );
            }
            return str.toString();
        }
    }
    
    /**
     * Category for testing environments for environment list fields.
     */
    public static class Environment extends AbstractConfigCategory<TestConfigFile> {
        
        public final EnvironmentListField[] fields;
        
        Environment( TestConfigFile parent ) {
            super( parent, "environments", "The environments selected and sensed by the " +
                    "config system shall be robust such that they do not include any environment or levels of " +
                    "environmental stimulus that may be experienced by the config prior to the commencement of " +
                    "the launch cycle." );
            
            AbstractConfigField dummy = new BooleanField( "ignore_me", false, (String[]) null );
            dummy.setSpec( SPEC );
            
            Set<String> environments = CrustEnvironmentRegistry.getNames();
            fields = new EnvironmentListField[environments.size()];
            int i = 0;
            TestCrust.LOG.warn( "TEST TEST TEST - Please ignore the following warnings - TEST TEST TEST" );
            for( String env : environments ) {
                fields[i++] = SPEC.define( new EnvironmentListField( env,
                        new EnvironmentList( new EnvironmentEntry( 1.0,
                                CrustEnvironmentRegistry.parse( dummy, env, "" ) ) ),
                        (String[]) null ) );
            }
            TestCrust.LOG.warn( "TEST TEST TEST - End of scheduled warnings, carry on - TEST TEST TEST" );
        }
    }
}