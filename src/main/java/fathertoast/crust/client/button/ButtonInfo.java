
package fathertoast.crust.client.button;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import fathertoast.crust.api.impl.InternalCrustPlugin;
import fathertoast.crust.client.ExtraInvButtonsCrustConfigFile;
import fathertoast.crust.common.command.impl.CrustPortalCommand;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class ButtonInfo {
    
    public static final String ICON_PATH = "textures/icon/";
    
    private static final Map<String, ButtonInfo> BUTTON_REGISTRY = new HashMap<>();
    private static final ArrayList<String> BUILT_INS = new ArrayList<>();
    
    public static List<String> builtInIds() { return BUILT_INS; }
    
    @Nullable
    public static ButtonInfo get( String id ) { return BUTTON_REGISTRY.get( id ); }
    
    /** Called each time a custom button config category is loaded. */
    public static void loadCustomButton( String id, ExtraInvButtonsCrustConfigFile.Button buttonCfg ) {
        BUTTON_REGISTRY.put( id, new ButtonInfo( id, buttonCfg.tooltip.get(), buttonCfg.icon.get(),
                buttonCfg.iconColor.get(), buttonCfg.commands.get() ) );
    }
    
    private static void builtIn( ButtonInfo info ) {
        BUTTON_REGISTRY.put( info.ID, info );
        BUILT_INS.add( info.ID );
    }
    
    static {
        // Utilities
        builtIn( new ButtonInfo( "fullHeal", "Full recover", "instant_health.png", "crustrecover" ) );
        builtIn( new ButtonInfo( "killAll", "Kill all mobs", "creeper_slash.png", "kill @e[type=!player]" ) );
        builtIn( new ButtonInfo( "netherPortal", "Create a Nether portal", "portal_nether.png", "crustportal nether" )
                .condition( () -> {
                    Minecraft mc = Minecraft.getInstance();
                    return mc.level != null && CrustPortalCommand.isDimensionValid( InternalCrustPlugin.NETHER_PORTAL, mc.level );
                } ) );
        builtIn( new ButtonInfo( "endPortal", "Create an End portal", "portal_end.png", "crustportal end" )
                .condition( () -> {
                    Minecraft mc = Minecraft.getInstance();
                    return mc.level != null && CrustPortalCommand.isDimensionValid( InternalCrustPlugin.END_PORTAL, mc.level );
                } ) );
        
        // Time control
        builtIn( new ButtonInfo( "day", "Set time to day", "day.png", Command.TIME_DAY ) );
        builtIn( new ButtonInfo( "night", "Set time to night", "night.png", Command.TIME_NIGHT ) );
        builtIn( new ButtonInfo( "toggleDay", "Toggle time to day or night", "day_night.png",
                ButtonInfo::toggleDay, Command.TIME_DAY, Command.TIME_NIGHT ) );
        
        // Weather control
        builtIn( new ButtonInfo( "weatherClear", "Clear weather", "rain_slash.png", Command.WEATHER_CLEAR ) );
        builtIn( new ButtonInfo( "weatherRain", "Set weather to rain", "rain.png", Command.WEATHER_RAIN ) );
        builtIn( new ButtonInfo( "weatherStorm", "Set weather to thunder", "thunder.png", Command.WEATHER_THUNDER ) );
        builtIn( new ButtonInfo( "toggleRain", "Toggle weather to clear or rain", "weather_toggle.png",
                ButtonInfo::toggleRain, Command.WEATHER_CLEAR, Command.WEATHER_RAIN ) );
        //builtIn( new ButtonInfo( "toggleStorm", "Toggle weather to rain or thunder", "rain_toggle.png", ButtonInfo::toggleStorm ) );
        
        // Mode toggles
        builtIn( new ButtonInfo( "gameMode", "Toggle game mode", "grass.png",
                ButtonInfo::gameMode, Command.MODE_SURVIVAL, Command.MODE_CREATIVE ) );
        builtIn( new ButtonInfo( "magnetMode", "Toggle magnet mode", "magnet.png", ( button ) -> { } ) );//TODO
        builtIn( new ButtonInfo( "multiMineMode", "Toggle multi-mine mode", "haste.png", ( button ) -> { } ) );//TODO
        builtIn( new ButtonInfo( "godMode", "Toggle god mode", "undying.png", ( button ) -> { } ) );//TODO
        builtIn( new ButtonInfo( "superVisionMode", "Toggle super vision mode", "night_vision.png", ( button ) -> { } ) );//TODO
        builtIn( new ButtonInfo( "superSpeedMode", "Toggle super speed mode", "swiftness.png", ( button ) -> { } ) );//TODO
        builtIn( new ButtonInfo( "noPickupMode", "Toggle destroy-on-pickup mode", "weakness.png", ( button ) -> { } ) );//TODO
        
        BUILT_INS.trimToSize();
    }
    
    
    public final String ID;
    
    public final String TOOLTIP;
    
    public final String TEXT;
    public final ResourceLocation ICON;
    public final int COLOR;
    
    public final List<String> COMMANDS = new ArrayList<>();
    public final Button.IPressable ON_PRESS;
    
    private Supplier<Boolean> enabled;
    
    private boolean active;
    
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
    private ButtonInfo condition( Supplier<Boolean> canEnable ) {
        enabled = canEnable;
        return this;
    }
    
    /** @return True if the button should be enabled. */
    public boolean isUsable() {
        for( String command : COMMANDS ) {
            if( !canUseCommand( command ) ) return false;
        }
        return true;
    }
    
    /** @return True if the button should be enabled. */
    public boolean canEnable() { return enabled == null || enabled.get(); }
    
    /** Sets this button's active state. */
    public void setActive( boolean value ) { active = value; }
    
    /** @return True if the button should be active. */
    public boolean isActive() { return active; }
    
    
    private static class ButtonPressCommandChain implements Button.IPressable {
        
        private final List<String> COMMANDS;
        
        ButtonPressCommandChain( List<String> commands ) { COMMANDS = commands; }
        
        @Override
        public void onPress( @Nullable Button button ) {
            for( String command : COMMANDS ) cmd( command );
        }
    }
    
    /** Runs the given command as this client's player, if possible. */
    private static void cmd( String command ) {
        if( Minecraft.getInstance().player != null ) Minecraft.getInstance().player.chat( "/" + command );
    }
    
    
    private static void toggleDay( @Nullable Button button ) {
        Minecraft mc = Minecraft.getInstance();
        final int dayTime = mc.level == null ? 0 : (int) (mc.level.getDayTime() % 24_000L);
        cmd( dayTime < 1_000 || dayTime >= 13_000 ? Command.TIME_DAY : Command.TIME_NIGHT );
    }
    
    private static void toggleRain( @Nullable Button button ) {
        Minecraft mc = Minecraft.getInstance();
        cmd( mc.level == null || mc.level.getLevelData().isRaining() || mc.level.getLevelData().isThundering() ?
                Command.WEATHER_CLEAR : Command.WEATHER_RAIN );
    }
    
    // Not currently viable; isThundering() in not implemented client-side
    //    private static void toggleStorm( @Nullable Button button ) {
    //        Minecraft mc = Minecraft.getInstance();
    //        cmd( mc.level == null || !mc.level.getLevelData().isRaining() || mc.level.getLevelData().isThundering() ?
    //                Command.WEATHER_RAIN : Command.WEATHER_THUNDER );
    //    }
    
    private static void gameMode( @Nullable Button button ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player == null ) return;
        cmd( !mc.player.isCreative() ? Command.MODE_CREATIVE : Command.MODE_SURVIVAL ); // TODO allow player to config modes?
    }
    
    
    /**
     * @return Parse results for a command based on the client player's command suggestion helper.
     * Will be null if there is no client player (no world loaded).
     */
    @Nullable
    private static ParseResults<ISuggestionProvider> parseCommand( String command ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player == null ) return null;
        
        StringReader reader = new StringReader( org.apache.commons.lang3.StringUtils.normalizeSpace( command ) );
        if( reader.canRead() && reader.peek() == '/' ) reader.skip();
        
        return mc.player.connection.getCommands().parse( reader, mc.player.connection.getSuggestionsProvider() );
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
    }
}