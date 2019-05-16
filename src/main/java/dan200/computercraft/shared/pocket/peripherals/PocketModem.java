/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.pocket.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.shared.peripheral.PeripheralType;
import dan200.computercraft.shared.peripheral.common.PeripheralItemFactory;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PocketModem extends AbstractPocketUpgrade
{
    private final boolean advanced;

    public PocketModem( boolean advanced )
    {
        super(
            advanced
                ? new ResourceLocation( "computercraft", "advanced_modem" )
                : new ResourceLocation( "computercraft", "wireless_modem" ),
            PeripheralItemFactory.create(
                advanced ? PeripheralType.AdvancedModem : PeripheralType.WirelessModem,
                null, 1
            )
        );
        this.advanced = advanced;
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral( @Nonnull IPocketAccess access )
    {
        return new PocketModemPeripheral( advanced );
    }

    @Override
    public void update( @Nonnull IPocketAccess access, @Nullable IPeripheral peripheral )
    {
        if( !(peripheral instanceof PocketModemPeripheral) ) return;

        Entity entity = access.getValidEntity();

        PocketModemPeripheral modem = (PocketModemPeripheral) peripheral;

        if( entity != null ) modem.setLocation( entity.getEntityWorld(), entity.getPositionEyes( 1 ) );

        ModemState state = modem.getModemState();
        if( state.pollChanged() ) access.setLight( state.isOpen() ? 0xBA0000 : -1 );
    }
}
