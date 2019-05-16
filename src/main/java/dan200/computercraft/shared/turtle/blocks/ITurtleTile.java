/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.blocks;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.common.IDirectionalTile;
import dan200.computercraft.shared.computer.blocks.IComputerTile;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public interface ITurtleTile extends IComputerTile, IDirectionalTile
{
    int getColour();

    ResourceLocation getOverlay();

    ITurtleUpgrade getUpgrade( TurtleSide side );

    ITurtleAccess getAccess();

    Vec3d getRenderOffset( float f );

    float getRenderYaw( float f );

    float getToolRenderAngle( TurtleSide side, float f );
}
