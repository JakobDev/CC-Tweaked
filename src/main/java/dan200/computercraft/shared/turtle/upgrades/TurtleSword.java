/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.upgrades;

import dan200.computercraft.shared.turtle.core.TurtlePlayer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TurtleSword extends TurtleTool
{
    public TurtleSword( ResourceLocation id, int legacyId, String adjective, Item item )
    {
        super( id, legacyId, adjective, item );
    }

    public TurtleSword( ResourceLocation id, int legacyId, Item item )
    {
        super( id, legacyId, item );
    }

    @Override
    protected boolean canBreakBlock( IBlockState state, World world, BlockPos pos, TurtlePlayer player )
    {
        if( !super.canBreakBlock( state, world, pos, player ) ) return false;

        Material material = state.getMaterial();
        return material == Material.PLANTS ||
            material == Material.LEAVES ||
            material == Material.VINE ||
            material == Material.CLOTH ||
            material == Material.WEB;
    }

    @Override
    protected float getDamageMultiplier()
    {
        return 9.0f;
    }
}
