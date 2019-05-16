/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.peripheral.modem;

/**
 * This only exists for backwards compatibility
 */
public abstract class WirelessModemPeripheral extends dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemPeripheral
{
    @Deprecated
    public WirelessModemPeripheral( boolean advanced )
    {
        super( new ModemState(), advanced );
    }
}
