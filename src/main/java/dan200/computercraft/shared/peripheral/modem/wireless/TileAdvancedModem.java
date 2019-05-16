/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.peripheral.modem.wireless;

import net.minecraft.util.EnumFacing;

public class TileAdvancedModem extends TileWirelessModemBase
{
    public TileAdvancedModem()
    {
        super( true );
    }

    @Override
    protected EnumFacing getDirection()
    {
        return getBlockState().getValue( BlockAdvancedModem.FACING );
    }
}
