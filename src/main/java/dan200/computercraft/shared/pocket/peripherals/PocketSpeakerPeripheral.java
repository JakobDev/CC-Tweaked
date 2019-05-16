/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.pocket.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPeripheral;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PocketSpeakerPeripheral extends SpeakerPeripheral
{
    private World world = null;
    private Vec3d position = Vec3d.ZERO;

    void setLocation( World world, Vec3d position )
    {
        this.position = position;
        this.world = world;
    }

    @Override
    public World getWorld()
    {
        return world;
    }

    @Override
    public Vec3d getPosition()
    {
        return world != null ? position : null;
    }

    @Override
    public boolean equals( IPeripheral other )
    {
        return other instanceof PocketSpeakerPeripheral;
    }
}
