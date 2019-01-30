/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.peripheral.modem.wired;

import com.google.common.collect.ImmutableMap;
import dan200.computercraft.shared.peripheral.modem.ModemShapes;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.shapes.ShapeUtils;
import net.minecraft.util.math.shapes.VoxelShape;

import java.util.EnumMap;

import static dan200.computercraft.shared.peripheral.modem.wired.BlockCable.*;

public final class CableShapes
{
    private static final double MIN = 0.375;
    private static final double MAX = 1 - MIN;

    private static final VoxelShape SHAPE_CABLE_CORE = ShapeUtils.create( MIN, MIN, MIN, MAX, MAX, MAX );
    private static final EnumMap<EnumFacing, VoxelShape> SHAPE_CABLE_ARM =
        new EnumMap<>( new ImmutableMap.Builder<EnumFacing, VoxelShape>()
            .put( EnumFacing.DOWN, ShapeUtils.create( MIN, 0, MIN, MAX, MIN, MAX ) )
            .put( EnumFacing.UP, ShapeUtils.create( MIN, MAX, MIN, MAX, 1, MAX ) )
            .put( EnumFacing.NORTH, ShapeUtils.create( MIN, MIN, 0, MAX, MAX, MIN ) )
            .put( EnumFacing.SOUTH, ShapeUtils.create( MIN, MIN, MAX, MAX, MAX, 1 ) )
            .put( EnumFacing.WEST, ShapeUtils.create( 0, MIN, MIN, MIN, MAX, MAX ) )
            .put( EnumFacing.EAST, ShapeUtils.create( MAX, MIN, MIN, 1, MAX, MAX ) )
            .build()
        );

    private static final VoxelShape[] SHAPES = new VoxelShape[(1 << 6) * 7];
    private static final VoxelShape[] CABLE_SHAPES = new VoxelShape[1 << 6];

    private CableShapes()
    {
    }

    private static int getCableIndex( IBlockState state )
    {
        int index = 0;
        for( EnumFacing facing : DirectionUtil.FACINGS )
        {
            if( state.get( CONNECTIONS.get( facing ) ) ) index |= 1 << facing.ordinal();
        }

        return index;
    }

    private static VoxelShape getCableShape( int index )
    {
        VoxelShape shape = CABLE_SHAPES[index];
        if( shape != null ) return shape;

        shape = SHAPE_CABLE_CORE;
        for( EnumFacing facing : DirectionUtil.FACINGS )
        {
            if( (index & (1 << facing.ordinal())) != 0 )
            {
                shape = ShapeUtils.or( shape, SHAPE_CABLE_ARM.get( facing ) );
            }
        }

        return CABLE_SHAPES[index] = shape;
    }

    public static VoxelShape getCableShape( IBlockState state )
    {
        if( !state.get( CABLE ) ) return ShapeUtils.empty();
        return getCableShape( getCableIndex( state ) );
    }

    public static VoxelShape getModemShape( IBlockState state )
    {
        EnumFacing facing = state.get( MODEM ).getFacing();
        return facing == null ? ShapeUtils.empty() : ModemShapes.getBounds( facing );
    }

    public static VoxelShape getShape( IBlockState state )
    {
        EnumFacing facing = state.get( MODEM ).getFacing();
        if( !state.get( CABLE ) ) return getModemShape( state );

        int cableIndex = getCableIndex( state );
        int index = cableIndex + ((facing == null ? 0 : facing.ordinal() + 1) << 6);

        VoxelShape shape = SHAPES[index];
        if( shape != null ) return shape;

        shape = getCableShape( cableIndex );
        if( facing != null ) shape = ShapeUtils.or( shape, ModemShapes.getBounds( facing ) );
        return SHAPES[index] = shape;
    }
}
