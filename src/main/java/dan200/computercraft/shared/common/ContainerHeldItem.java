/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.common;

import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public class ContainerHeldItem extends Container
{
    private final ItemStack stack;
    private final EnumHand hand;

    public ContainerHeldItem( EntityPlayer player, EnumHand hand )
    {
        this.hand = hand;
        stack = InventoryUtil.copyItem( player.getHeldItem( hand ) );
    }

    @Nonnull
    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public boolean canInteractWith( @Nonnull EntityPlayer player )
    {
        if( !player.isAlive() ) return false;

        ItemStack stack = player.getHeldItem( hand );
        return stack == this.stack || (!stack.isEmpty() && !this.stack.isEmpty() && stack.getItem() == this.stack.getItem());
    }
}
