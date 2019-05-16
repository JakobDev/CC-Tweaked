/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.event.TurtleRefuelEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

public class TurtleRefuelCommand implements ITurtleCommand
{
    private final int limit;

    public TurtleRefuelCommand( int limit )
    {
        this.limit = limit;
    }

    @Nonnull
    @Override
    public TurtleCommandResult execute( @Nonnull ITurtleAccess turtle )
    {
        int slot = turtle.getSelectedSlot();
        ItemStack stack = turtle.getInventory().getStackInSlot( slot );
        if( stack.isEmpty() ) return TurtleCommandResult.failure( "No items to combust" );

        TurtleRefuelEvent event = new TurtleRefuelEvent( turtle, stack );
        if( MinecraftForge.EVENT_BUS.post( event ) ) return TurtleCommandResult.failure( event.getFailureMessage() );
        if( event.getHandler() == null ) return TurtleCommandResult.failure( "Items not combustible" );

        if( limit != 0 )
        {
            turtle.addFuel( event.getHandler().refuel( turtle, stack, slot, limit ) );
            turtle.playAnimation( TurtleAnimation.Wait );
        }

        return TurtleCommandResult.success();
    }
}
