/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.util;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

import static dan200.computercraft.shared.util.NBTUtil.TAG_ANY_NUMERIC;

public final class ColourUtils
{
    public static EnumDyeColor getStackColour( ItemStack stack )
    {
        Item item = stack.getItem();
        if( item instanceof ItemDye ) return ((ItemDye) item).getDyeColor();

        // TODO: Ore dictionary! (well, tags)
        return null;
    }

    public static int getHexColour( @Nonnull NBTTagCompound tag )
    {
        if( tag.contains( "colourIndex", TAG_ANY_NUMERIC ) )
        {
            return Colour.VALUES[tag.getInt( "colourIndex" ) & 0xF].getHex();
        }
        else if( tag.contains( "colour", TAG_ANY_NUMERIC ) )
        {
            return tag.getInt( "colour" );
        }
        else if( tag.contains( "color", TAG_ANY_NUMERIC ) )
        {
            return tag.getInt( "color" );
        }
        else
        {
            return -1;
        }
    }

    public static Colour getColour( @Nonnull NBTTagCompound tag )
    {
        if( tag.contains( "colourIndex", TAG_ANY_NUMERIC ) )
        {
            return Colour.fromInt( tag.getInt( "colourIndex" ) & 0xF );
        }
        else
        {
            return null;
        }
    }
}
