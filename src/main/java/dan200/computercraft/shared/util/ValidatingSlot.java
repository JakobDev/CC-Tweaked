/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ValidatingSlot extends Slot
{
    public ValidatingSlot( IInventory inventoryIn, int index, int xPosition, int yPosition )
    {
        super( inventoryIn, index, xPosition, yPosition );
    }

    @Override
    public boolean isItemValid( ItemStack stack )
    {
        return true; // inventory.isItemValidForSlot( slotNumber, stack );
    }
}
