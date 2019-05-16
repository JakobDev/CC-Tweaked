/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.common;

import dan200.computercraft.api.redstone.IBundledRedstoneProvider;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DefaultBundledRedstoneProvider implements IBundledRedstoneProvider
{
    @Override
    public int getBundledRedstoneOutput( @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side )
    {
        return getDefaultBundledRedstoneOutput( world, pos, side );
    }

    public static int getDefaultBundledRedstoneOutput( World world, BlockPos pos, EnumFacing side )
    {
        Block block = world.getBlockState( pos ).getBlock();
        if( block instanceof BlockGeneric )
        {
            BlockGeneric generic = (BlockGeneric) block;
            if( generic.getBundledRedstoneConnectivity( world, pos, side ) )
            {
                return generic.getBundledRedstoneOutput( world, pos, side );
            }
        }
        return -1;
    }
}
