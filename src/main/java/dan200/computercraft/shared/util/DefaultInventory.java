/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface DefaultInventory extends IInventory
{
    @Override
    default int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    default void openInventory( @Nonnull EntityPlayer player )
    {
    }

    @Override
    default void closeInventory( @Nonnull EntityPlayer player )
    {
    }

    @Override
    default boolean isItemValidForSlot( int slot, @Nonnull ItemStack stack )
    {
        return true;
    }

    @Override
    default int getField( int field )
    {
        return 0;
    }

    @Override
    default void setField( int field, int value )
    {
    }

    @Override
    default int getFieldCount()
    {
        return 0;
    }
}
