/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.network.container;

import dan200.computercraft.shared.network.NetworkMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

import static dan200.computercraft.shared.network.container.ContainerType.guiFactories;

/**
 * Opens a GUI on the client
 *
 * This is just a temporary hack until Forge has their own version.
 */
public class OpenGuiClientMessage implements NetworkMessage
{
    private final int windowId;
    private final ContainerType<?> type;

    public OpenGuiClientMessage( int windowId, ContainerType<?> type )
    {
        this.windowId = windowId;
        this.type = type;
    }

    public OpenGuiClientMessage( PacketBuffer buf )
    {
        windowId = buf.readVarInt();

        ResourceLocation id = buf.readResourceLocation();
        type = ContainerType.factories.get( id ).get();
        type.fromBytes( buf );
    }

    @Override
    public void toBytes( @Nonnull PacketBuffer buf )
    {
        buf.writeVarInt( windowId );
        buf.writeResourceLocation( type.getId() );
        type.toBytes( buf );
    }

    @Override
    @OnlyIn( Dist.CLIENT )
    public void handle( NetworkEvent.Context context )
    {
        Minecraft mc = Minecraft.getInstance();

        @SuppressWarnings( "unchecked" )
        GuiScreen gui = ((BiFunction<ContainerType<?>, EntityPlayer, GuiContainer>) guiFactories.get( type.getId() )).apply( type, mc.player );
        mc.displayGuiScreen( gui );
        mc.player.openContainer.windowId = windowId;
    }
}
