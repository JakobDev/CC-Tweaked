/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.items;

import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public interface IComputerItem
{
    String NBT_ID = "computerID";

    default int getComputerID( @Nonnull ItemStack stack )
    {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null && nbt.hasKey( NBT_ID ) ? nbt.getInteger( NBT_ID ) : -1;
    }

    default String getLabel( @Nonnull ItemStack stack )
    {
        return stack.hasDisplayName() ? stack.getDisplayName() : null;
    }

    ComputerFamily getFamily( @Nonnull ItemStack stack );

    ItemStack withFamily( @Nonnull ItemStack stack, @Nonnull ComputerFamily family );
}
