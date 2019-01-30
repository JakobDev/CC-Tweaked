/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.network.container;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.inventory.ContainerComputer;
import dan200.computercraft.shared.peripheral.diskdrive.ContainerDiskDrive;
import dan200.computercraft.shared.peripheral.printer.ContainerPrinter;
import dan200.computercraft.shared.turtle.inventory.ContainerTurtle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * Opens a GUI on a specific ComputerCraft TileEntity
 *
 * @see dan200.computercraft.shared.peripheral.diskdrive.TileDiskDrive
 * @see dan200.computercraft.shared.peripheral.printer.TilePrinter
 * @see dan200.computercraft.shared.computer.blocks.TileComputer
 */
public class BlockEntityContainerType<T extends Container> implements ContainerType<T>
{
    private static final ResourceLocation DISK_DRIVE = new ResourceLocation( ComputerCraft.MOD_ID, "disk_drive" );
    private static final ResourceLocation PRINTER = new ResourceLocation( ComputerCraft.MOD_ID, "printer" );
    private static final ResourceLocation COMPUTER = new ResourceLocation( ComputerCraft.MOD_ID, "computer" );
    private static final ResourceLocation TURTLE = new ResourceLocation( ComputerCraft.MOD_ID, "turtle" );

    public BlockPos pos;
    private final ResourceLocation id;

    private BlockEntityContainerType( ResourceLocation id, BlockPos pos )
    {
        this.id = id;
        this.pos = pos;
    }

    private BlockEntityContainerType( ResourceLocation id )
    {
        this.id = id;
    }

    @Nonnull
    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public void toBytes( PacketBuffer buf )
    {
        buf.writeBlockPos( pos );
    }

    @Override
    public void fromBytes( PacketBuffer buf )
    {
        pos = buf.readBlockPos();
    }

    public TileEntity getTileEntity( EntityPlayer entity )
    {
        return entity.world.getTileEntity( pos );
    }

    public static BlockEntityContainerType<ContainerDiskDrive> diskDrive()
    {
        return new BlockEntityContainerType<>( DISK_DRIVE );
    }

    public static BlockEntityContainerType<ContainerDiskDrive> diskDrive( BlockPos pos )
    {
        return new BlockEntityContainerType<>( DISK_DRIVE, pos );
    }

    public static BlockEntityContainerType<ContainerPrinter> printer()
    {
        return new BlockEntityContainerType<>( PRINTER );
    }

    public static BlockEntityContainerType<ContainerPrinter> printer( BlockPos pos )
    {
        return new BlockEntityContainerType<>( PRINTER, pos );
    }

    public static BlockEntityContainerType<ContainerComputer> computer()
    {
        return new BlockEntityContainerType<>( COMPUTER );
    }

    public static BlockEntityContainerType<ContainerComputer> computer( BlockPos pos )
    {
        return new BlockEntityContainerType<>( COMPUTER, pos );
    }

    public static BlockEntityContainerType<ContainerTurtle> turtle()
    {
        return new BlockEntityContainerType<>( TURTLE );
    }

    public static BlockEntityContainerType<ContainerTurtle> turtle( BlockPos pos )
    {
        return new BlockEntityContainerType<>( TURTLE, pos );
    }
}
