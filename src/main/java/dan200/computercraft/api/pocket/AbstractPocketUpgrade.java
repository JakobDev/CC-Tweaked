/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.api.pocket;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * A base class for {@link IPocketUpgrade}s.
 *
 * One does not have to use this, but it does provide a convenient template.
 */
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

    protected AbstractPocketUpgrade( ResourceLocation id, ItemStack stack )
    {
        this( id, "upgrade." + id + ".adjective", stack );
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
