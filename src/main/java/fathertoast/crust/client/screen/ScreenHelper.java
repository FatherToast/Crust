package fathertoast.crust.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ScreenHelper {
    /**
     * Attempts to equip the item currently being dragged around by the pointer, or whatever the pointer is over.
     *
     * @return True if further processing of mouse input should be canceled.
     */
    public static boolean equip( Screen screen ) {
        Minecraft client = Minecraft.getInstance();
        if( client.player != null ) {
            ItemStack carried = client.player.containerMenu.getCarried();
            if( !carried.isEmpty() ) {
                return equip( client.player, carried );
            }
            else if( screen instanceof AbstractContainerScreen<?> containerScreen ) {
                Slot hoveredSlot = containerScreen.getSlotUnderMouse();
                if( hoveredSlot != null && hoveredSlot.hasItem() )
                    return equip( client.player, hoveredSlot.getItem() );
            }
        }
        return false;
    }
    
    /** @return Attempts to equip the item to a non-main-hand slot and returns true if the item was equipped. */
    public static boolean equip( LocalPlayer player, ItemStack item ) {
        EquipmentSlot slot = Mob.getEquipmentSlotForItem( item );
        if( slot != EquipmentSlot.MAINHAND ) {
            //TODO figure out how to unequip any existing item in the slot and then equip the item in an orderly manner
            //  Maybe should send a custom packet to server?
            return true;
        }
        return false;
    }
    
    
    // Static helper, no instantiation
    private ScreenHelper() {}
}