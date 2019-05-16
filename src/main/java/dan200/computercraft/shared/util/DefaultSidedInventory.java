/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.util;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public interface DefaultSidedInventory extends DefaultInventory, ISidedInventory
{
    @Override
    default boolean canInsertItem( int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side )
    {
        return isItemValidForSlot( slot, stack );
    }

    @Override
    default boolean canExtractItem( int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side )
    {
        return true;
    }
}
