/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */
package dan200.computercraft.shared.pocket.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.modem.ModemState;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemPeripheral;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class PocketModemPeripheral extends WirelessModemPeripheral
{
    private World world = null;
    private Vec3d position = Vec3d.ZERO;

    public PocketModemPeripheral( boolean advanced )
    {
        super( new ModemState(), advanced );
    }

    void setLocation( World world, Vec3d position )
    {
        this.position = position;
        this.world = world;
    }

    @Nonnull
    @Override
    public World getWorld()
    {
        return world;
    }

    @Nonnull
    @Override
    public Vec3d getPosition()
    {
        return position;
    }

    @Override
    public boolean equals( IPeripheral other )
    {
        return other instanceof PocketModemPeripheral;
    }
}
