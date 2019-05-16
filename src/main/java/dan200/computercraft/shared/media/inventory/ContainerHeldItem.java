/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.media.inventory;

import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

public class ContainerHeldItem extends Container
{
    private final ItemStack m_stack;
    private final EnumHand m_hand;

    public ContainerHeldItem( EntityPlayer player, EnumHand hand )
    {
        m_hand = hand;
        m_stack = InventoryUtil.copyItem( player.getHeldItem( hand ) );
    }

    @Nonnull
    public ItemStack getStack()
    {
        return m_stack;
    }

    @Override
    public boolean canInteractWith( @Nonnull EntityPlayer player )
    {
        if( !player.isEntityAlive() ) return false;

        ItemStack stack = player.getHeldItem( m_hand );
        return stack == m_stack || !stack.isEmpty() && !m_stack.isEmpty() && stack.getItem() == m_stack.getItem();
    }
}
