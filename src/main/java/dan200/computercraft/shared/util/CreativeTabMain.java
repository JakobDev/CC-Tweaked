/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.util;

import dan200.computercraft.ComputerCraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CreativeTabMain extends CreativeTabs
{
    public CreativeTabMain( int i )
    {
        super( i, ComputerCraft.MOD_ID );
    }

    @Nonnull
    @Override
    @SideOnly( Side.CLIENT )
    public ItemStack createIcon()
    {
        return new ItemStack( ComputerCraft.Blocks.computer );
    }
}
