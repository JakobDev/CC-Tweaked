/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.peripheral.common;

import dan200.computercraft.shared.peripheral.PeripheralType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IPeripheralItem
{
    PeripheralType getPeripheralType( @Nonnull ItemStack stack );
}
