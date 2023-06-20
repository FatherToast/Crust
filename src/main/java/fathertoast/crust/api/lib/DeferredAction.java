package fathertoast.crust.api.lib;

import fathertoast.crust.api.ICrustApi;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This helper class is used to queue work to be done at the end of the server tick.
 */
@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber( modid = ICrustApi.MOD_ID )
public class DeferredAction {
    
    /**
     * Queues an action to perform at the end of the server tick. Will be called at the end
     * of each server tick until the supplier returns 'true'.
     */
    public static void queue( Supplier<Boolean> action ) { TICK_END_ACTIONS.add( action ); }
    
    /**
     * Queues an action to perform at the end of the server tick. Will be called at the end
     * of each server tick until the supplier returns 'true'.
     *
     * @param delay Number of ticks to wait before performing the action.
     */
    public static void queue( int delay, Supplier<Boolean> action ) {
        TICK_END_ACTIONS.add( new DelayedAction( delay, action ) );
    }
    
    
    // ---- Internal Methods ---- //
    
    /** All actions currently waiting to be performed at the end of the server tick. */
    private static final List<Supplier<Boolean>> TICK_END_ACTIONS = new ArrayList<>();
    
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onServerTick( TickEvent.ServerTickEvent event ) {
        if( event.phase == TickEvent.Phase.END && !TICK_END_ACTIONS.isEmpty() ) {
            TICK_END_ACTIONS.removeIf( Supplier::get );
        }
    }
    
    private static class DelayedAction implements Supplier<Boolean> {
        int delayRemaining;
        final Supplier<Boolean> underlyingAction;
        
        DelayedAction( int delay, Supplier<Boolean> action ) {
            delayRemaining = delay;
            underlyingAction = action;
        }
        
        @Override
        public Boolean get() {
            if( delayRemaining <= 0 ) return underlyingAction.get();
            
            delayRemaining--;
            return false;
        }
    }
}