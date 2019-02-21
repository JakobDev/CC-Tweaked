/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.turtle.recipes;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.TurtleUpgrades;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.turtle.items.ITurtleItem;
import dan200.computercraft.shared.turtle.items.TurtleItemFactory;
import dan200.computercraft.shared.util.AbstractRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TurtleUpgradeRecipe extends AbstractRecipe
{
    private TurtleUpgradeRecipe( ResourceLocation id )
    {
        super( id );
    }

    @Override
    public boolean canFit( int x, int y )
    {
        return x >= 3 && y >= 1;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput()
    {
        return TurtleItemFactory.create( -1, null, -1, ComputerFamily.Normal, null, null, 0, null );
    }

    @Override
    public boolean matches( @Nonnull IInventory inventory, @Nonnull World world )
    {
        return !getCraftingResult( inventory ).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult( @Nonnull IInventory inventory )
    {
        // Scan the grid for a row containing a turtle and 1 or 2 items
        ItemStack leftItem = ItemStack.EMPTY;
        ItemStack turtle = ItemStack.EMPTY;
        ItemStack rightItem = ItemStack.EMPTY;

        for( int y = 0; y < inventory.getHeight(); y++ )
        {
            if( turtle.isEmpty() )
            {
                // Search this row for potential turtles
                boolean finishedRow = false;
                for( int x = 0; x < inventory.getWidth(); x++ )
                {
                    ItemStack item = inventory.getStackInSlot( x + y * inventory.getWidth() );
                    if( !item.isEmpty() )
                    {
                        if( finishedRow )
                        {
                            return ItemStack.EMPTY;
                        }

                        if( item.getItem() instanceof ITurtleItem )
                        {
                            // Item is a turtle
                            if( turtle.isEmpty() )
                            {
                                turtle = item;
                            }
                            else
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                        else
                        {
                            // Item is not a turtle
                            if( turtle.isEmpty() && leftItem.isEmpty() )
                            {
                                leftItem = item;
                            }
                            else if( !turtle.isEmpty() && rightItem.isEmpty() )
                            {
                                rightItem = item;
                            }
                            else
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    else
                    {
                        // Item is empty
                        if( !leftItem.isEmpty() || !turtle.isEmpty() )
                        {
                            finishedRow = true;
                        }
                    }
                }

                // If we found anything, check we found a turtle too
                if( turtle.isEmpty() && (!leftItem.isEmpty() || !rightItem.isEmpty()) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                // Turtle is already found, just check this row is empty
                for( int x = 0; x < inventory.getWidth(); x++ )
                {
                    ItemStack item = inventory.getStackInSlot( x + y * inventory.getWidth() );
                    if( !item.isEmpty() )
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        // See if we found a turtle + one or more items
        if( turtle.isEmpty() || (leftItem.isEmpty() && rightItem.isEmpty()) )
        {
            return ItemStack.EMPTY;
        }

        // At this point we have a turtle + 1 or 2 items
        // Get the turtle we already have
        ITurtleItem itemTurtle = (ITurtleItem) turtle.getItem();
        ComputerFamily family = itemTurtle.getFamily();
        ITurtleUpgrade[] upgrades = {
            itemTurtle.getUpgrade( turtle, TurtleSide.Left ),
            itemTurtle.getUpgrade( turtle, TurtleSide.Right ),
        };

        // Get the upgrades for the new items
        ItemStack[] items = new ItemStack[] { rightItem, leftItem };
        for( int i = 0; i < 2; i++ )
        {
            if( !items[i].isEmpty() )
            {
                ITurtleUpgrade itemUpgrade = TurtleUpgrades.get( items[i] );
                if( itemUpgrade == null )
                {
                    return ItemStack.EMPTY;
                }
                if( upgrades[i] != null )
                {
                    return ItemStack.EMPTY;
                }
                if( !TurtleUpgrades.suitableForFamily( family, itemUpgrade ) )
                {
                    return ItemStack.EMPTY;
                }
                upgrades[i] = itemUpgrade;
            }
        }

        // Construct the new stack
        int computerID = itemTurtle.getComputerID( turtle );
        String label = itemTurtle.getLabel( turtle );
        int fuelLevel = itemTurtle.getFuelLevel( turtle );
        int colour = itemTurtle.getColour( turtle );
        ResourceLocation overlay = itemTurtle.getOverlay( turtle );
        return TurtleItemFactory.create( computerID, label, colour, family, upgrades[0], upgrades[1], fuelLevel, overlay );
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    public static final IRecipeSerializer<TurtleUpgradeRecipe> SERIALIZER = new RecipeSerializers.SimpleSerializer<>(
        ComputerCraft.MOD_ID + ":turtle_upgrade", TurtleUpgradeRecipe::new
    );
}
