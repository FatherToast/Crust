
package fathertoast.crust.client.button;

import fathertoast.crust.client.ExtraInvButtonsCrustConfigFile;
import fathertoast.crust.common.core.Crust;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
        builtIn( new ButtonInfo( "fullHeal", "Full recover", "instant_health.png", ( button ) -> { } ) );//TODO
        builtIn( new ButtonInfo( "killAll", "Kill all mobs", "creeper_slash.png", "kill @e[type=!player]" ) );
        builtIn( new ButtonInfo( "netherPortal", "Create a Nether portal", "portal_nether.png",
                ButtonInfo::netherPortal ).condition( () -> {
            Minecraft mc = Minecraft.getInstance();
            return mc.level != null && (mc.level.dimension() == World.OVERWORLD || mc.level.dimension() == World.NETHER);
        } ) );
        builtIn( new ButtonInfo( "endPortal", "Create an End portal", "portal_end.png",
                ButtonInfo::endPortal ).condition( () -> {
            Minecraft mc = Minecraft.getInstance();
            return mc.level != null && (mc.level.dimension() == World.OVERWORLD || mc.level.dimension() == World.END);
        } ) );
        
        // Time control
        builtIn( new ButtonInfo( "day", "Set time to day", "day.png", "time set day" ) );
        builtIn( new ButtonInfo( "night", "Set time to night", "night.png", "time set night" ) );
        builtIn( new ButtonInfo( "toggleDay", "Toggle time to day or night", "day_night.png", ButtonInfo::toggleDay ) );
        
        // Weather control
        builtIn( new ButtonInfo( "weatherClear", "Clear weather", "rain_slash.png", "weather clear" ) );
        builtIn( new ButtonInfo( "weatherRain", "Set weather to rain", "rain.png", "weather rain" ) );
        builtIn( new ButtonInfo( "weatherStorm", "Set weather to thunder", "thunder.png", "weather thunder" ) );
        builtIn( new ButtonInfo( "toggleRain", "Toggle weather to clear or rain", "weather_toggle.png", ButtonInfo::toggleRain ) );
        //builtIn( new ButtonInfo( "toggleStorm", "Toggle weather to rain or thunder", "rain_toggle.png", ButtonInfo::toggleStorm ) );
        
        // Mode toggles
        builtIn( new ButtonInfo( "gameMode", "Toggle game mode", "grass.png", ButtonInfo::gameMode ) );
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
    
    public final Button.IPressable ON_PRESS;
    
    private Supplier<Boolean> enabled;
    
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
    public ButtonInfo( String id, String tooltip, String display, Button.IPressable onPress ) {
        this( id, tooltip, display, 0xFFFFFF, onPress );
    }
    
    /** Defines info for a simple command button with no color tint. */
    public ButtonInfo( String id, String tooltip, String display, String command ) {
        this( id, tooltip, display, 0xFFFFFF, new ButtonPressCommandChain( Collections.singletonList( command ) ) );
    }
    
    /** Defines info for a custom button (executes a user-defined command chain on press). */
    public ButtonInfo( String id, String tooltip, String display, int color, List<String> commands ) {
        this( id, tooltip, display, color, new ButtonPressCommandChain( commands ) );
    }
    
    /** Builder-like method that adds a condition to when the button can be used. */
    private ButtonInfo condition( Supplier<Boolean> canEnable ) {
        enabled = canEnable;
        return this;
    }
    
    /** @return True if the button should be enabled. */
    public boolean canEnable() { return enabled == null || enabled.get(); }
    
    
    private static class ButtonPressCommandChain implements Button.IPressable {
        
        private final List<String> COMMANDS;
        
        ButtonPressCommandChain( List<String> commands ) { COMMANDS = commands; }
        
        @Override
        public void onPress( Button button ) {
            for( String command : COMMANDS ) cmd( command );
        }
    }
    
    /** Runs the given command as this client's player, if possible. */
    private static void cmd( String command ) {
        if( Minecraft.getInstance().player != null ) Minecraft.getInstance().player.chat( "/" + command );
    }
    
    
    private static void netherPortal( Button button ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.level != null && (mc.level.dimension() == World.OVERWORLD || mc.level.dimension() == World.NETHER) ) {
            //cmd( "" ); TODO
        }
    }
    
    private static void endPortal( Button button ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.level != null && (mc.level.dimension() == World.OVERWORLD || mc.level.dimension() == World.END) ) {
            //cmd( "" ); TODO
        }
    }
    
    private static void toggleDay( Button button ) {
        Minecraft mc = Minecraft.getInstance();
        final int dayTime = mc.level == null ? 0 : (int) (mc.level.getDayTime() % 24_000L);
        cmd( dayTime < 1_000 || dayTime >= 13_000 ? "time set day" : "time set night" );
    }
    
    private static void toggleRain( Button button ) {
        Minecraft mc = Minecraft.getInstance();
        cmd( mc.level == null || mc.level.getLevelData().isRaining() || mc.level.getLevelData().isThundering() ?
                "weather clear" : "weather rain" );
    }
    
    // Not currently viable; isThundering() in not implemented client-side
    //    private static void toggleStorm( Button button ) {
    //        Minecraft mc = Minecraft.getInstance();
    //        cmd( mc.level == null || !mc.level.getLevelData().isRaining() || mc.level.getLevelData().isThundering() ?
    //                "weather rain" : "weather thunder" );
    //    }
    
    private static void gameMode( Button button ) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.player == null ) return;
        cmd( !mc.player.isCreative() ? "gamemode creative" : "gamemode survival" ); // TODO allow player to config modes?
    }
}