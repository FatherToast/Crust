package fathertoast.crust.test.common;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.field.collection.*;
import fathertoast.crust.api.config.common.value.AttributeEntry;
import fathertoast.crust.api.config.common.value.AttributeList;
import fathertoast.crust.api.config.common.value.EnvironmentEntry;
import fathertoast.crust.api.config.common.value.EnvironmentList;
import fathertoast.crust.api.config.common.value.collection.*;
import fathertoast.crust.api.config.common.value.collection.value.*;
import fathertoast.crust.api.config.common.value.environment.CrustEnvironmentRegistry;
import fathertoast.crust.api.config.common.value.environment.biome.BiomeCategory;
import fathertoast.crust.api.lib.CrustObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        
        SPEC.fileOnlyNewLine( 2 );
        SPEC.describeEnvironmentListPart2of2();
    }
    
    /**
     * Category for testing configs.
     */
    public static class General extends AbstractConfigCategory<TestConfigFile> {
        // Primitives
        public final BooleanField booleanField;
        public final IntField intField;
        public final ColorIntField colorIntField;
        public final ColorIntField colorIntFieldAlpha;
        public final DoubleField doubleField;
        public final ScaledDoubleField scaledDoubleField;
        public final SqrDoubleField sqrDoubleField;
        // Simple objects
        public final StringField stringField;
        public final EnumField<BiomeCategory> enumField;
        //        public final BlockStateField blockStateField;
        //        public final FuzzyKeyField<String> fuzzyKeyField;
        //        public final ValueCodecField<MobEffectStats> valueCodecField;
        // Fuzzy collections
        public final EntitySetField entitySetField;
        public final EntityMapField<Double[]> entityMapField;
        //        public final BlockStateSetField blockStateSetField;
        //        public final BlockStateMapField<BiomeCategory> blockStateMapField;
        public final BlockStateListField blockStateListField;
        //        public final BlockStateValueListField<Double> blockStateValueListField;
        //        public final BlockStateWeightedListField blockStateWeightedListField;
        //        public final BlockStateWeightedValueListField<Double> blockStateWeightedValueListField;
        public final RegistrySetField<EntityType<?>> registrySetField;
        public final RegistryMapField<EntityType<?>, Integer> registryMapField;
        public final RegistryListField<Instrument> registryListFieldVn;
        public final RegistryValueListField<DamageType, String> registryValueListFieldDn;
        public final RegistryWeightedListField<ConfiguredFeature<?, ?>> registryWeightedListField;
        public final RegistryWeightedValueListField<MobEffect, MobEffectStats> registryWeightedValueListField;
        // Misc collections
        public final AttributeListField attributeListField;
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
            
            // ---- Fuzzy collections ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            /// Note: To fully test {@link fathertoast.crust.api.config.common.value.collection.key.RegObjKey}, we
            ///     want to use each {@link fathertoast.crust.api.config.common.value.collection.key.IRegWrapper} type
            ///     (using registries that have tags) with each {@link KeyUsage}.
            /// Forge reg:      {@link ForgeRegistries#BLOCKS};         tags: {@link Tags.Blocks}   (BlockState collections)
            /// Vanilla reg:    {@link BuiltInRegistries#INSTRUMENT};   tags: {@link InstrumentTags}
            /// Dynamic reg:    {@link Registries#DAMAGE_TYPE};         tags: {@link DamageTypeTags}
            //TODO implement the above in various registry collection fields
            ResourceLocation MISSING_FEATURE = ResourceLocation.fromNamespaceAndPath( "missing", "resource_.-/location" );
            
            entitySetField = SPEC.define( new InjectionWrapperField<>(
                    new EntitySetField( "entity_set_field", new EntitySet.Builder<>()
                            .addTagBlacklist( MISSING_FEATURE )
                            .add( EntityType.CREEPER ).add( TestCrustObjects.Obj.TEST_SKELETON )
                            .add( EntityType.PLAYER )
                            .addWildcard( "uninstalled_mod" ).add( MISSING_FEATURE )
                            .addBlacklist( EntityType.STRAY ).addTag( EntityTypeTags.SKELETONS )
                            .addBlacklist( EntityType.ZOMBIE ).addExtends( EntityType.ZOMBIE )
                            .addTag( "deadlyworld:mini" )
                            //.add( EntityType.STRAY ) // Should crash - dupes not allowed in set/map builders
                            .addWildcard( "minecraft", "ender" )
                            .addWildcard( "specialmobs", "fire" )
                            .build() ), General::testCallback ) ).field();
            entityMapField = SPEC.define( new InjectionWrapperField<>(
                    new EntityMapField<>( "entity_map_field", new EntityMap
                            .Builder<>( ArrayValueCodec.of( 3, DoubleValueCodec.SIGNED_PERCENT ) )
                            .put( EntityType.DONKEY, new Double[] { -0.420, 0.0001, 0.42042 } )
                            .put( TestCrustObjects.Obj.TEST_SKELETON, new Double[] { 0.0, -1.0, 1.0 } )
                            .putBlacklist( EntityType.STRAY ).putBlacklist( EntityType.ZOMBIE )
                            .putTag( EntityTypeTags.SKELETONS, new Double[] { 0.666, 0.666, 0.666 } )
                            .putExtends( EntityType.ZOMBIE, new Double[] { 0.9, 0.6, -0.9 } )
                            //.put( EntityType.STRAY, new Double[3] ) // Should crash - dupes not allowed in set/map builders
                            .putWildcard( "minecraft", "ender", new Double[] { 0.1, 0.2, 0.3 } )
                            .buildWithDefault( new Double[3] ) ), General::testCallback ) ).field();
            
            blockStateListField = SPEC.define( new InjectionWrapperField<>(//TODO
                    new BlockStateListField( "block_state_list_field", new BlockStateList.Builder<>()
                            .add( Blocks.ANVIL ).add( MISSING_FEATURE ).add( "oak_log[axis=y]" )
                            .add( Blocks.ACACIA_STAIRS.defaultBlockState() )
                            .addTag( Tags.Blocks.CHESTS ).addTag( "forge:sandstone" ).addTag( MISSING_FEATURE )
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
            
            // ---- Misc. collections ---- //
            SPEC.callback( General::printLine );
            SPEC.newLine();
            
            List<AttributeEntry> attributes = new ArrayList<>();
            for( Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues() )
                attributes.add( AttributeEntry.mult( attribute, 1.0 ) );
            attributeListField = SPEC.define( new InjectionWrapperField<>(
                    new AttributeListField( "attribute_list",
                            new AttributeList( attributes ) ), General::testCallback ) ).field();
            
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