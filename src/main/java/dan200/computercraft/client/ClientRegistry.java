/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.client;

/**
 * Registers textures and models for items.
 */
public class ClientRegistry
{
    private static final String[] TURTLE_UPGRADES = {
        "turtle_modem_off_left",
        "turtle_modem_on_left",
        "turtle_modem_off_right",
        "turtle_modem_on_right",
        "turtle_crafting_table_left",
        "turtle_crafting_table_right",
        "advanced_turtle_modem_off_left",
        "advanced_turtle_modem_on_left",
        "advanced_turtle_modem_off_right",
        "advanced_turtle_modem_on_right",
        "turtle_speaker_upgrade_left",
        "turtle_speaker_upgrade_right",
    };

    public static void registerModels()
    {
        String[] extraTurtleModels = new String[] { "turtle", "turtle_advanced", "turtle_white", "turtle_elf_overlay" };
        // registerUniversalItemModel( ComputerCraft.Items.turtleNormal, "turtle_dynamic", extraTurtleModels );
        // registerUniversalItemModel( ComputerCraft.Items.turtleAdvanced, "turtle_dynamic", extraTurtleModels );
    }

    /*
    @SubscribeEvent
    public static void onTextureStitchEvent( TextureStitchEvent.Pre event )
    {
        // Load all textures for upgrades
        TextureMap map = event.getMap();
        for( String upgrade : TURTLE_UPGRADES )
        {
            IUnbakedModel model = ModelLoaderRegistry.getModelOrMissing( new ResourceLocation( "computercraft", "block/" + upgrade ) );
            /*
            for( ResourceLocation texture : model.getTextures() )
            {
                map.registerSprite( texture );
            }
            */  /*
        }
    }

    public static void onModelBakeEvent( ModelBakeEvent event )
    {
        // Load all upgrade models
        for( String upgrade : TURTLE_UPGRADES )
        {
            loadBlockModel( event, upgrade );
        }
    }

    private static void registerUniversalItemModel( Item item, String mainModel, String... extraModels )
    {

        Identifier mainLocation = new Identifier( ComputerCraft.MOD_ID, mainModel );

        Identifier[] modelLocations = new Identifier[extraModels.length + 1];
        modelLocations[0] = mainLocation;
        for( int i = 0; i < extraModels.length; i++ )
        {
            modelLocations[i + 1] = new Identifier( ComputerCraft.MOD_ID, extraModels[i] );
        }

        /*
        ModelBakery.registerItemVariants( item, modelLocations );

        final ModelResourceLocation mainModelLocation = new ModelResourceLocation( mainLocation, "inventory" );
        ModelLoader.setCustomMeshDefinition( item, new ItemMeshDefinition()
        {
            @Nonnull
            @Override
            public ModelResourceLocation getModelLocation( @Nonnull ItemStack stack )
            {
                return mainModelLocation;
            }
        } );
        */  /*
    }

    private static void loadBlockModel( ModelBakeEvent event, String name )
    {
        /*
        IModel model = ModelLoaderRegistry.getModelOrMissing( new ResourceLocation( ComputerCraft.MOD_ID, "block/" + name ) );
        IBakedModel bakedModel = model.bake(
            model.getDefaultState(), DefaultVertexFormats.ITEM,
            location -> Minecraft.getInstance().getTextureMap().getAtlasSprite( location.toString() )
        );

        event.getModelRegistry().put( new ModelResourceLocation( ComputerCraft.MOD_ID + ":" + name, "inventory" ), bakedModel );
        */ /*
    }
    */
}
