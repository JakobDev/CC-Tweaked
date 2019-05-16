/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.network.client;

import dan200.computercraft.ComputerCraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ComputerDeletedClientMessage extends ComputerClientMessage
{
    public ComputerDeletedClientMessage( int instanceId )
    {
        super( instanceId );
    }

    public ComputerDeletedClientMessage()
    {
    }

    @Override
    public void handle( MessageContext context )
    {
        ComputerCraft.clientComputerRegistry.remove( getInstanceId() );
    }
}
