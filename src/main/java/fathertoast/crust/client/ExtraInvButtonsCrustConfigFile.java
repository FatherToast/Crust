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
    
    private static String customId( int index ) { return "custom" + (index + 1); }
    
    public final General GENERAL;
    public final Button[] CUSTOM_BUTTONS = new Button[16];
    
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
        
        for( int i = 0; i < CUSTOM_BUTTONS.length; i++ ) {
            CUSTOM_BUTTONS[i] = new Button( this, i );
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
                    "Set this to false to hide all extra inventory buttons." ) ); //TODO note about hotkeys later
            hideUnusable = SPEC.define( new BooleanField( "hide_unusable", true,
                    "If true, buttons that are unusable due to permissions will not be displayed." ) );
            hideDisabled = SPEC.define( new BooleanField( "hide_disabled", false,
                    "If true, built-in buttons that are disabled due to temporary conditions will not be displayed." ) );
            
            SPEC.newLine();
            
            buttonsPerRow = SPEC.define( new IntField( "buttons_per_row", 3, IntField.Range.NON_NEGATIVE,
                    "The number of buttons that can be displayed per row. The number of rows is automatically calculated." ) );
            buttons = SPEC.define( new StringListField( "displayed_buttons", "Button", Arrays.asList(
                    "toggleRain", "weatherStorm", "gameMode",
                    "day", "night", "killAll",
                    "netherPortal", "endPortal", "fullHeal" ), // TODO temp - testing
                    "The buttons displayed in the inventory, in the order you want them displayed.",
                    "These are ordered left-to-right, then wrapped into rows.",
                    //"You may assign a hotkey to any button, whether or not you choose to display it.", TODO when hotkeys exist
                    "Built-in buttons are " + TomlHelper.literalList( ButtonInfo.builtInIds().subList( 0, 7 ) ) + ",",
                    TomlHelper.literalList( ButtonInfo.builtInIds().subList( 7, 14 ) ) + ",", // TODO figure out a better way to wrap
                    TomlHelper.literalList( ButtonInfo.builtInIds().subList( 14, ButtonInfo.builtInIds().size() ) ) + ".",
                    "Custom buttons are \"" + customId( 0 ) + "\", \"" + customId( 1 ) + "\", etc. - same as the category name." ) );
            
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
     * Category for a single extra button.
     */
    public static class Button extends AbstractConfigCategory<ExtraInvButtonsCrustConfigFile> {
        
        public final StringField tooltip;
        public final StringField icon;
        public final ColorIntField iconColor;
        public final StringListField commands;
        
        Button( ExtraInvButtonsCrustConfigFile parent, int index ) {
            super( parent, customId( index ),
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
            String id = customId( index );
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
            return new String[] { ExtraInvButtonsCrustConfigFile.customId( index ), "" + (index + 1), "kill" };
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