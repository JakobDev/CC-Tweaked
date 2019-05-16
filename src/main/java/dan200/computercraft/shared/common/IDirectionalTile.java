/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.common;

import net.minecraft.util.EnumFacing;

public interface IDirectionalTile
{
    EnumFacing getDirection();

    void setDirection( EnumFacing dir );
}
