
package fathertoast.crust.client.button;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import fathertoast.crust.api.impl.InternalCrustPlugin;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.client.ExtraInvButtonsCrustConfigFile;
import fathertoast.crust.common.command.impl.CrustPortalCommand;
import fathertoast.crust.common.core.Crust;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.mode.type.CrustMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class ButtonInfo {
    
    public static final String ICON_PATH = "textures/icon/";
    
    private static final Map<String, ButtonInfo> BUTTON_REGISTRY = new HashMap<>();
    private static final ArrayList<String> BUILT_INS = new ArrayList<>();
    
    
    // ---- Button Registry Hooks ---- //
    
    /** Returns a list of all buttons built-in to Crust. */
    public static List<String> builtInIds() { return BUILT_INS; }
    
    /** @return The info for button with the given id, or null if none exists. */
    @Nullable
    public static ButtonInfo get( String id ) { return BUTTON_REGISTRY.get( id ); }
    
    /** @return The custom button for a given index. */
    @SuppressWarnings( "unused" )
    public static ButtonInfo getCustom( int index ) { return Objects.requireNonNull( get( customId( index ) ) ); }
    
    /** @return The id for the custom button. */
    public static String customId( int index ) { return "custom" + (index + 1); }
    
    
    // ---- Config Hooks ---- //
    
    /** Called each time a custom button config category is loaded. */
    public static void loadCustomButton( String id, ExtraInvButtonsCrustConfigFile.Button buttonCfg ) {
        BUTTON_REGISTRY.put( id, new ButtonInfo( id, buttonCfg.tooltip.get(), buttonCfg.icon.get(),
                buttonCfg.iconColor.get(), buttonCfg.commands.get() ) );
    }
    
    /** Called to update the 'commands used' list each time the button config is loaded. */
    public static void updateGodModePerms( ExtraInvButtonsCrustConfigFile.BuiltInButtons buttonCfg ) {
        ButtonInfo godModeButton = ButtonInfo.get( "godMode" );
        if( godModeButton == null ) return;
        godModeButton.COMMANDS.clear();
        if( buttonCfg.godModeUndying.get() )
            godModeButton.COMMANDS.add( Command.forMode( CrustModes.UNDYING ) );
        if( buttonCfg.godModeUnbreaking.get() )
            godModeButton.COMMANDS.add( Command.forMode( CrustModes.UNBREAKING ) );
        if( buttonCfg.godModeUneating.get() >= 0.0 )
            godModeButton.COMMANDS.add( Command.forMode( CrustModes.UNEATING ) );
    }
    
    
    // ---- Built-In Button References ---- //
    
    /** Registers a new built-in button. */
    private static ButtonInfo builtIn( ButtonInfo info ) {
        BUTTON_REGISTRY.put( info.ID, info );
        BUILT_INS.add( info.ID );
        return info;
    }
    
    // Utilities
    public static final ButtonInfo FULL_HEAL = builtIn( new ButtonInfo( "fullHeal", "Full recover",
            "instant_health.png", "crustrecover" ) );
    public static final ButtonInfo KILL_ALL = builtIn( new ButtonInfo( "killAll", "Kill all mobs",
            "creeper_slash.png", "kill @e[type=!player]" ) );
    public static final ButtonInfo NETHER_PORTAL = builtIn( new ButtonInfo( "netherPortal", "Create a Nether portal",
            "portal_nether.png", "crustportal nether" ).condition(
            () -> CrustPortalCommand.isDimensionValid( InternalCrustPlugin.NETHER_PORTAL, world() ) ) );
    public static final ButtonInfo END_PORTAL = builtIn( new ButtonInfo( "endPortal", "Create an End portal",
            "portal_end.png", "crustportal end" ).condition(
            () -> CrustPortalCommand.isDimensionValid( InternalCrustPlugin.END_PORTAL, world() ) ) );
    
    // Time control
    public static final ButtonInfo DAY = builtIn( new ButtonInfo( "day", "Set time to day",
            "day.png", Command.TIME_DAY ) );
    public static final ButtonInfo NIGHT = builtIn( new ButtonInfo( "night", "Set time to night",
            "night.png", Command.TIME_NIGHT ) );
    @SuppressWarnings( "unused" )
    public static final ButtonInfo TOGGLE_DAY = builtIn( new ButtonInfo( "toggleDay", "Toggle time to day or night",
            "day_night.png", ButtonInfo::toggleDay, Command.TIME_DAY, Command.TIME_NIGHT ) );
    
    // Weather control
    @SuppressWarnings( "unused" )
    public static final ButtonInfo WEATHER_CLEAR = builtIn( new ButtonInfo( "weatherClear", "Clear weather",
            "rain_slash.png", ( button ) -> cmd( Command.clear() ), Command.WEATHER_CLEAR ) );
    @SuppressWarnings( "unused" )
    public static final ButtonInfo WEATHER_RAIN = builtIn( new ButtonInfo( "weatherRain", "Set weather to rain",
            "rain.png", ( button ) -> cmd( Command.rain() ), Command.WEATHER_RAIN ) );
    public static final ButtonInfo WEATHER_STORM = builtIn( new ButtonInfo( "weatherStorm", "Set weather to thunder",
            "thunder.png", ( button ) -> cmd( Command.thunder() ), Command.WEATHER_THUNDER ) );
    public static final ButtonInfo TOGGLE_RAIN = builtIn( new ButtonInfo( "toggleRain", "Toggle weather to clear or rain",
            "weather_toggle.png", ButtonInfo::toggleRain, Command.WEATHER_CLEAR, Command.WEATHER_RAIN ) );
    
    // Mode toggles
    public static final ButtonInfo GAME_MODE = builtIn( new ButtonInfo( "gameMode", "Toggle game mode",
            "grass.png", ButtonInfo::gameMode, Command.MODE_SURVIVAL, Command.MODE_CREATIVE ).toggle(
            () -> player().isCreative() ) );
    public static final ButtonInfo MAGNET_MODE = builtIn( new ButtonInfo( "magnetMode", "Toggle magnet mode",
            "magnet.png", ButtonInfo::magnetMode, Command.forMode( CrustModes.MAGNET ) ).toggle(
            () -> modeEnabled( CrustModes.MAGNET ) ) );
    public static final ButtonInfo MULTI_MINE_MODE = builtIn( new ButtonInfo( "multiMineMode", "Toggle multi-mine mode",
            "haste.png", ButtonInfo::multiMineMode, Command.forMode( CrustModes.MULTI_MINE ) ).toggle(
            () -> modeEnabled( CrustModes.MULTI_MINE ) ) );
    public static final ButtonInfo GOD_MODE = builtIn( new ButtonInfo( "godMode", "Toggle god mode",
            "undying.png", ButtonInfo::godMode ).toggle(
            () -> {
                ExtraInvButtonsCrustConfigFile.BuiltInButtons buttonCfg = ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS;
                CrustModesData playerModes = CrustModesData.of( player() );
                return !(buttonCfg.godModeUndying.get() && !playerModes.enabled( CrustModes.UNDYING ) ||
                        buttonCfg.godModeUnbreaking.get() && !playerModes.enabled( CrustModes.UNBREAKING ) ||
                        buttonCfg.godModeUneating.get() >= 0.0 && !playerModes.enabled( CrustModes.UNEATING ));
            } ) );
    public static final ButtonInfo SUPER_VISION_MODE = builtIn( new ButtonInfo( "superVisionMode", "Toggle super vision mode",
            "night_vision.png", ButtonInfo::superVisionMode, Command.forMode( CrustModes.SUPER_VISION ) ).toggle(
            () -> modeEnabled( CrustModes.SUPER_VISION ) ) );
    public static final ButtonInfo SUPER_SPEED_MODE = builtIn( new ButtonInfo( "superSpeedMode", "Toggle super speed mode",
            "swiftness.png", ButtonInfo::superSpeedMode, Command.forMode( CrustModes.SUPER_SPEED ) ).toggle(
            () -> modeEnabled( CrustModes.SUPER_SPEED ) ) );
    public static final ButtonInfo NO_PICKUP_MODE = builtIn( new ButtonInfo( "noPickupMode", "Toggle destroy-on-pickup mode",
            "weakness.png", ButtonInfo::noPickupMode, Command.forMode( CrustModes.DESTROY_ON_PICKUP ) ).toggle(
            () -> modeEnabled( CrustModes.DESTROY_ON_PICKUP ) ) );
    
    static {
        BUILT_INS.trimToSize();
    }
    
    
    // ---- Instance Logic ---- //
    
    public final String ID;
    
    public final String TOOLTIP;
    
    /** The text to display on the button if icon is null; otherwise this is null. */
    public final String TEXT;
    /** The image displayed on the button, or null if text should be used instead. */
    public final ResourceLocation ICON;
    /** Color multiplier applied to the button's text/icon. */
    public final int COLOR;
    
    /** Function to run on button press. */
    public final Button.IPressable ON_PRESS;
    
    /** The commands used by this button. This list is used ONLY for checking permissions to calculate {@link #active}. */
    protected final List<String> COMMANDS = new ArrayList<>();
    
    /** Whether the button is able to function. Causes the button to be grayed-out or hidden based on configs. */
    private Supplier<Boolean> enabled;
    /** Whether the button should be grayed-out and non-pressable. */
    private boolean active;
    
    /** Whether the button is 'toggled on'. */
    private Supplier<Boolean> toggledOn;
    
    
    // ---- Button Info Builders ---- //
    
    /** Defines info for a button. */
    public ButtonInfo( String id, String tooltip, String display, int color, Button.IPressable onPress ) {
        ID = id;
        
        TOOLTIP = tooltip;
        
        if( display.endsWith( ".png" ) ) {
            TEXT = "";
            ICON = new ResourceLocation( Crust.MOD_ID, ICON_PATH + display );
        }
        else {
            TEXT = display;
            ICON = null;
        }
        COLOR = color;
        
        ON_PRESS = onPress;
    }
    
    /** Defines info for a button with no color tint. */
    public ButtonInfo( String id, String tooltip, String display, Button.IPressable onPress, String... commands ) {
        this( id, tooltip, display, 0xFFFFFF, onPress );
        COMMANDS.addAll( Arrays.asList( commands ) );
    }
    
    /** Defines info for a simple command button with no color tint. */
    public ButtonInfo( String id, String tooltip, String display, String command ) {
        this( id, tooltip, display, 0xFFFFFF, new ButtonPressCommandChain( Collections.singletonList( command ) ) );
        COMMANDS.add( command );
    }
    
    /** Defines info for a custom button (executes a user-defined command chain on press). */
    public ButtonInfo( String id, String tooltip, String display, int color, List<String> commands ) {
        this( id, tooltip, display, color, new ButtonPressCommandChain( commands ) );
        COMMANDS.addAll( commands );
    }
    
    /** Builder-like method that adds a condition to when the button can be used. */
    protected ButtonInfo condition( Supplier<Boolean> canEnable ) {
        enabled = canEnable;
        return this;
    }
    
    /** Builder-like method that adds a condition to when the button should appear 'toggled on'. */
    protected ButtonInfo toggle( Supplier<Boolean> isToggledOn ) {
        toggledOn = isToggledOn;
        return this;
    }
    
    
    // ---- Button Logic ---- //
    
    /**
     * @return True if the player has adequate permission to use the button's commands.
     * Causes the button to be grayed-out or hidden based on configs.
     */
    public boolean isUsable() {
        if( COMMANDS.isEmpty() ) return false;
        for( String command : COMMANDS ) {
            if( !canUseCommand( command ) ) return false;
        }
        return true;
    }
    
    /**
     * @return True if the button should be enabled.
     * Causes the button to be grayed-out or hidden based on configs.
     */
    public boolean canEnable() { return enabled == null || enabled.get(); }
    
    /** Sets this button's active state. */
    public void setActive( boolean value ) { active = value; }
    
    /** @return True if the button should be active. Inactive buttons are grayed-out and non-pressable. */
    public boolean isActive() { return active; }
    
    /** @return True if the button should appear 'toggled on'. */
    public boolean isToggledOn() { return toggledOn != null && toggledOn.get(); }
    
    private static class ButtonPressCommandChain implements Button.IPressable {
        
        private final List<String> COMMANDS;
        
        ButtonPressCommandChain( List<String> commands ) { COMMANDS = commands; }
        
        @Override
        public void onPress( @Nullable Button button ) {
            for( String command : COMMANDS ) cmd( command );
        }
    }
    
    
    // ---- Built-In Button Impl ---- //
    
    private static void toggleDay( @Nullable Button button ) {
        final int dayTime = (int) (world().getDayTime() % 24_000L);
        cmd( dayTime < 1_000 || dayTime >= 13_000 ? Command.TIME_DAY : Command.TIME_NIGHT );
    }
    
    private static void toggleRain( @Nullable Button button ) {
        cmd( world().getLevelData().isRaining() || world().getLevelData().isThundering() ?
                Command.clear() : Command.rain() );
    }
    
    // isThundering() is not implemented client-side; possibly some other method works?
    //    private static void toggleStorm( @Nullable Button button ) {
    //        Minecraft mc = Minecraft.getInstance();
    //        cmd( mc.level == null || !mc.level.getLevelData().isRaining() || mc.level.getLevelData().isThundering() ?
    //                Command.WEATHER_RAIN : Command.WEATHER_THUNDER );
    //    }
    
    private static void gameMode( @Nullable Button button ) {
        cmd( ButtonInfo.GAME_MODE.isToggledOn() ? Command.MODE_SURVIVAL : Command.MODE_CREATIVE ); // TODO allow player to config modes
    }
    
    private static void magnetMode( @Nullable Button button ) {
        toggleCrustMode( CrustModes.MAGNET, ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.magnetMaxRange.getFloat() );
    }
    
    private static void multiMineMode( @Nullable Button button ) {
        toggleCrustMode( CrustModes.MULTI_MINE, (byte) 1 );
    }
    
    private static void godMode( @Nullable Button button ) {
        ExtraInvButtonsCrustConfigFile.BuiltInButtons buttonCfg = ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS;
        boolean toggleOff = ButtonInfo.GOD_MODE.isToggledOn();
        
        if( buttonCfg.godModeUndying.get() )
            cmd( Command.forMode( CrustModes.UNDYING, toggleOff ? null : (byte) 1 ) );
        if( buttonCfg.godModeUnbreaking.get() )
            cmd( Command.forMode( CrustModes.UNBREAKING, toggleOff ? null : (byte) 1 ) );
        if( buttonCfg.godModeUneating.get() >= 0.0 )
            cmd( Command.forMode( CrustModes.UNEATING, toggleOff ? null : buttonCfg.godModeUneating.getFloat() ) );
    }
    
    private static void superVisionMode( @Nullable Button button ) {
        toggleCrustMode( CrustModes.SUPER_VISION, (byte) 1 );
    }
    
    private static void superSpeedMode( @Nullable Button button ) {
        toggleCrustMode( CrustModes.SUPER_SPEED, ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.superSpeedMulti.getFloat() );
    }
    
    private static void noPickupMode( @Nullable Button button ) {
        toggleCrustMode( CrustModes.DESTROY_ON_PICKUP, (byte) 1 );
    }
    
    /** Sends a command to toggle the Crust mode. */
    private static <T> void toggleCrustMode( CrustMode<T> mode, T value ) {
        cmd( Command.forMode( mode, modeEnabled( mode ) ? null : value ) );
    }
    
    
    // ---- Helper Methods ---- //
    
    /** Runs the given command as this client's player, if possible. */
    private static void cmd( String command ) { player().chat( "/" + command ); }
    
    /** @return True if the client player has the mode enabled. */
    private static boolean modeEnabled( CrustMode<?> mode ) { return CrustModesData.of( player() ).enabled( mode ); }
    
    /** @return The client player. */
    private static ClientPlayerEntity player() { return Objects.requireNonNull( Minecraft.getInstance().player ); }
    
    /** @return The client world. */
    private static ClientWorld world() { return Objects.requireNonNull( Minecraft.getInstance().level ); }
    
    /**
     * @return Parse results for a command based on the client player's command suggestion helper.
     * Will be null if there is no client player (no world loaded).
     */
    @Nullable
    private static ParseResults<ISuggestionProvider> parseCommand( String command ) {
        StringReader reader = new StringReader( org.apache.commons.lang3.StringUtils.normalizeSpace( command ) );
        if( reader.canRead() && reader.peek() == '/' ) reader.skip();
        
        ClientPlayNetHandler connection = player().connection;
        return connection.getCommands().parse( reader, connection.getSuggestionsProvider() );
    }
    
    /** @return True if the client player can use a command, according to its command suggestion helper. */
    private static boolean canUseCommand( String command ) {
        ParseResults<ISuggestionProvider> parse = parseCommand( command );
        return parse != null && !parse.getReader().canRead();
    }
    
    private static class Command {
        
        static final String TIME_DAY = "time set day";
        static final String TIME_NIGHT = "time set night";
        
        static final String WEATHER_CLEAR = "weather clear";
        static final String WEATHER_RAIN = "weather rain";
        static final String WEATHER_THUNDER = "weather thunder";
        
        static final String MODE_SURVIVAL = "gamemode survival";
        static final String MODE_CREATIVE = "gamemode creative";
        
        static String clear() { return WEATHER_CLEAR + " " + ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.weatherDuration.get(); }
        
        static String rain() { return WEATHER_RAIN + " " + ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.weatherDuration.get(); }
        
        static String thunder() { return WEATHER_THUNDER + " " + ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.weatherDuration.get(); }
        
        static <T> String forMode( CrustMode<T> mode ) { return forMode( mode, null ); }
        
        static <T> String forMode( CrustMode<T> mode, @Nullable T value ) {
            return "crustmode " + mode.ID + " " + (value == null ? "disable" : toCmd( value ));
        }
        
        private static String toCmd( Object value ) {
            // Command parser can't handle scientific notation
            if( value instanceof Float ) return String.format( "%f", (Float) value );
            if( value instanceof Double ) return String.format( "%f", (Double) value );
            return value.toString();
        }
    }
}