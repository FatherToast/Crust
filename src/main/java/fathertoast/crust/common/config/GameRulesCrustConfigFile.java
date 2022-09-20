package fathertoast.crust.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.InjectionWrapperField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.common.core.Crust;
import net.minecraft.world.GameRules;

import java.util.Locale;
import java.util.Map;

/**
 * File for configuring default game rules.
 */
public class GameRulesCrustConfigFile extends AbstractConfigFile {
    
    // Note: Since all fields in all categories are injected, we don't need to reference any of them
    //private final GRConfigCategory[] CATEGORIES;
    //public GRConfigCategory get( Category category ) { return CATEGORIES[category.ordinal()]; }
    
    /** Whether this config file is enabled. */
    public final BooleanField enabled;
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    GameRulesCrustConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "This config allows the default game rules for world creation to be altered.",
                "Many game rules make testing easier, so this is handy when you need to repeatedly generate new worlds.",
                "",
                "For more information on game rules, see the in-game descriptions in the 'Create New World' menu",
                "or the Minecraft Wiki article (https://minecraft.fandom.com/wiki/Game_rule)." );
        
        SPEC.newLine( 2 );
        SPEC.decreaseIndent();
        
        enabled = SPEC.define( new BooleanField( "config_enabled", false,
                "Whether this config file is enabled." ), RestartNote.GAME_PARTIAL );
        
        SPEC.increaseIndent();
        
        //CATEGORIES = new GRConfigCategory[Category.values().length];
        for( Category category : Category.values() ) {
            //CATEGORIES[category.ordinal()] =
            new GRConfigCategory( this, category );
        }
    }
    
    /**
     * Category linked to one game rule category.
     */
    public static class GRConfigCategory extends AbstractConfigCategory<GameRulesCrustConfigFile> {
        
        GRConfigCategory( GameRulesCrustConfigFile parent, Category category ) {
            super( parent, category.name().toLowerCase( Locale.ROOT ),
                    "Default game rule settings for the '" + category.name().toLowerCase() + "' category." );
            
            for( Map.Entry<GameRules.RuleKey<?>, GameRules.RuleType<?>> rule : GameRules.GAME_RULE_TYPES.entrySet() ) {
                if( category.BASE.equals( rule.getKey().category ) ) {
                    final GameRules.RuleValue<?> testValue = rule.getValue().createRule();
                    if( testValue instanceof GameRules.BooleanValue ) {
                        //noinspection unchecked
                        defineFor( (GameRules.RuleKey<GameRules.BooleanValue>) rule.getKey(),
                                (((GameRules.BooleanValue) testValue).get()) );
                    }
                    else if( testValue instanceof GameRules.IntegerValue ) {
                        //noinspection unchecked
                        defineFor( (GameRules.RuleKey<GameRules.IntegerValue>) rule.getKey(),
                                (((GameRules.IntegerValue) testValue).get()) );
                    }
                    else {
                        Crust.LOG.warn( "Skipping undefined game rule type {}:'{}' with value '{}'",
                                category.name().toLowerCase(), rule.getKey().getId(), testValue );
                    }
                }
            }
        }
        
        /** Defines a config option in the spec to control the default setting of a boolean game rule. */
        private void defineFor( GameRules.RuleKey<GameRules.BooleanValue> gameRule, boolean defaultValue ) {
            String id = ConfigUtil.camelCaseToLowerUnderscore( gameRule.getId() );
            BooleanField wrappedField = new BooleanField( id, defaultValue, (String[]) null );
            SPEC.define( new InjectionWrapperField<>( wrappedField, ( wrapped ) -> {
                if( CrustConfig.DEFAULT_GAME_RULES.enabled.get() )
                    GameRules.GAME_RULE_TYPES.put( gameRule, GameRules.BooleanValue.create( wrapped.get() ) );
            } ) );
            //return wrappedField; // If we want to poll the value, we can return the underlying field
        }
        
        /** Defines a config option in the spec to control the default setting of an integer game rule. */
        private void defineFor( GameRules.RuleKey<GameRules.IntegerValue> gameRule, int defaultValue ) {
            String id = ConfigUtil.camelCaseToLowerUnderscore( gameRule.getId() );
            IntField wrappedField = new IntField( id, defaultValue, IntField.Range.ANY, (String[]) null );
            SPEC.define( new InjectionWrapperField<>( wrappedField, ( wrapped ) -> {
                if( CrustConfig.DEFAULT_GAME_RULES.enabled.get() )
                    GameRules.GAME_RULE_TYPES.put( gameRule, GameRules.IntegerValue.create( wrapped.get() ) );
            } ) );
            //return wrappedField; // If we want to poll the value, we can return the underlying field
        }
    }
    
    /**
     * Wraps the vanilla game rule categories so they have readable names in an obfuscated environment.
     *
     * @see GameRules.Category
     */
    public enum Category {
        
        PLAYER( GameRules.Category.PLAYER ),
        MOBS( GameRules.Category.MOBS ),
        SPAWNING( GameRules.Category.SPAWNING ),
        DROPS( GameRules.Category.DROPS ),
        UPDATES( GameRules.Category.UPDATES ),
        CHAT( GameRules.Category.CHAT ),
        MISC( GameRules.Category.MISC );
        
        static {
            if( values().length != GameRules.Category.values().length ) {
                Crust.LOG.error( "Game rule config category count is unexpected! May break default game rule settings. :(" );
            }
        }
        
        /** The wrapped vanilla value. */
        public final GameRules.Category BASE;
        
        Category( GameRules.Category base ) { BASE = base; }
    }
}