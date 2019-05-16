/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.peripheral.modem.wired;

import dan200.computercraft.shared.peripheral.PeripheralType;
import dan200.computercraft.shared.peripheral.common.ItemPeripheralBase;
import net.minecraft.block.Block;

public class ItemWiredModemFull extends ItemPeripheralBase
{
    public ItemWiredModemFull( Block block )
    {
        super( block );
    }

    @Override
    public PeripheralType getPeripheralType( int damage )
    {
        return PeripheralType.WiredModemFull;
    }
}
