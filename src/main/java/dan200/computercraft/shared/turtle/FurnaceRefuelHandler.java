/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.event.TurtleRefuelEvent;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber( modid = ComputerCraft.MOD_ID )
public final class FurnaceRefuelHandler implements TurtleRefuelEvent.Handler
{
    private static final FurnaceRefuelHandler INSTANCE = new FurnaceRefuelHandler();

    private FurnaceRefuelHandler()
    {
    }

    @Override
    public int refuel( @Nonnull ITurtleAccess turtle, @Nonnull ItemStack currentStack, int slot, int limit )
    {
        ItemStack stack = turtle.getItemHandler().extractItem( slot, limit, false );
        int fuelToGive = getFuelPerItem( stack ) * stack.getCount();

        // Store the replacement item in the inventory
        ItemStack replacementStack = stack.getItem().getContainerItem( stack );
        if( !replacementStack.isEmpty() )
        {
            ItemStack remainder = InventoryUtil.storeItems( replacementStack, turtle.getItemHandler(), turtle.getSelectedSlot() );
            if( !remainder.isEmpty() )
            {
                WorldUtil.dropItemStack( remainder, turtle.getWorld(), turtle.getPosition(), turtle.getDirection().getOpposite() );
            }
        }

        return fuelToGive;
    }


    private static int getFuelPerItem( @Nonnull ItemStack stack )
    {
        return TileEntityFurnace.getItemBurnTime( stack ) * 5 / 100;
    }

    @SubscribeEvent
    public static void onTurtleRefuel( TurtleRefuelEvent event )
    {
        if( event.getHandler() == null && getFuelPerItem( event.getStack() ) > 0 ) event.setHandler( INSTANCE );
    }
}
