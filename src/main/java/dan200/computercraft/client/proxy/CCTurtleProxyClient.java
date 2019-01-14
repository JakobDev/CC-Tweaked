/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.client.proxy;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.render.TileEntityTurtleRenderer;
import dan200.computercraft.shared.proxy.CCTurtleProxyCommon;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import dan200.computercraft.shared.turtle.items.ItemTurtle;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.fabricmc.fabric.client.render.BlockEntityRendererRegistry;

public class CCTurtleProxyClient extends CCTurtleProxyCommon
{
    @Override
    public void setup()
    {
        super.setup();

        // Setup turtle colours
        ColorProviderRegistry.ITEM.register( ( stack, tintIndex ) -> {
            if( tintIndex == 0 )
            {
                ItemTurtle turtle = (ItemTurtle) stack.getItem();
                int colour = turtle.getColour( stack );
                if( colour != -1 ) return colour;
            }

            return 0xFFFFFF;
        }, ComputerCraft.Blocks.turtleNormal, ComputerCraft.Blocks.turtleAdvanced );

        // Setup renderers
        BlockEntityRendererRegistry.INSTANCE.register( TileTurtle.class, new TileEntityTurtleRenderer() );
    }

    /*
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
    */
}
