/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.network.container;

import dan200.computercraft.shared.network.NetworkHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A horrible hack to allow opening GUIs until Forge adds a built-in system.
 */
public interface ContainerType<T extends Container>
{
    @Nonnull
    ResourceLocation getId();

    void toBytes( PacketBuffer buf );

    void fromBytes( PacketBuffer buf );

    default void open( EntityPlayer player )
    {
        EntityPlayerMP serverPlayer = (EntityPlayerMP) player;

        @SuppressWarnings( "unchecked" )
        Container container = ((BiFunction<ContainerType<T>, EntityPlayer, ? extends Container>) containerFactories.get( getId() )).apply( this, serverPlayer );
        if( container == null ) return;

        if( serverPlayer.openContainer != serverPlayer.inventoryContainer ) serverPlayer.closeScreen();

        serverPlayer.getNextWindowId();
        container.windowId = serverPlayer.currentWindowId;
        serverPlayer.openContainer = container;
        container.addListener( serverPlayer );

        MinecraftForge.EVENT_BUS.post( new PlayerContainerEvent.Open( serverPlayer, container ) );

        NetworkHandler.sendToPlayer( serverPlayer, new OpenGuiClientMessage( serverPlayer.currentWindowId, this ) );
    }

    static <C extends Container, T extends ContainerType<C>> void register( Supplier<T> containerType, BiFunction<T, EntityPlayer, C> factory )
    {
        factories.put( containerType.get().getId(), containerType );
        containerFactories.put( containerType.get().getId(), factory );
    }

    static <C extends Container, T extends ContainerType<C>> void registerGui( Supplier<T> containerType, BiFunction<T, EntityPlayer, GuiContainer> factory )
    {
        guiFactories.put( containerType.get().getId(), factory );
    }

    static <C extends Container, T extends ContainerType<C>> void registerGui( Supplier<T> containerType, Function<C, GuiContainer> factory )
    {
        registerGui( containerType, ( type, player ) -> {
            @SuppressWarnings( "unchecked" )
            C container = ((BiFunction<T, EntityPlayer, C>) containerFactories.get( type.getId() )).apply( type, player );
            return container == null ? null : factory.apply( container );
        } );
    }

    Map<ResourceLocation, Supplier<? extends ContainerType<?>>> factories = new HashMap<>();
    Map<ResourceLocation, BiFunction<? extends ContainerType<?>, EntityPlayer, GuiContainer>> guiFactories = new HashMap<>();
    Map<ResourceLocation, BiFunction<? extends ContainerType<?>, EntityPlayer, ? extends Container>> containerFactories = new HashMap<>();
}
