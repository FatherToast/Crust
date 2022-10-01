package fathertoast.crust.client;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.client.button.ButtonInfo;
import fathertoast.crust.common.core.Crust;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.loot.LootTables;
import net.minecraft.world.biome.Biomes;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * File for configuring the extra inventory buttons.
 */
public class ExtraInvButtonsCrustConfigFile extends AbstractConfigFile {
    
    public final General GENERAL;
    
    public final BuiltInButtons BUILT_IN_BUTTONS;
    public final CustomButton[] CUSTOM_BUTTONS = new CustomButton[16];
    
    /**
     * @param cfgManager The mod's config manager.
     * @param cfgName    Name for the new config file. May include a file path (e.g. "folder/subfolder/filename").
     */
    ExtraInvButtonsCrustConfigFile( ConfigManager cfgManager, String cfgName ) {
        super( cfgManager, cfgName,
                "Options to modify the extra inventory buttons displayed.",
                "Extra inventory buttons are essentially macros that send commands for you.",
                "(You still need adequate permission to run the commands.)",
                "In general, you must close and reopen your inventory to see changes made to this config." );
        
        GENERAL = new General( this );
        
        BUILT_IN_BUTTONS = new BuiltInButtons( this );
        for( int i = 0; i < CUSTOM_BUTTONS.length; i++ ) {
            CUSTOM_BUTTONS[i] = new CustomButton( this, i );
        }
    }
    
    /**
     * Category for extra button display.
     */
    public static class General extends AbstractConfigCategory<ExtraInvButtonsCrustConfigFile> {
        
        public final BooleanField enabled;
        public final BooleanField hideUnusable;
        public final BooleanField hideDisabled;
        
        public final IntField buttonsPerRow;
        public final StringListField buttons;
        
        public final EnumField<Anchor> anchorY;
        public final EnumField<Anchor> anchorX;
        
        public final IntField offsetY;
        public final IntField offsetX;
        
        General( ExtraInvButtonsCrustConfigFile parent ) {
            super( parent, "general",
                    "Options to modify the extra inventory buttons displayed.",
                    "Extra inventory buttons are essentially macros that send commands for you.",
                    "You still need permission to run the commands." );
            
            enabled = SPEC.define( new BooleanField( "enabled", true,
                    "Set this to false to hide all extra inventory buttons.",
                    "Does not affect hotkeys (key bindings) assigned to buttons." ) );
            hideUnusable = SPEC.define( new BooleanField( "hide_unusable", true,
                    "If true, buttons that are unusable due to permissions will not be displayed." ) );
            hideDisabled = SPEC.define( new BooleanField( "hide_disabled", false,
                    "If true, built-in buttons that are disabled due to temporary conditions will not be displayed." ) );
            
            SPEC.newLine();
            
            buttonsPerRow = SPEC.define( new IntField( "buttons_per_row", 4, IntField.Range.NON_NEGATIVE,
                    "The number of buttons that can be displayed per row. The number of rows is automatically calculated." ) );
            buttons = SPEC.define( new StringListField( "displayed_buttons", "Button", Arrays.asList(
                    ButtonInfo.MAGNET_MODE.ID, ButtonInfo.KILL_ALL.ID, ButtonInfo.TOGGLE_RAIN.ID, ButtonInfo.WEATHER_STORM.ID,
                    ButtonInfo.SUPER_VISION_MODE.ID, ButtonInfo.SUPER_SPEED_MODE.ID, ButtonInfo.DAY.ID, ButtonInfo.NIGHT.ID,
                    ButtonInfo.NO_PICKUP_MODE.ID, ButtonInfo.GOD_MODE.ID, ButtonInfo.CLEAR_EFFECTS.ID, ButtonInfo.FULL_HEAL.ID,
                    ButtonInfo.KILL_ALL.ID, ButtonInfo.GAME_MODE.ID ),
                    "The buttons displayed in the inventory, in the order you want them displayed.",
                    "These are ordered left-to-right, then wrapped into rows.",
                    "You may assign a hotkey to any button in your options, whether or not you choose to display it.",
                    "Built-in buttons are " + TomlHelper.literalList( ButtonInfo.builtInIds().subList( 0, 7 ) ) + ",",
                    TomlHelper.literalList( ButtonInfo.builtInIds().subList( 7, 14 ) ) + ",", // TODO figure out a better way to wrap
                    TomlHelper.literalList( ButtonInfo.builtInIds().subList( 14, ButtonInfo.builtInIds().size() ) ) + ".",
                    "Custom buttons are \"" + ButtonInfo.customId( 0 ) + "\", \"" + ButtonInfo.customId( 1 ) +
                            "\", ..., \"" + ButtonInfo.customId( parent.CUSTOM_BUTTONS.length - 1 ) + "\".  (same as the category name)." ) );
            
            SPEC.newLine();
            
            anchorY = SPEC.define( new EnumField<>( "anchor.vertical", Anchor.SCREEN_TOP, Anchor.VERTICAL,
                    "The anchor position for the extra inventory buttons. That is, where they should be positioned",
                    "relative to the screen or inventory GUI." ) );
            anchorX = SPEC.define( new EnumField<>( "anchor.horizontal", Anchor.SCREEN_LEFT, Anchor.HORIZONTAL,
                    (String[]) null ) );
            
            SPEC.newLine();
            
            offsetY = SPEC.define( new IntField( "offset.vertical", 16, IntField.Range.ANY,
                    "The position offset for the extra inventory buttons from the anchor position, in GUI pixels.",
                    "Negative values move the buttons toward the top/left, positive move them toward the bottom/right." ) );
            offsetX = SPEC.define( new IntField( "offset.horizontal", 16, IntField.Range.ANY,
                    (String[]) null ) );
        }
    }
    
