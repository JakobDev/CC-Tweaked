/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.network.client;

import dan200.computercraft.shared.util.NBTUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class ComputerTerminalClientMessage extends ComputerClientMessage
{
    private NBTTagCompound tag;

    public ComputerTerminalClientMessage( int instanceId, NBTTagCompound tag )
    {
        super( instanceId );
        this.tag = tag;
    }

    public ComputerTerminalClientMessage()
    {
    }

    @Override
    public void toBytes( @Nonnull PacketBuffer buf )
    {
        super.toBytes( buf );
        buf.writeCompoundTag( tag ); // TODO: Do we need to compress this?
    }

    @Override
    public void fromBytes( @Nonnull PacketBuffer buf )
    {
        super.fromBytes( buf );
        tag = NBTUtil.readCompoundTag( buf );
    }

    @Override
    public void handle( MessageContext context )
    {
        getComputer().readDescription( tag );
    }
}
