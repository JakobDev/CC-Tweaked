/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.pocket.peripherals;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import javax.annotation.Nonnull;

public abstract class AbstractPocketUpgrade implements IPocketUpgrade
{
    private final ResourceLocation id;
    private final String adjective;
    private final ItemStack stack;

    protected AbstractPocketUpgrade( ResourceLocation id, String adjective, ItemStack stack )
    {
        this.id = id;
        this.adjective = adjective;
        this.stack = stack;
    }

    protected AbstractPocketUpgrade( ResourceLocation id, String adjective, IItemProvider item )
    {
        this( id, adjective, new ItemStack( item ) );
    }

    protected AbstractPocketUpgrade( ResourceLocation id, IItemProvider item )
    {
        this( id, Util.makeTranslationKey( "upgrade", id ) + ".adjective", new ItemStack( item ) );
    }

    @Nonnull
    @Override
    public final ResourceLocation getUpgradeID()
    {
        return id;
    }

    @Nonnull
    @Override
    public final String getUnlocalisedAdjective()
    {
        return adjective;
    }

    @Nonnull
    @Override
    public final ItemStack getCraftingItem()
    {
        return stack;
    }
}
