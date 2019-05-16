/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.network.server;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.NetworkMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class RequestComputerMessage implements NetworkMessage
{
    private int instance;

    public RequestComputerMessage( int instance )
    {
        this.instance = instance;
    }

    public RequestComputerMessage()
    {
    }

    @Override
    public void toBytes( @Nonnull PacketBuffer buf )
    {
        buf.writeVarInt( instance );
    }

    @Override
    public void fromBytes( @Nonnull PacketBuffer buf )
    {
        instance = buf.readVarInt();
    }

    @Override
    public void handle( MessageContext context )
    {
        ServerComputer computer = ComputerCraft.serverComputerRegistry.get( instance );
        if( computer != null ) computer.sendComputerState( context.getServerHandler().player );
    }
}
