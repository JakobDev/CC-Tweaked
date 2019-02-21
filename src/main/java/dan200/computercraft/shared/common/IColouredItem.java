/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.common;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IColouredItem
{
    String TAG_COLOUR = "color";

    default int getColour( ItemStack stack )
    {
        return getColourBasic( stack );
    }

    default ItemStack withColour( ItemStack stack, int colour )
    {
        ItemStack copy = stack.copy();
        setColourBasic( copy, colour );
        return copy;
    }

    static int getColourBasic( ItemStack stack )
    {
        NBTTagCompound tag = stack.getTag();
        return tag != null && tag.contains( TAG_COLOUR ) ? tag.getInt( TAG_COLOUR ) : -1;
    }

    static void setColourBasic( ItemStack stack, int colour )
    {
        if( colour == -1 )
        {
            NBTTagCompound tag = stack.getTag();
            if( tag != null ) tag.remove( TAG_COLOUR );
        }
        else
        {
            stack.getOrCreateTag().putInt( TAG_COLOUR, colour );
        }
    }
}
