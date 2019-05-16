/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.pocket.inventory;

import dan200.computercraft.shared.computer.core.IComputer;
import dan200.computercraft.shared.computer.core.IContainerComputer;
import dan200.computercraft.shared.computer.core.InputState;
import dan200.computercraft.shared.media.inventory.ContainerHeldItem;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerPocketComputer extends ContainerHeldItem implements IContainerComputer
{
    private final InputState input = new InputState( this );

    public ContainerPocketComputer( EntityPlayer player, EnumHand hand )
    {
        super( player, hand );
    }

    @Nullable
    @Override
    public IComputer getComputer()
    {
        ItemStack stack = getStack();
        return !stack.isEmpty() && stack.getItem() instanceof ItemPocketComputer
            ? ItemPocketComputer.getServerComputer( stack ) : null;
    }

    @Nonnull
    @Override
    public InputState getInput()
    {
        return input;
    }

    @Override
    public void onContainerClosed( EntityPlayer player )
    {
        super.onContainerClosed( player );
        input.close();
    }
}
