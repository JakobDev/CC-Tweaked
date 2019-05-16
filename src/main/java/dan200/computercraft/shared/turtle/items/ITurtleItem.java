/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.items;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.items.IComputerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITurtleItem extends IComputerItem, IColouredItem
{
    @Nullable
    ITurtleUpgrade getUpgrade( @Nonnull ItemStack stack, @Nonnull TurtleSide side );

    int getFuelLevel( @Nonnull ItemStack stack );

    @Nullable
    ResourceLocation getOverlay( @Nonnull ItemStack stack );
}
