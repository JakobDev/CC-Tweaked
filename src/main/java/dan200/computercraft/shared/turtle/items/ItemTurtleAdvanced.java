/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.items;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.block.Block;

public class ItemTurtleAdvanced extends ItemTurtleNormal
{
    public ItemTurtleAdvanced( Block block )
    {
        super( block );
        setTranslationKey( "computercraft:advanced_turtle" );
        setCreativeTab( ComputerCraft.mainCreativeTab );
    }

    // IComputerItem implementation

    @Override
    public ComputerFamily getFamily()
    {
        return ComputerFamily.Advanced;
    }
}
