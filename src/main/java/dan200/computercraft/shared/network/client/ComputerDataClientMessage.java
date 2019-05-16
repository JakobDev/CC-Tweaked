/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.network.client;

import dan200.computercraft.shared.computer.blocks.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.util.NBTUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

/**
 * Provides additional data about a client computer, such as its ID and current state.
 */
public class ComputerDataClientMessage extends ComputerClientMessage
{
    private ComputerState state;
    private NBTTagCompound userData;

    public ComputerDataClientMessage( ServerComputer computer )
    {
        super( computer.getInstanceID() );
        state = computer.getState();
        userData = computer.getUserData();
    }

    public ComputerDataClientMessage()
    {
    }

    @Override
    public void toBytes( @Nonnull PacketBuffer buf )
    {
        super.toBytes( buf );
        buf.writeEnumValue( state );
        buf.writeCompoundTag( userData );
    }

    @Override
    public void fromBytes( @Nonnull PacketBuffer buf )
    {
        super.fromBytes( buf );
        state = buf.readEnumValue( ComputerState.class );
        userData = NBTUtil.readCompoundTag( buf );
    }

    @Override
    public void handle( MessageContext context )
    {
        getComputer().setState( state, userData );
    }
}
