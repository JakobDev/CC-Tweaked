/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.util.EnumFacing;

public enum MoveDirection
{
    Forward,
    Back,
    Up,
    Down;

    public EnumFacing toWorldDir( ITurtleAccess turtle )
    {
        switch( this )
        {
            case Forward:
            default:
                return turtle.getDirection();
            case Back:
                return turtle.getDirection().getOpposite();
            case Up:
                return EnumFacing.UP;
            case Down:
                return EnumFacing.DOWN;
        }
    }
}
