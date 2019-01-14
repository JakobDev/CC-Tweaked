/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface DefaultInventory extends Inventory
{
    @Nonnull
    @Override
    default ItemStack takeInvStack( int slot, int count )
    {
        ItemStack stack = getInvStack( slot ).split( count );
        if( !stack.isEmpty() ) markDirty( slot );
        return stack;
    }

    @Nonnull
    @Override
    default ItemStack removeInvStack( int slot )
    {
        ItemStack stack = getInvStack( slot );
        if( !stack.isEmpty() ) setInvStack( slot, ItemStack.EMPTY );
        return stack;
    }

    default void markDirty( int slot )
    {
        markDirty();
    }
}
