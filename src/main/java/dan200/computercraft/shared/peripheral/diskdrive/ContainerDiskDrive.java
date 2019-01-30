/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.diskdrive;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerDiskDrive extends Container
{
    private final TileDiskDrive m_diskDrive;

    public ContainerDiskDrive( IInventory playerInventory, TileDiskDrive diskDrive )
    {
        m_diskDrive = diskDrive;
        addSlot( new Slot( m_diskDrive, 0, 8 + 4 * 18, 35 ) );

        for( int y = 0; y < 3; y++ )
        {
            for( int x = 0; x < 9; x++ )
            {
                addSlot( new Slot( playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 ) );
            }
        }

        for( int x = 0; x < 9; x++ )
        {
            addSlot( new Slot( playerInventory, x, 8 + x * 18, 142 ) );
        }
    }

    public TileDiskDrive getDiskDrive()
    {
        return m_diskDrive;
    }

    @Override
    public boolean canInteractWith( @Nonnull EntityPlayer player )
    {
        return m_diskDrive.isUsableByPlayer( player );
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot( EntityPlayer payer, int slotIndex )
    {
        Slot slot = inventorySlots.get( slotIndex );
        if( slot == null || !slot.getHasStack() ) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getStack();
        ItemStack result = slotStack.copy();
        if( slotIndex == 0 )
        {
            // Insert into player inventory
            if( !mergeItemStack( slotStack, 1, 37, true ) ) return ItemStack.EMPTY;
        }
        else
        {
            // Insert into disk inventory
            if( !mergeItemStack( slotStack, 0, 1, false ) ) return ItemStack.EMPTY;
        }

        // Update the slot
        if( slotStack.isEmpty() )
        {
            slot.putStack( ItemStack.EMPTY );
        }
        else
        {
            slot.onSlotChanged();
        }

        if( slotStack.getCount() == result.getCount() ) return ItemStack.EMPTY;
        slot.onTake( payer, slotStack );
        return result;
    }
}
