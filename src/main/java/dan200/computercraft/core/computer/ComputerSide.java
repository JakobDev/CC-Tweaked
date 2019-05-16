/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.core.computer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A side on a computer. Unlike {@link net.minecraft.util.EnumFacing}, this is relative to the direction the computer is
 * facing..
 */
public enum ComputerSide
{
    BOTTOM( "bottom" ),
    TOP( "top" ),
    BACK( "back" ),
    FRONT( "front" ),
    RIGHT( "right" ),
    LEFT( "left" );

    public static final String[] NAMES = new String[] { "bottom", "top", "back", "front", "right", "left" };

    public static final int COUNT = 6;

    private static final ComputerSide[] VALUES = values();

    private final String name;

    ComputerSide( String name ) {this.name = name;}

    @Nonnull
    public static ComputerSide valueOf( int side )
    {
        return VALUES[side];
    }

    @Nullable
    public static ComputerSide valueOfInsensitive( @Nonnull String name )
    {
        for( ComputerSide side : VALUES )
        {
            if( side.name.equalsIgnoreCase( name ) ) return side;
        }

        return null;
    }

    public String getName()
    {
        return name;
    }
}