    /**
     * Category for built-in buttons.
     */
    public static class BuiltInButtons extends AbstractConfigCategory<ExtraInvButtonsCrustConfigFile> {
        
        public final IntField weatherDuration;
        
        public final DoubleField magnetMaxRange;
        
        public final BooleanField godModeUndying;
        public final BooleanField godModeUnbreaking;
        public final IntField godModeUneating;
        
        public final DoubleField superSpeedMulti;
        
        BuiltInButtons( ExtraInvButtonsCrustConfigFile parent ) {
            super( parent, "built_in_buttons",
                    "Options for built-in buttons." );
            
            weatherDuration = SPEC.define( new IntField( "weather.duration", 6_000, 0, 1_000_000,
                    "The duration (seconds) to set the weather for when using the various built-in weather buttons.",
                    "If 0, the duration is decided 'naturally' by the world." ) );
            
            SPEC.newLine();
            
            magnetMaxRange = SPEC.define( new DoubleField( "magnet_mode.max_range", 3.4e38, 0.0, 3.4e38,
                    "The max range (blocks) to request for magnet mode when using the \"" + ButtonInfo.MAGNET_MODE.ID + "\" button.",
                    "Leaving this at a very high value effectively just sets your range to the max allowed by the server." ) );
            
            SPEC.newLine();
            
            godModeUndying = SPEC.define( new BooleanField( "god_mode.undying", true,
                    "Whether undying mode (prevents death) should be toggled when using the \"" + ButtonInfo.GOD_MODE.ID + "\" button." ) );
            godModeUnbreaking = SPEC.define( new BooleanField( "god_mode.unbreaking", true,
                    "Whether unbreaking mode (prevents item break) should be toggled when using the \"" + ButtonInfo.GOD_MODE.ID + "\" button." ) );
            godModeUneating = SPEC.define( new IntField( "god_mode.uneating", 6, 0, 20,
                    "The level for uneating mode (minimum food level in half-drumsticks) to request when using the",
                    "\"" + ButtonInfo.GOD_MODE.ID + "\" button. Set this to 0 if you don't want to toggle uneating mode." ) );
            SPEC.callback( () -> ButtonInfo.updateGodModePerms( this ) );
            
            SPEC.newLine();
            
            superSpeedMulti = SPEC.define( new DoubleField( "super_speed.multiplier", 8.0, 1.0, 3.4e38,
                    "The speed multiplier to request for super-speed mode when using the \"" + ButtonInfo.SUPER_SPEED_MODE.ID + "\" button.",
                    "Setting this at a very high value effectively just sets your speed to the max allowed by the server.",
                    "Note: Very large speed multipliers might break the game's physics." ) );
        }
    }
    
    /**
     * Category for a single user-defined button.
     */
    public static class CustomButton extends AbstractConfigCategory<ExtraInvButtonsCrustConfigFile> {
        
        public final StringField tooltip;
        public final StringField icon;
        public final ColorIntField iconColor;
        public final StringListField commands;
        
        CustomButton( ExtraInvButtonsCrustConfigFile parent, int index ) {
            super( parent, ButtonInfo.customId( index ),
                    "Options defining the look and function of custom button #" + (index + 1) + "." );
            
            String[] defaults = getDefaults( index );
            List<String> defaultList = Arrays.asList( defaults );
            
            tooltip = SPEC.define( new StringField( "tooltip", defaults[0],
                    "A short description of the custom button's function." ) );
            icon = SPEC.define( new StringField( "icon", defaults[1],
                    "The button icon. This is a relative path from \"" + Crust.MOD_ID + ":" + ButtonInfo.ICON_PATH + "\".",
                    "If this does not end in \".png\", this string will be rendered instead of a texture." ) );
            iconColor = SPEC.define( new ColorIntField( "icon_color", Color.WHITE, false,
                    "The button icon (or text) color. Pure white (the default) is effectively no tint." ) );
            commands = SPEC.define( new StringListField( "commands", "Command",
                    defaultList.subList( 2, defaultList.size() ),
                    "A list of commands to execute when the custom button is pressed.",
                    "These are sent to the server in the order listed, as if you typed them into chat." ) );
            
            // Have the spec automatically register this on load
            String id = ButtonInfo.customId( index );
            SPEC.callback( () -> ButtonInfo.loadCustomButton( id, this ) );
        }
        
