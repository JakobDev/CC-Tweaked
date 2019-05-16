/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.peripheral.common;

import dan200.computercraft.shared.peripheral.PeripheralType;

/**
 * The tile for {@link BlockPeripheral}.
 */
public interface ITilePeripheral
{
    PeripheralType getPeripheralType();

    default String getLabel()
    {
        return null;
    }

    default void setLabel( String label )
    {
    }
}
