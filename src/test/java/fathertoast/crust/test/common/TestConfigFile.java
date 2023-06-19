package fathertoast.crust.test.common;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.value.*;
import fathertoast.crust.api.config.common.value.environment.CrustEnvironmentRegistry;
import fathertoast.crust.api.config.common.value.environment.biome.BiomeCategory;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.TextFormatting;
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
                "In-game config editor client preferences." );
        
        GENERAL = new General( this );
        ENVIRONMENT = new Environment( this );
        
        SPEC.newLine( 2 );
        SPEC.describeEnvironmentListPart2of2();
    }
    
    /**
     * Category for testing configs.
     */
    public static class General extends AbstractConfigCategory<TestConfigFile> {
        
        public final AttributeListField attributeListField;
        public final BlockListField blockListField;
        public final BooleanField booleanField;
        public final ColorIntField colorIntField;
        public final ColorIntField colorIntFieldAlpha;
        public final DoubleField doubleField;
        public final EntityListField entityListField;
        public final EnumField<BiomeCategory> enumField;
        public final EnvironmentListField environmentListField;
        public final IntField intField;
        public final LazyRegistryEntryListField<Effect> lazyRegistryEntryListField;
        public final ScaledDoubleField scaledDoubleField;
        public final SqrDoubleField sqrDoubleField;
        public final StringField stringField;
        public final StringListField stringListField;
        
        General( TestConfigFile parent ) {
            super( parent, "general", generateFormatTest() );
            
            SPEC.comment( generateFormatTest() );
            
            SPEC.newLine();
            
            // Tester for each field type, in alphabetical order
            
            List<AttributeEntry> attributes = new ArrayList<>();
            for( Attribute attribute : ForgeRegistries.ATTRIBUTES.getValues() )
                attributes.add( AttributeEntry.mult( attribute, 1.0 ) );
            attributeListField = SPEC.define( new InjectionWrapperField<>(
                    new AttributeListField( "attribute_list", new AttributeList( attributes ),
                            (String[]) null ), General::testCallback ) ).field();
            blockListField = SPEC.define( new InjectionWrapperField<>(
                    new BlockListField( "block_list", new BlockList(
                            new BlockEntry( Blocks.GRASS_BLOCK ),
                            new BlockEntry( Blocks.FURNACE.defaultBlockState().setValue( AbstractFurnaceBlock.LIT, true ) ) ),
                            (String[]) null ), General::testCallback ) ).field();
            booleanField = SPEC.define( new InjectionWrapperField<>(
                    new BooleanField( "boolean", false,
                            (String[]) null ), General::testCallback ) ).field();
            colorIntField = SPEC.define( new InjectionWrapperField<>(
                    new ColorIntField( "color_int_rgb", 0x00FFFF, false,
                            (String[]) null ), General::testCallback ) ).field();
            colorIntFieldAlpha = SPEC.define( new InjectionWrapperField<>(
                    new ColorIntField( "color_int_argb", 0x77FF00FF, true,
                            (String[]) null ), General::testCallback ) ).field();
            doubleField = SPEC.define( new InjectionWrapperField<>(
                    new DoubleField( "double", 1.0, DoubleField.Range.ANY,
                            (String[]) null ), General::testCallback ) ).field();
            entityListField = SPEC.define( new InjectionWrapperField<>(
                    new EntityListField( "entity_list", new EntityList(
                            new EntityEntry( 0.0 ), new EntityEntry( EntityType.CREEPER, true, 1.0 ),
                            new EntityEntry( EntityType.ZOMBIE, false, 2.0 )
                    ).setSingleValue().setRange( 0.0, 2.0 ),
                            (String[]) null ), General::testCallback ) ).field();
            enumField = SPEC.define( new InjectionWrapperField<>(
                    new EnumField<>( "enum", BiomeCategory.NONE,
                            (String[]) null ), General::testCallback ) ).field();
            environmentListField = SPEC.define( new InjectionWrapperField<>(
                    new EnvironmentListField( "environment_list_field", new EnvironmentList(
                            EnvironmentEntry.builder( SPEC, 0.0 ).belowSeaLevel().isRaining().build(),
                            EnvironmentEntry.builder( SPEC, 1.0 ).aboveGoldLevel().isRaining().build(),
                            EnvironmentEntry.builder( SPEC, 666.0 ).inBiomeCategory( BiomeCategory.FOREST ).build(),
                            EnvironmentEntry.builder( SPEC, 6.9 ).inOverworld().build() ),
                            (String[]) null ), General::testCallback ) ).field();
            intField = SPEC.define( new InjectionWrapperField<>(
                    new IntField( "int", 1, IntField.Range.ANY,
                            (String[]) null ), General::testCallback ) ).field();
            lazyRegistryEntryListField = SPEC.define( new InjectionWrapperField<>(
                    new LazyRegistryEntryListField<>( "lazy_registry_entry_list",
                            new LazyRegistryEntryList<>( ForgeRegistries.POTIONS, Effects.CONFUSION ),
                            (String[]) null ), General::testCallback ) ).field();
            scaledDoubleField = SPEC.define( new InjectionWrapperField<>(
                    new ScaledDoubleField( "scaled_double", 1.0, 6.0, DoubleField.Range.ANY,
                            (String[]) null ), General::testCallback ) ).field();
            sqrDoubleField = SPEC.define( new InjectionWrapperField<>(
                    new SqrDoubleField( "sqr_double", 1.0, DoubleField.Range.ANY,
                            (String[]) null ), General::testCallback ) ).field();
            stringField = SPEC.define( new InjectionWrapperField<>(
                    new StringField( "string", "Test!",
                            (String[]) null ), General::testCallback ) ).field();
            stringListField = SPEC.define( new InjectionWrapperField<>(
                    new StringListField( "string_list", Arrays.asList( "test0", "test1", "test2" ),
                            (String[]) null ), General::testCallback ) ).field();
        }
        
        private static void testCallback( AbstractConfigField field ) {
            TestCrust.LOG.info( "{} = {}", field.getKey(), field.getValue() );
        }
        
        private static String generateFormatTest() {
            StringBuilder str = new StringBuilder( "TEST" );
            for( TextFormatting format : TextFormatting.values() ) {
                str.append( ' ' ).append( format ).append( format.name() ).append( TextFormatting.RESET );
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