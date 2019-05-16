/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.blocks;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum ComputerState implements IStringSerializable
{
    Off( "off" ),
    On( "on" ),
    Blinking( "blinking" );

    private static final ComputerState[] VALUES = ComputerState.values();

    // TODO: Move to dan200.computercraft.shared.computer.core in the future. We can't do it now
    //  as Plethora depends on it.

    private final String name;

    ComputerState( String name )
    {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static ComputerState valueOf( int ordinal )
    {
        return ordinal < 0 || ordinal >= VALUES.length ? ComputerState.Off : VALUES[ordinal];
    }
}

