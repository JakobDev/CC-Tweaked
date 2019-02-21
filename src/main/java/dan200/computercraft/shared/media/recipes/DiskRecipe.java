/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.media.recipes;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.media.items.ItemDisk;
import dan200.computercraft.shared.util.AbstractRecipe;
import dan200.computercraft.shared.util.Colour;
import dan200.computercraft.shared.util.ColourTracker;
import dan200.computercraft.shared.util.ColourUtils;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;

public class DiskRecipe extends AbstractRecipe
{
    private final Ingredient paper = Ingredient.fromItems( Items.PAPER );
    private final Ingredient redstone = Ingredient.fromTag( Tags.Items.DUSTS_REDSTONE );

    public DiskRecipe( ResourceLocation id )
    {
        super( id );
    }

    @Override
    public boolean matches( @Nonnull IInventory inv, @Nonnull World world )
    {
        boolean paperFound = false;
        boolean redstoneFound = false;

        for( int i = 0; i < inv.getSizeInventory(); i++ )
        {
            ItemStack stack = inv.getStackInSlot( i );

            if( !stack.isEmpty() )
            {
                if( paper.test( stack ) )
                {
                    if( paperFound ) return false;
                    paperFound = true;
                }
                else if( redstone.test( stack ) )
                {
                    if( redstoneFound ) return false;
                    redstoneFound = true;
                }
                else if( ColourUtils.getStackColour( stack ) != null )
                {
                    return false;
                }
            }
        }

        return redstoneFound && paperFound;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult( @Nonnull IInventory inv )
    {
        ColourTracker tracker = new ColourTracker();

        for( int i = 0; i < inv.getSizeInventory(); i++ )
        {
            ItemStack stack = inv.getStackInSlot( i );

            if( stack.isEmpty() ) continue;

            if( !paper.test( stack ) && !redstone.test( stack ) )
            {
                EnumDyeColor dye = ColourUtils.getStackColour( stack );
                if( dye == null ) continue;

                Colour colour = Colour.VALUES[dye.getId()];
                tracker.addColour( colour.getR(), colour.getG(), colour.getB() );
            }
        }

        return ItemDisk.createFromIDAndColour( -1, null, tracker.hasColour() ? tracker.getColour() : Colour.Blue.getHex() );
    }

    @Override
    public boolean canFit( int x, int y )
    {
        return x >= 2 && y >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemDisk.createFromIDAndColour( -1, null, Colour.Blue.getHex() );
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    public static final IRecipeSerializer<DiskRecipe> SERIALIZER = new RecipeSerializers.SimpleSerializer<>(
        ComputerCraft.MOD_ID + ":disk", DiskRecipe::new
    );
}
