/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.proxy;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.core.computer.MainThread;
import dan200.computercraft.core.tracking.Tracking;
import dan200.computercraft.shared.Registry;
import dan200.computercraft.shared.command.CommandComputerCraft;
import dan200.computercraft.shared.command.arguments.ArgumentSerializers;
import dan200.computercraft.shared.common.ContainerHeldItem;
import dan200.computercraft.shared.common.DefaultBundledRedstoneProvider;
import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.computer.inventory.ContainerComputer;
import dan200.computercraft.shared.computer.inventory.ContainerViewComputer;
import dan200.computercraft.shared.media.DefaultMediaProvider;
import dan200.computercraft.shared.network.container.*;
import dan200.computercraft.shared.peripheral.DefaultPeripheralProvider;
import dan200.computercraft.shared.peripheral.commandblock.CommandBlockPeripheralProvider;
import dan200.computercraft.shared.peripheral.diskdrive.ContainerDiskDrive;
import dan200.computercraft.shared.peripheral.diskdrive.TileDiskDrive;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessNetwork;
import dan200.computercraft.shared.peripheral.printer.ContainerPrinter;
import dan200.computercraft.shared.peripheral.printer.TilePrinter;
import dan200.computercraft.shared.pocket.inventory.ContainerPocketComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.inventory.ContainerTurtle;
import net.fabricmc.fabric.commands.CommandRegistry;
import net.fabricmc.fabric.events.ServerEvent;
import net.fabricmc.fabric.events.TickEvent;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.ModifiableRegistry;

public class ComputerCraftProxyCommon
{
    public void setup()
    {
        Registry.registerBlocks( net.minecraft.util.registry.Registry.BLOCK );
        Registry.registerTileEntities( (ModifiableRegistry<BlockEntityType<?>>) net.minecraft.util.registry.Registry.BLOCK_ENTITY );
        Registry.registerItems( net.minecraft.util.registry.Registry.ITEM );
        Registry.registerRecipes( (ModifiableRegistry<RecipeSerializer<?>>) net.minecraft.util.registry.Registry.RECIPE_SERIALIZER );

        registerProviders();
        registerContainers();
        registerHandlers();

        ArgumentSerializers.register();

        // if( Loader.isModLoaded( ModCharset.MODID ) ) IntegrationCharset.register();
    }

    private void registerProviders()
    {
        // Register peripheral providers
        ComputerCraftAPI.registerPeripheralProvider( new DefaultPeripheralProvider() );
        if( ComputerCraft.enableCommandBlock )
        {
            ComputerCraftAPI.registerPeripheralProvider( new CommandBlockPeripheralProvider() );
        }

        // Register bundled power providers
        ComputerCraftAPI.registerBundledRedstoneProvider( new DefaultBundledRedstoneProvider() );

        // Register media providers
        ComputerCraftAPI.registerMediaProvider( new DefaultMediaProvider() );

        // Register network providers
        // CapabilityWiredElement.register();
    }

    private void registerContainers()
    {
        ContainerType.register( BlockEntityContainerType::computer, ( id, packet, player ) ->
            new ContainerComputer( id, (TileComputer) packet.getBlockEntity( player ) ) );
        ContainerType.register( BlockEntityContainerType::turtle, ( id, packet, player ) -> {
            TileTurtle turtle = (TileTurtle) packet.getBlockEntity( player );
            return new ContainerTurtle( id, player.inventory, turtle.getAccess(), turtle.getServerComputer() );
        } );
        ContainerType.register( BlockEntityContainerType::diskDrive, ( id, packet, player ) ->
            new ContainerDiskDrive( id, player.inventory, (TileDiskDrive) packet.getBlockEntity( player ) ) );
        ContainerType.register( BlockEntityContainerType::printer, ( id, packet, player ) ->
            new ContainerPrinter( id, player.inventory, (TilePrinter) packet.getBlockEntity( player ) ) );

        ContainerType.register( PocketComputerContainerType::new, ( id, packet, player ) -> new ContainerPocketComputer( id, player, packet.hand ) );
        ContainerType.register( PrintoutContainerType::new, ( id, packet, player ) -> new ContainerHeldItem( id, player, packet.hand ) );
        ContainerType.register( ViewComputerContainerType::new, ( id, packet, player ) -> new ContainerViewComputer( id, ComputerCraft.serverComputerRegistry.get( packet.instanceId ) ) );
    }

    private void registerHandlers()
    {
        CommandRegistry.INSTANCE.register( false, CommandComputerCraft::register );

        TickEvent.SERVER.register( server -> {
            MainThread.executePendingTasks();
            ComputerCraft.serverComputerRegistry.update();
        } );

        ServerEvent.START.register( server -> {
            ComputerCraft.serverComputerRegistry.reset();
            WirelessNetwork.resetNetworks();
            Tracking.reset();
        } );

        ServerEvent.STOP.register( server -> {
            ComputerCraft.serverComputerRegistry.reset();
            WirelessNetwork.resetNetworks();
            Tracking.reset();
        } );
    }

    /*
    @Mod.EventBusSubscriber( modid = ComputerCraft.MOD_ID )
    public final static class ForgeHandlers
    {
        private ForgeHandlers()
        {
        }

        /*
        @SubscribeEvent
        public static void onConnectionOpened( FMLNetworkEvent.ClientConnectedToServerEvent event )
        {
            ComputerCraft.clientComputerRegistry.reset();
        }

        @SubscribeEvent
        public static void onConnectionClosed( FMLNetworkEvent.ClientDisconnectionFromServerEvent event )
        {
            ComputerCraft.clientComputerRegistry.reset();
        }
        *//*

        @SubscribeEvent
        public static void onConfigChanged( ConfigChangedEvent.OnConfigChangedEvent event )
        {
            if( event.getModID().equals( ComputerCraft.MOD_ID ) ) Config.sync();
        }

        @SubscribeEvent
        public static void onContainerOpen( PlayerContainerEvent.Open event )
        {
            // If we're opening a computer container then broadcast the terminal state
            Container container = event.getContainer();
            if( container instanceof IContainerComputer )
            {
                IComputer computer = ((IContainerComputer) container).getComputer();
                if( computer instanceof ServerComputer )
                {
                    ((ServerComputer) computer).sendTerminalState( event.getEntityPlayer() );
                }
            }
        }
    }
    */
}
