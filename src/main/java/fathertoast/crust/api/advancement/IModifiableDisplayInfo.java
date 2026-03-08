package fathertoast.crust.api.advancement;

import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;


/**
 * Helper interface for modifying the display info of
 * advancements when they are being loaded from JSON.
 * <br>
 * Used in {@link IModifiableAdvancement}.
 */
public interface IModifiableDisplayInfo {
    
    
    /** @return The title component of this display info. */
    Component getTitle();
    
    /** Sets the title component of this display info. */
    void setTitle( Component title );
    
    
    /** @return The description component of this display info. */
    Component getDescription();
    
    /** Sets the description component of this display info. */
    void setDescription( Component description );
    
    
    /** @return The icon ItemStack of this display info. */
    ItemStack getIconItem();
    
    /** Sets the icon ItemStack for this display info. */
    void setIconItem( ItemStack icon );
    
    /** Sets the icon ItemStack for this display info. */
    void setIconItem( Item icon );
    
    /**
     * @return The background texture location of this display info.
     * Can be null.
     */
    @Nullable
    ResourceLocation getBackgroundLocation();
    
    /**
     * Sets the background texture location for this display info.
     * Setting this to null is safe.
     */
    void setBackgroundLocation( @Nullable ResourceLocation location );
    
    
    /**
     * @return The {@link FrameType} of this display info.
     */
    FrameType getFrameType();
    
    /** Sets the {@link FrameType} for this display info. */
    void setFrameType( FrameType type );
    
    
    /** @return True if a toast popup should be displayed in the corner of the screen. */
    boolean showToast();
    
    /** Sets the "show toast" property for this display info. */
    void setShowToast( boolean showToast );
    
    
    /** @return True if completion of the advancement should be broadcast in chat. */
    boolean announceChat();
    
    /** Sets the "announce chat" property for this display info. */
    void setAnnounceChat( boolean announceChat );
    
    
    /**
     * @return True if the advancement is hidden and cannot be seen
     * in the advancement tree until it has been unlocked.
     */
    boolean isHidden();
    
    /** Sets the "hidden" property for this display info. */
    void setHidden( boolean hidden );
    
    
    /** @return The X-position of the advancement in the advancement tree. */
    float getX();
    
    /** Sets the X-position for this display info. */
    void setX( float x );
    
    
    /** @return The Y-position of the advancement in the advancement tree. */
    float getY();
    
    /** Sets the Y-position for this display info. */
    void setY( float y );
}
