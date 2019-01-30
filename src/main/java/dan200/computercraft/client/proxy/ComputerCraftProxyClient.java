/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.client.proxy;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.gui.*;
import dan200.computercraft.client.render.TileEntityCableRenderer;
import dan200.computercraft.client.render.TileEntityMonitorRenderer;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.computer.core.ClientComputer;
import dan200.computercraft.shared.computer.inventory.ContainerViewComputer;
import dan200.computercraft.shared.network.container.*;
import dan200.computercraft.shared.peripheral.modem.wired.TileCable;
import dan200.computercraft.shared.peripheral.monitor.ClientMonitor;
import dan200.computercraft.shared.peripheral.monitor.TileMonitor;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import dan200.computercraft.shared.proxy.ComputerCraftProxyCommon;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.inventory.ContainerTurtle;
import dan200.computercraft.shared.util.Colour;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

public class ComputerCraftProxyClient extends ComputerCraftProxyCommon
{
    @SubscribeEvent
    public void setupClient( InterModProcessEvent event ) // TODO: Move this somewhere more sane if Forge fixes things.
    {
        // Register any client-specific commands
        // ClientCommandHandler.instance.registerCommand( CommandCopy.INSTANCE );

        registerContainers();

        Minecraft mc = Minecraft.getInstance();

        // Setup
        mc.getItemColors().register(
            ( stack, layer ) -> layer == 0 ? 0xFFFFFF : ComputerCraft.Items.disk.getColour( stack ),
            ComputerCraft.Items.disk
        );

        mc.getItemColors().register( ( stack, layer ) -> {
            switch( layer )
            {
                case 0:
                default:
                    return 0xFFFFFF;
                case 1:
                {
                    // Frame colour
                    int colour = IColouredItem.getColourBasic( stack );
                    return colour == -1 ? 0xFFFFFF : colour;
                }
                case 2:
                {
                    // Light colour
                    int colour = ItemPocketComputer.getLightState( stack );
                    return colour == -1 ? Colour.Black.getHex() : colour;
                }
            }
        }, ComputerCraft.Items.pocketComputerNormal, ComputerCraft.Items.pocketComputerAdvanced );

        // Setup renderers
        ClientRegistry.bindTileEntitySpecialRenderer( TileMonitor.class, new TileEntityMonitorRenderer() );
        ClientRegistry.bindTileEntitySpecialRenderer( TileCable.class, new TileEntityCableRenderer() );
    }

    private void registerContainers()
    {
        ContainerType.registerGui( BlockEntityContainerType::computer, ( packet, player ) ->
            new GuiComputer( (TileComputer) packet.getTileEntity( player ) ) );
        ContainerType.registerGui( BlockEntityContainerType::diskDrive, GuiDiskDrive::new );
        ContainerType.registerGui( BlockEntityContainerType::printer, GuiPrinter::new );
        ContainerType.registerGui( BlockEntityContainerType::turtle, ( packet, player ) -> {
            TileTurtle turtle = (TileTurtle) packet.getTileEntity( player );
            return new GuiTurtle( turtle, new ContainerTurtle( player.inventory, turtle.getAccess(), turtle.getClientComputer() ) );
        } );

        ContainerType.registerGui( PocketComputerContainerType::new, GuiPocketComputer::new );
        ContainerType.registerGui( PrintoutContainerType::new, GuiPrintout::new );
        ContainerType.registerGui( ViewComputerContainerType::new, ( packet, player ) -> {
            ClientComputer computer = ComputerCraft.clientComputerRegistry.get( packet.instanceId );
            if( computer == null )
            {
                ComputerCraft.clientComputerRegistry.add( packet.instanceId, computer = new ClientComputer( packet.instanceId ) );
            }

            ContainerViewComputer container = new ContainerViewComputer( computer );
            return new GuiComputer( container, packet.family, computer, packet.width, packet.height );
        } );
    }


    @Mod.EventBusSubscriber( modid = ComputerCraft.MOD_ID, value = Dist.CLIENT )
    public static class ForgeHandlers
    {
        @SubscribeEvent
        public static void onWorldUnload( WorldEvent.Unload event )
        {
            if( event.getWorld().isRemote() )
            {
                ClientMonitor.destroyAll();
            }
        }
    }
}
