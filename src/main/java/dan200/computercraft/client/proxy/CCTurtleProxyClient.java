/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.client.proxy;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.render.TileEntityTurtleRenderer;
import dan200.computercraft.client.render.TurtleSmartItemModel;
import dan200.computercraft.shared.proxy.CCTurtleProxyCommon;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.items.ItemTurtle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

public class CCTurtleProxyClient extends CCTurtleProxyCommon
{
    @Override
    @SubscribeEvent
    public void setup( FMLCommonSetupEvent event )
    {
        super.setup( event );
        MinecraftForge.EVENT_BUS.register( new ForgeHandlers() );
    }

    @SubscribeEvent
    public void setupClient( InterModProcessEvent event ) // TODO: Move this somewhere more sane if Forge fixes things.
    {
        // Setup turtle colours
        Minecraft.getInstance().getItemColors().register( ( stack, tintIndex ) -> {
            if( tintIndex == 0 )
            {
                ItemTurtle turtle = (ItemTurtle) stack.getItem();
                int colour = turtle.getColour( stack );
                if( colour != -1 ) return colour;
            }

            return 0xFFFFFF;
        }, ComputerCraft.Blocks.turtleNormal, ComputerCraft.Blocks.turtleAdvanced );

        // Setup renderers
        ClientRegistry.bindTileEntitySpecialRenderer( TileTurtle.class, new TileEntityTurtleRenderer() );
    }

    public static class ForgeHandlers
    {
        private final TurtleSmartItemModel m_turtleSmartItemModel = new TurtleSmartItemModel();

        ForgeHandlers()
        {
            IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            if( resourceManager instanceof IReloadableResourceManager )
            {
                ((IReloadableResourceManager) resourceManager).addReloadListener( m_turtleSmartItemModel );
            }
        }

        @SubscribeEvent
        public void onModelBakeEvent( ModelBakeEvent event )
        {
            event.getModelRegistry().put( new ModelResourceLocation( "computercraft:turtle_dynamic", "inventory" ), m_turtleSmartItemModel );
        }
    }

}
