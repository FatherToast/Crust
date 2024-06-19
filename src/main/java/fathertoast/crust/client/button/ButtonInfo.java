
package fathertoast.crust.client.button;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fathertoast.crust.api.ICrustApi;
import fathertoast.crust.api.lib.CrustObjects;
import fathertoast.crust.client.ClientRegister;
import fathertoast.crust.client.ExtraInvButtonsCrustConfigFile;
import fathertoast.crust.client.KeyBindingEvents;
import fathertoast.crust.common.mode.CrustModes;
import fathertoast.crust.common.mode.CrustModesData;
import fathertoast.crust.common.mode.type.CrustMode;
import fathertoast.crust.common.portal.CrustPortals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import org.apache.commons.lang3.StringUtils;

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
    public static void loadCustomButton( String id, ExtraInvButtonsCrustConfigFile.CustomButton buttonCfg ) {
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
        if( buttonCfg.godModeUneating.get() > 0 )
            godModeButton.COMMANDS.add( Command.forMode( CrustModes.UNEATING ) );
    }
    
    
    // ---- Built-In Button References ---- //
    
    /** Registers a new built-in button. */
    private static ButtonInfo builtIn( ButtonInfo info ) {
        BUTTON_REGISTRY.put( info.ID, info );
        BUILT_INS.add( info.ID );
        return info;
    }
    
    /** @return The lang key to use for a button with the given id. */
    private static String toLangKey( String id ) { return "inventory.buttons." + id.toLowerCase( Locale.ROOT ) + ".tooltip"; }
    
    // Utilities
    public static final ButtonInfo FULL_HEAL = builtIn( new ButtonInfo( "fullHeal", "instant_health.png",
            "crustrecover" ) );
    public static final ButtonInfo CLEAR_EFFECTS = builtIn( new ButtonInfo( "clearEffects", "milk.png",
            "effect clear" )
            .condition( () -> !player().getActiveEffectsMap().isEmpty() ) );
    public static final ButtonInfo DESTROY_POINTER_ITEM = builtIn( new ButtonInfo( "destroyOnPointer", "fire.png",
            ButtonInfo::destroyOnPointer, Command.CLEAN_POINTER )
            .condition( () -> !player().inventoryMenu.getCarried().isEmpty() ) );
    public static final ButtonInfo KILL_ALL = builtIn( new ButtonInfo( "killAll", "creeper_slash.png",
            "kill @e[type=!player]" ) );
    @SuppressWarnings( "unused" )
    public static final ButtonInfo NETHER_PORTAL = builtIn( new ButtonInfo( "netherPortal", "portal_nether.png",
            "crustportal " + CrustPortals.NETHER_PORTAL )
            .condition( () -> CrustObjects.netherPortal().isValidDimension( world() ) )
            .key( KeyModifier.CONTROL, "0" ) );
    @SuppressWarnings( "unused" )
    public static final ButtonInfo END_PORTAL = builtIn( new ButtonInfo( "endPortal", "portal_end.png",
            "crustportal " + CrustPortals.END_PORTAL )
            .condition( () -> CrustObjects.endPortal().isValidDimension( world() ) )
            .key( KeyModifier.ALT, "0" ) );
    
    // Time control
    public static final ButtonInfo DAY = builtIn( new ButtonInfo( "day", "day.png",
            Command.TIME_DAY ) );
    public static final ButtonInfo NIGHT = builtIn( new ButtonInfo( "night", "night.png",
            Command.TIME_NIGHT ) );
    @SuppressWarnings( "unused" )
    public static final ButtonInfo TOGGLE_DAY = builtIn( new ButtonInfo( "toggleDay", "day_night.png",
            ButtonInfo::toggleDay, Command.TIME_DAY, Command.TIME_NIGHT ) );
    
    // Weather control
    @SuppressWarnings( "unused" )
    public static final ButtonInfo WEATHER_CLEAR = builtIn( new ButtonInfo( "weatherClear", "rain_slash.png",
            ( button ) -> cmd( Command.clear() ), Command.WEATHER_CLEAR ) );
    @SuppressWarnings( "unused" )
    public static final ButtonInfo WEATHER_RAIN = builtIn( new ButtonInfo( "weatherRain", "rain.png",
            ( button ) -> cmd( Command.rain() ), Command.WEATHER_RAIN ) );
    public static final ButtonInfo WEATHER_STORM = builtIn( new ButtonInfo( "weatherStorm", "thunder.png",
            ( button ) -> cmd( Command.thunder() ), Command.WEATHER_THUNDER ) );
    public static final ButtonInfo TOGGLE_RAIN = builtIn( new ButtonInfo( "toggleRain", "weather_toggle.png",
            ButtonInfo::toggleRain, Command.WEATHER_CLEAR, Command.WEATHER_RAIN ) );
    
    // Mode toggles
    public static final ButtonInfo GAME_MODE = builtIn( new ButtonInfo( "gameMode", "grass.png",
            ButtonInfo::gameMode, Command.MODE_SURVIVAL, Command.MODE_CREATIVE )
            .toggle( () -> player().isCreative() ) );
    public static final ButtonInfo MAGNET_MODE = builtIn( new ButtonInfo( "magnetMode", "magnet.png",
            ButtonInfo::magnetMode, Command.forMode( CrustModes.MAGNET ) )
            .toggle( () -> modeEnabled( CrustModes.MAGNET ) )
            .key( KeyModifier.CONTROL, "m" ) );
    //public static final ButtonInfo MULTI_MINE_MODE = builtIn( new ButtonInfo( "multiMineMode", "haste.png",
    //        ButtonInfo::multiMineMode, Command.forMode( CrustModes.MULTI_MINE ) )
    //        .toggle( () -> modeEnabled( CrustModes.MULTI_MINE ) )
    //        .key( KeyModifier.ALT, "m" ) );
    public static final ButtonInfo GOD_MODE = builtIn( new ButtonInfo( "godMode", "undying.png",
            ButtonInfo::godMode )
            .toggle( () -> {
                ExtraInvButtonsCrustConfigFile.BuiltInButtons buttonCfg = ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS;
                CrustModesData playerModes = CrustModesData.of( player() );
                return !(buttonCfg.godModeUndying.get() && !playerModes.enabled( CrustModes.UNDYING ) ||
                        buttonCfg.godModeUnbreaking.get() && !playerModes.enabled( CrustModes.UNBREAKING ) ||
                        buttonCfg.godModeUneating.get() > 0 && !playerModes.enabled( CrustModes.UNEATING ));
            } ) );
    public static final ButtonInfo SUPER_VISION_MODE = builtIn( new ButtonInfo( "superVisionMode", "night_vision.png",
            ButtonInfo::superVisionMode, Command.forMode( CrustModes.SUPER_VISION ) )
            .toggle( () -> modeEnabled( CrustModes.SUPER_VISION ) ) );
    public static final ButtonInfo SUPER_SPEED_MODE = builtIn( new ButtonInfo( "superSpeedMode", "swiftness.png",
            ButtonInfo::superSpeedMode, Command.forMode( CrustModes.SUPER_SPEED ) )
            .toggle( () -> modeEnabled( CrustModes.SUPER_SPEED ) ) );
    public static final ButtonInfo NO_PICKUP_MODE = builtIn( new ButtonInfo( "noPickupMode", "weakness.png",
            ButtonInfo::noPickupMode, Command.forMode( CrustModes.DESTROY_ON_PICKUP ) )
            .toggle( () -> modeEnabled( CrustModes.DESTROY_ON_PICKUP ) ) );
    
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
    public final Button.OnPress ON_PRESS;
    
    /** The commands used by this button. This list is used ONLY for checking permissions to calculate {@link #active}. */
    protected final List<String> COMMANDS = new ArrayList<>();
    
    /** Whether the button is able to function. Causes the button to be grayed-out or hidden based on configs. */
    private Supplier<Boolean> enabled;
    /** Whether the button should be grayed-out and non-pressable. */
    private boolean active;
    
    /** Whether the button is 'toggled on'. */
    private Supplier<Boolean> toggledOn;
    
    /** This button's default key binding. */
    private KeyBindingEvents.Key keyBinding;
    
    
    // ---- Button Info Builders ---- //
    
    /** Defines info for a button. */
    public ButtonInfo( String id, String tooltip, String display, int color, Button.OnPress onPress ) {
        ID = id;
        
        TOOLTIP = tooltip;
        
        if( display.endsWith( ".png" ) ) {
            TEXT = "";
            ICON = new ResourceLocation( ICrustApi.MOD_ID, ICON_PATH + display );
        }
        else {
            TEXT = display;
            ICON = null;
        }
        COLOR = color;
        
        ON_PRESS = onPress;
    }
    
    /** Defines info for a button with no color tint. */
    public ButtonInfo( String id, String display, Button.OnPress onPress, String... commands ) {
        this( id, toLangKey( id ), display, 0xFFFFFF, onPress );
        COMMANDS.addAll( Arrays.asList( commands ) );
    }
    
    /** Defines info for a simple command button with no color tint. */
    public ButtonInfo( String id, String display, String command ) {
        this( id, toLangKey( id ), display, 0xFFFFFF, new ButtonPressCommandChain( Collections.singletonList( command ) ) );
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
    
    /** Builder-like method that adds a default key binding to this button. */
    protected ButtonInfo key( KeyModifier modifier, String key ) {
        keyBinding = KeyBindingEvents.Key.of( modifier, key );
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
    
    /** @return True if the button should be enabled. Causes the button to be grayed-out when false. */
    public boolean canEnable() { return enabled == null || enabled.get(); }
    
    /** Sets this button's active state. When false, the button will be forcibly disabled. */
    public void setCanBeActive( boolean value ) { active = value; }
    
    /** @return True if the button should be active. Inactive buttons are grayed-out and non-pressable. */
    public boolean canBeActive() { return active; }
    
    /** @return True if the button should appear 'toggled on'. */
    public boolean isToggledOn() { return toggledOn != null && toggledOn.get(); }
    
    private static class ButtonPressCommandChain implements Button.OnPress {
        
        private final List<String> COMMANDS;
        
        ButtonPressCommandChain( List<String> commands ) { COMMANDS = commands; }
        
        @Override
        public void onPress( @Nullable Button button ) {
            for( String command : COMMANDS ) cmd( command );
        }
    }
    
    /** @return This button's default key binding, or null if it should be unbound by default. */
    @Nullable
    public KeyBindingEvents.Key getDefaultKey() { return keyBinding; }
    
    
    // ---- Built-In Button Impl ---- //
    
    private static void destroyOnPointer( @Nullable Button button ) {
        if( Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen ) {
            player().inventoryMenu.setCarried( ItemStack.EMPTY );
        }
        else {
            cmd( Command.CLEAN_POINTER );
        }
    }
    
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
    
    //private static void multiMineMode( @Nullable Button button ) {
    //    toggleCrustMode( CrustModes.MULTI_MINE, 1 );
    //}
    
    private static void godMode( @Nullable Button button ) {
        ExtraInvButtonsCrustConfigFile.BuiltInButtons buttonCfg = ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS;
        boolean toggleOff = ButtonInfo.GOD_MODE.isToggledOn();
        
        if( buttonCfg.godModeUndying.get() )
            cmd( Command.forMode( CrustModes.UNDYING, toggleOff ? null : (byte) 1 ) );
        if( buttonCfg.godModeUnbreaking.get() )
            cmd( Command.forMode( CrustModes.UNBREAKING, toggleOff ? null : (byte) 1 ) );
        if( buttonCfg.godModeUneating.get() > 0 )
            cmd( Command.forMode( CrustModes.UNEATING, toggleOff ? null : buttonCfg.godModeUneating.getByte() ) );
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
    private static void cmd( String command ) { player().sendSystemMessage(Component.literal("/" + command )); }
    
    /** @return True if the client player has the mode enabled. */
    private static boolean modeEnabled( CrustMode<?> mode ) { return CrustModesData.of( player() ).enabled( mode ); }
    
    /** @return The client player. */
    private static LocalPlayer player() { return Objects.requireNonNull( Minecraft.getInstance().player ); }
    
    /** @return The client world. */
    private static ClientLevel world() { return Objects.requireNonNull( Minecraft.getInstance().level ); }
    
    /**
     * @return Parse results for a command based on the client player's command suggestion helper.
     * Will be null if there is no client player (no world loaded).
     */
    @Nullable
    private static ParseResults<SharedSuggestionProvider> parseCommand(String command ) {
        StringReader reader = new StringReader( StringUtils.normalizeSpace( command ) );
        if( reader.canRead() && reader.peek() == '/' ) reader.skip();
        
        ClientPacketListener connection = player().connection;
        return connection.getCommands().parse( reader, connection.getSuggestionsProvider() );
    }
    
    /** @return True if the client player can use a command, according to its command suggestion helper. */
    private static boolean canUseCommand( String command ) {
        ParseResults<SharedSuggestionProvider> parse = parseCommand( command );
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
        
        static final String CLEAN_POINTER = "crustclean pointer";
        
        static String clear() { return WEATHER_CLEAR + " " + ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.weatherDuration.get(); }
        
        static String rain() { return WEATHER_RAIN + " " + ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.weatherDuration.get(); }
        
        static String thunder() { return WEATHER_THUNDER + " " + ClientRegister.EXTRA_INV_BUTTONS.BUILT_IN_BUTTONS.weatherDuration.get(); }
        
        static <T> String forMode( CrustMode<T> mode ) { return forMode( mode, null ); }
        
        static <T> String forMode( CrustMode<T> mode, @Nullable T value ) {
            return "crustmode " + mode.ID + " " + (value == null ? "disable" : toCmd( value ));
        }
        
        private static String toCmd( Object value ) {
            // Command parser can't handle scientific notation or comma decimal separators
            if( value instanceof Float ) return String.format( Locale.ROOT, "%f", (Float) value );
            if( value instanceof Double ) return String.format( Locale.ROOT, "%f", (Double) value );
            return value.toString();
        }
    }
}