/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.computer.inventory;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerViewComputer extends Container implements IContainerComputer
{
    private final IComputer computer;
    private final InputState input = new InputState( this );

    public ContainerViewComputer( IComputer computer )
    {
        this.computer = computer;
    }

    @Nullable
    @Override
    public IComputer getComputer()
    {
        return computer;
    }

    @Override
    public boolean canInteractWith( @Nonnull EntityPlayer player )
    {
        if( computer instanceof ServerComputer )
        {
            ServerComputer serverComputer = (ServerComputer) computer;

            // If this computer no longer exists then discard it.
            if( ComputerCraft.serverComputerRegistry.get( serverComputer.getInstanceID() ) != serverComputer )
            {
                return false;
            }

            // If we're a command computer then ensure we're in creative
            if( serverComputer.getFamily() == ComputerFamily.Command )
            {
                MinecraftServer server = player.getServer();
                if( server == null || !server.isCommandBlockEnabled() )
                {
                    player.sendMessage( new TextComponentTranslation( "advMode.notEnabled" ) );
                    return false;
                }
                else if( !player.canUseCommandBlock() )
                {
                    player.sendMessage( new TextComponentTranslation( "advMode.notAllowed" ) );
                    return false;
                }
            }
        }

        return true;
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
