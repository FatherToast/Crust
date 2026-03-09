package fathertoast.crust.common.api.impl.event.advancement;

import fathertoast.crust.api.event.advancement.IModifiableDisplayInfo;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;


/** The default implementation of {@link IModifiableDisplayInfo} . */
public class ModifiableDisplayInfo implements IModifiableDisplayInfo {
    
    private Component title;
    private Component description;
    
    private ItemStack icon;
    
    @Nullable
    private ResourceLocation background;
    
    private FrameType frame;
    
    private boolean showToast;
    private boolean announceChat;
    private boolean hidden;
    
    private float x;
    private float y;
    
    
    /**
     * @return A new ModifiableDisplayInfo instance with all the
     * display info data from the specified DisplayInfo object copied over,
     * or null if we are copying from null.
     */
    @Nullable
    public static ModifiableDisplayInfo copyFrom( @Nullable DisplayInfo displayInfo ) {
        if( displayInfo == null ) return null;
        return new ModifiableDisplayInfo( displayInfo );
    }
    
    /**
     * @return A new DisplayInfo instance with all the
     * display info data from the specified IModifiableDisplayInfo object copied over,
     * or null if we are converting null.
     */
    @Nullable
    public static DisplayInfo convertToVanilla( @Nullable IModifiableDisplayInfo modifiableDisplayInfo ) {
        if( modifiableDisplayInfo == null ) return null;
        
        return new DisplayInfo(
                modifiableDisplayInfo.getIconItem(),
                modifiableDisplayInfo.getTitle(),
                modifiableDisplayInfo.getDescription(),
                modifiableDisplayInfo.getBackgroundLocation(),
                modifiableDisplayInfo.getFrameType(),
                modifiableDisplayInfo.showToast(),
                modifiableDisplayInfo.announceChat(),
                modifiableDisplayInfo.isHidden()
        );
    }
    
    protected ModifiableDisplayInfo( DisplayInfo displayInfo ) {
        this.title = displayInfo.getTitle();
        this.description = displayInfo.getDescription();
        this.icon = displayInfo.getIcon();
        this.background = displayInfo.getBackground();
        this.frame = displayInfo.getFrame();
        this.showToast = displayInfo.shouldShowToast();
        this.announceChat = displayInfo.shouldAnnounceChat();
        this.hidden = displayInfo.isHidden();
        this.x = displayInfo.getX();
        this.y = displayInfo.getY();
    }
    
    /**
     * Creates a "blank" instance that is
     * technically usable and safe.
     */
    protected ModifiableDisplayInfo() {
        this.title = Component.empty();
        this.description = Component.empty();
        this.icon = ItemStack.EMPTY;
        this.frame = FrameType.TASK;
        this.background = null;
        this.showToast = false;
        this.announceChat = false;
        this.hidden = false;
    }
    
    
    @Override
    public Component getTitle() {
        return title;
    }
    
    @Override
    public void setTitle( Component title ) {
        Objects.requireNonNull( title );
        this.title = title;
    }
    
    @Override
    public Component getDescription() {
        return description;
    }
    
    @Override
    public void setDescription( Component description ) {
        Objects.requireNonNull( description );
        this.description = description;
    }
    
    @Override
    public ItemStack getIconItem() {
        return icon;
    }
    
    @Override
    public void setIconItem( ItemStack iconItem ) {
        Objects.requireNonNull( iconItem );
        this.icon = iconItem;
    }
    
    @Override
    public void setIconItem( Item iconItem ) {
        Objects.requireNonNull( iconItem );
        setIconItem( new ItemStack( iconItem ) );
    }
    
    @Override
    @Nullable
    public ResourceLocation getBackgroundLocation() {
        return background;
    }
    
    @Override
    public void setBackgroundLocation( @Nullable ResourceLocation location ) {
        this.background = location;
    }
    
    @Override
    public FrameType getFrameType() {
        return frame;
    }
    
    @Override
    public void setFrameType( FrameType type ) {
        Objects.requireNonNull( type );
        this.frame = type;
    }
    
    @Override
    public boolean showToast() {
        return showToast;
    }
    
    @Override
    public void setShowToast( boolean showToast ) {
        this.showToast = showToast;
    }
    
    @Override
    public boolean announceChat() {
        return announceChat;
    }
    
    @Override
    public void setAnnounceChat( boolean announceChat ) {
        this.announceChat = announceChat;
    }
    
    @Override
    public boolean isHidden() {
        return hidden;
    }
    
    @Override
    public void setHidden( boolean hidden ) {
        this.hidden = hidden;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
}
