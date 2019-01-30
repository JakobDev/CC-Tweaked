/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DefaultInventory extends IInventory
{
    @Nonnull
    @Override
    default ItemStack decrStackSize( int slot, int count )
    {
        ItemStack stack = getStackInSlot( slot ).split( count );
        if( !stack.isEmpty() ) markDirty( slot );
        return stack;
    }

    @Nonnull
    @Override
    default ItemStack removeStackFromSlot( int slot )
    {
        ItemStack stack = getStackInSlot( slot );
        if( !stack.isEmpty() ) setInventorySlotContents( slot, ItemStack.EMPTY );
        return stack;
    }

    @Override
    default int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    default void openInventory( @Nonnull EntityPlayer playerEntity )
    {
    }

    @Override
    default void closeInventory( @Nonnull EntityPlayer playerEntity )
    {
    }

    @Override
    default boolean isItemValidForSlot( int i, @Nonnull ItemStack itemStack )
    {
        return true;
    }

    @Override
    default int getField( int key )
    {
        return 0;
    }

    @Override
    default void setField( int key, int value )
    {
    }

    @Override
    default int getFieldCount()
    {
        return 0;
    }

    default void markDirty( int slot )
    {
        markDirty();
    }

    @Override
    default boolean hasCustomName()
    {
        return getCustomName() != null;
    }

    @Nullable
    @Override
    default ITextComponent getCustomName()
    {
        return null;
    }
}
