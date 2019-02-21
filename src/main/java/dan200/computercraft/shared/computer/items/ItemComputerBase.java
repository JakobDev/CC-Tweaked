/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.computer.items;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.computer.blocks.BlockComputerBase;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemComputerBase extends ItemBlock implements IComputerItem, IMedia
{
    private final ComputerFamily family;

    public ItemComputerBase( BlockComputerBase<?> block, Properties settings )
    {
        super( block, settings );
        this.family = block.getFamily();
    }

    @Override
    public void addInformation( @Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> list, @Nonnull ITooltipFlag options )
    {
        if( options.isAdvanced() )
        {
            int id = getComputerID( stack );
            if( id >= 0 )
            {
                list.add( new TextComponentTranslation( "gui.computercraft.tooltip.computer_id", id )
                    .applyTextStyle( TextFormatting.GRAY ) );
            }
        }
    }

    // IComputerItem implementation

    @Override
    public String getLabel( @Nonnull ItemStack stack )
    {
        return IComputerItem.super.getLabel( stack );
    }

    @Override
    public final ComputerFamily getFamily()
    {
        return family;
    }

    // IMedia implementation

    @Override
    public boolean setLabel( @Nonnull ItemStack stack, String label )
    {
        if( label != null )
        {
            stack.setDisplayName( new TextComponentString( label ) );
        }
        else
        {
            stack.clearCustomName();
        }
        return true;
    }

    @Override
    public IMount createDataMount( @Nonnull ItemStack stack, @Nonnull World world )
    {
        ComputerFamily family = getFamily();
        if( family != ComputerFamily.Command )
        {
            int id = getComputerID( stack );
            if( id >= 0 )
            {
                return ComputerCraftAPI.createSaveDirMount( world, "computer/" + id, ComputerCraft.computerSpaceLimit );
            }
        }
        return null;
    }
}