        /** @return The default values for a button config as an array ( tooltip, icon, commands... ). */
        public static String[] getDefaults( int index ) {
            // "Mega super-chill switch statement"
            int i = -1;
            if( ++i == index )
                return new String[] { "Clear all potion effects", "milk.png", "effect clear" };
            if( ++i == index )
                return new String[] { "+5 levels", "xp_plus.png", "experience add @s 5 levels" };
            if( ++i == index )
                return new String[] { "-5 levels", "xp_minus.png", "experience add @s -5 levels" };
            if( ++i == index )
                return new String[] { "Locate woodland mansion", "map_mansion.png", "locate mansion" };
            if( ++i == index )
                return new String[] { "Locate ocean monument", "map_monument.png", "locate monument" };
            if( ++i == index )
                return new String[] { "Locate stronghold", "ender_eye.png", "locate stronghold" };
            if( ++i == index )
                return new String[] { "Locate jungle biome", "world.png", "locatebiome " + ConfigUtil.toString( Biomes.JUNGLE ) };
            //if( ++i == index ) // Add this in 1.19
            //    return new String[] { "Generate village structure", "villager.png", "place structure " + ConfigUtil.toString( Structure.VILLAGE ) };
            if( ++i == index )
                return new String[] { "Reload data packs", "crafting_table_side.png", "reload" };
            if( ++i == index )
                return new String[] { "Grant all advancements", "cake.png", "advancement grant @s everything" };
            if( ++i == index )
                return new String[] { "Revoke all advancements", "cake_is_a_lie.png", "advancement revoke @s everything" };
            if( ++i == index )
                return new String[] { "Set spawn point", "compass.png", "spawnpoint" };
            if( ++i == index )
                return new String[] { "\"Respawn\"", "soul.png", "kill" };
            if( ++i == index )
                return new String[] { "Clear inventory", "fire.png", "clear" };
            if( ++i == index )
                return new String[] { "Simulate chest loot", "chest_open.png", "clear",
                        "loot give @s loot " + ConfigUtil.toString( LootTables.SIMPLE_DUNGEON ) };
            if( ++i == index )
                return new String[] { "Simulate loot of nearest mob", "kill.png", "loot give @s kill @e[limit=1,sort=nearest,type=!player]" };
            if( ++i == index ) {
                String pattern = "attribute @e[limit=1,sort=nearest,type=!player] %s get";
                return new String[] { "Check attributes of nearest mob", "magnifying_glass.png",
                        String.format( pattern, ConfigUtil.toString( Attributes.MAX_HEALTH ) ),
                        String.format( pattern, ConfigUtil.toString( Attributes.ARMOR ) ),
                        String.format( pattern, ConfigUtil.toString( Attributes.ARMOR_TOUGHNESS ) ),
                        String.format( pattern, ConfigUtil.toString( Attributes.FOLLOW_RANGE ) ),
                        String.format( pattern, ConfigUtil.toString( Attributes.MOVEMENT_SPEED ) ),
                        String.format( pattern, ConfigUtil.toString( Attributes.ATTACK_DAMAGE ) ) };
            }
            
            // Defaults to a button that gives you the name and button number; also it kills you if you press it
            return new String[] { ButtonInfo.customId( index ), "" + (index + 1), "kill" };
        }
    }
    
    public enum Anchor {
        CENTER, SCREEN_LEFT, LEFT, SCREEN_RIGHT, RIGHT, SCREEN_TOP, TOP, SCREEN_BOTTOM, BOTTOM;
        
        public static final Anchor[] VERTICAL = { SCREEN_TOP, TOP, CENTER, BOTTOM, SCREEN_BOTTOM };
        public static final Anchor[] HORIZONTAL = { SCREEN_LEFT, LEFT, CENTER, RIGHT, SCREEN_RIGHT };
        
        /**
         * @param screenSize X- or Y-size of the entire game screen.
         * @param guiSize    X- or Y-size of the active GUI window.
         * @param size       X- or Y-size of the anchored GUI element.
         * @return The anchored position to render at; that is, the position of the top-left corner.
         */
        public int pos( int screenSize, int guiSize, int size ) {
            switch( this ) {
                case SCREEN_TOP: case SCREEN_LEFT:
                    return 0;
                case SCREEN_BOTTOM: case SCREEN_RIGHT:
                    return screenSize - guiSize;
                case TOP: case LEFT:
                    return (screenSize - guiSize) / 2 - size;
                case BOTTOM: case RIGHT:
                    return (screenSize + guiSize) / 2;
                default:
                    return (screenSize - size) / 2;
            }
        }
    }
}