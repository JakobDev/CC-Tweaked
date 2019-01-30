/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.computer.blocks;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.BundledRedstone;
import dan200.computercraft.shared.Peripherals;
import dan200.computercraft.shared.common.TileGeneric;
import dan200.computercraft.shared.computer.core.ClientComputer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.util.RedstoneUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class TileComputerBase extends TileGeneric implements IComputerTile, ITickable
{
    private static final String TAG_ID = "computer_id";
    private static final String TAG_LABEL = "computer_label";
    private static final String TAG_INSTANCE = "instance_id";

    private int m_instanceId = -1;
    private int m_computerId = -1;
    protected String m_label = null;
    private boolean m_on = false;
    boolean m_startOn = false;
    private boolean m_fresh = false;

    private final ComputerFamily family;

    public TileComputerBase( TileEntityType<? extends TileGeneric> type, ComputerFamily family )
    {
        super( type );
        this.family = family;
    }

    protected void unload()
    {
        if( m_instanceId >= 0 )
        {
            if( !getWorld().isRemote ) ComputerCraft.serverComputerRegistry.remove( m_instanceId );
            m_instanceId = -1;
        }
    }

    @Override
    public void destroy()
    {
        unload();
        for( EnumFacing dir : DirectionUtil.FACINGS )
        {
            RedstoneUtil.propagateRedstoneOutput( getWorld(), getPos(), dir );
        }
    }

    @Override
    public void remove()
    {
        unload();
        super.remove();
    }

    public abstract void openGUI( EntityPlayer player );

    protected boolean canNameWithTag( EntityPlayer player )
    {
        return false;
    }

    protected boolean onDefaultComputerInteract( EntityPlayer player )
    {
        if( !getWorld().isRemote && isUsable( player, false ) )
        {
            createServerComputer().turnOn();
            openGUI( player );
        }
        return true;
    }

    @Override
    public boolean onActivate( EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ )
    {
        ItemStack item = player.getHeldItem( hand );
        if( !item.isEmpty() && item.getItem() == Items.NAME_TAG && canNameWithTag( player ) )
        {
            // Label to rename computer
            if( !getWorld().isRemote && item.hasDisplayName() )
            {
                setLabel( item.getDisplayName().getUnformattedComponentText() );
                item.shrink( 1 );
            }
            return true;
        }
        else if( !player.isSneaking() )
        {
            // Regular right click to activate computer
            return onDefaultComputerInteract( player );
        }
        return false;
    }

    @Override
    public void onNeighbourChange( @Nonnull BlockPos neighbour )
    {
        updateSideInput( neighbour );
    }

    @Override
    public void onNeighbourTileEntityChange( @Nonnull BlockPos neighbour )
    {
        updateSideInput( neighbour );
    }

    @Override
    public void tick()
    {
        if( getWorld().isRemote ) return;
        ServerComputer computer = createServerComputer();
        if( computer == null ) return;

        // If the computer isn't on and should be, then turn it on
        if( m_startOn || (m_fresh && m_on) )
        {
            computer.turnOn();
            m_startOn = false;
        }

        computer.keepAlive();

        m_fresh = false;
        m_computerId = computer.getId();
        m_label = computer.getLabel();
        m_on = computer.isOn();

        // Update the block state if needed. We don't fire a block update intentionally,
        // as this only really is needed on the client side.
        updateBlockState( computer.getState() );

        if( computer.hasOutputChanged() ) updateOutput();
    }

    protected abstract void updateBlockState( ComputerState newState );

    @Nonnull
    @Override
    public NBTTagCompound write( NBTTagCompound nbt )
    {
        // Save id, label and power state
        if( m_computerId >= 0 ) nbt.putInt( TAG_ID, m_computerId );
        if( m_label != null ) nbt.putString( TAG_LABEL, m_label );
        nbt.putBoolean( "on", m_on );

        return super.write( nbt );
    }

    @Override
    public void read( NBTTagCompound nbt )
    {
        super.read( nbt );

        // Load ID, label and power state
        m_computerId = nbt.contains( TAG_ID ) ? nbt.getInt( TAG_ID ) : -1;
        m_label = nbt.contains( TAG_LABEL ) ? nbt.getString( TAG_LABEL ) : null;
        m_on = m_startOn = nbt.getBoolean( "on" );
    }

    protected boolean isPeripheralBlockedOnSide( EnumFacing localSide )
    {
        return false;
    }

    protected boolean isRedstoneBlockedOnSide( EnumFacing localSide )
    {
        return false;
    }

    protected abstract EnumFacing getDirection();

    protected EnumFacing remapToLocalSide( EnumFacing globalSide )
    {
        return DirectionUtil.toLocal( getDirection(), globalSide );
    }

    private void updateSideInput( ServerComputer computer, EnumFacing dir, BlockPos offset )
    {
        EnumFacing offsetSide = dir.getOpposite();
        EnumFacing localDir = remapToLocalSide( dir );
        if( !isRedstoneBlockedOnSide( localDir ) )
        {
            computer.setRedstoneInput( localDir.getIndex(), getWorld().getRedstonePower( offset, dir ) );
            computer.setBundledRedstoneInput( localDir.getIndex(), BundledRedstone.getOutput( getWorld(), offset, offsetSide ) );
        }
        if( !isPeripheralBlockedOnSide( localDir ) )
        {
            computer.setPeripheral( localDir.getIndex(), Peripherals.getPeripheral( getWorld(), offset, offsetSide ) );
        }
    }

    public void updateAllInputs()
    {
        if( getWorld() == null || getWorld().isRemote ) return;

        // Update all sides
        ServerComputer computer = getServerComputer();
        if( computer == null ) return;

        BlockPos pos = computer.getPosition();
        for( EnumFacing dir : DirectionUtil.FACINGS )
        {
            updateSideInput( computer, dir, pos.offset( dir ) );
        }
    }

    private void updateSideInput( BlockPos neighbour )
    {
        if( getWorld() == null || getWorld().isRemote ) return;

        ServerComputer computer = getServerComputer();
        if( computer == null ) return;

        // Find the appropriate side and update.
        BlockPos pos = computer.getPosition();
        for( EnumFacing dir : DirectionUtil.FACINGS )
        {
            BlockPos offset = pos.offset( dir );
            if( offset.equals( neighbour ) )
            {
                updateSideInput( computer, dir, offset );
                break;
            }
        }
    }

    public void updateOutput()
    {
        // Update redstone
        updateBlock();
        for( EnumFacing dir : DirectionUtil.FACINGS )
        {
            RedstoneUtil.propagateRedstoneOutput( getWorld(), getPos(), dir );
        }
    }

    protected abstract ServerComputer createComputer( int instanceID, int id );

    public abstract ComputerProxy createProxy();

    @Override
    public int getComputerId()
    {
        return m_computerId;
    }

    @Override
    public String getLabel()
    {
        return m_label;
    }

    @Override
    public void setComputerId( int id )
    {
        if( !getWorld().isRemote && m_computerId != id )
        {
            m_computerId = id;
            ServerComputer computer = getServerComputer();
            if( computer != null )
            {
                computer.setID( m_computerId );
            }
            markDirty();
        }
    }

    @Override
    public void setLabel( String label )
    {
        if( !getWorld().isRemote && !Objects.equals( this.m_label, label ) )
        {
            m_label = label;
            ServerComputer computer = getServerComputer();
            if( computer != null ) computer.setLabel( label );
            markDirty();
        }
    }

    @Override
    public ComputerFamily getFamily()
    {
        return family;
    }

    public ServerComputer createServerComputer()
    {
        if( getWorld().isRemote ) return null;

        boolean changed = false;
        if( m_instanceId < 0 )
        {
            m_instanceId = ComputerCraft.serverComputerRegistry.getUnusedInstanceID();
            changed = true;
        }

        if( !ComputerCraft.serverComputerRegistry.contains( m_instanceId ) )
        {
            ServerComputer computer = createComputer( m_instanceId, m_computerId );
            ComputerCraft.serverComputerRegistry.add( m_instanceId, computer );
            m_fresh = true;
            changed = true;
        }

        if( changed )
        {
            updateBlock();
            updateAllInputs();
        }
        return ComputerCraft.serverComputerRegistry.get( m_instanceId );
    }

    public ServerComputer getServerComputer()
    {
        return !getWorld().isRemote ? ComputerCraft.serverComputerRegistry.get( m_instanceId ) : null;
    }

    public ClientComputer createClientComputer()
    {
        if( !getWorld().isRemote || m_instanceId < 0 ) return null;

        ClientComputer computer = ComputerCraft.clientComputerRegistry.get( m_instanceId );
        if( computer == null )
        {
            ComputerCraft.clientComputerRegistry.add( m_instanceId, computer = new ClientComputer( m_instanceId ) );
        }
        return computer;
    }

    public ClientComputer getClientComputer()
    {
        return getWorld().isRemote ? ComputerCraft.clientComputerRegistry.get( m_instanceId ) : null;
    }

    // Networking stuff

    @Override
    protected NBTTagCompound writeDescription( NBTTagCompound nbt )
    {
        // The client needs to know about the computer ID and label in order to provide pick-block
        // functionality
        if( m_computerId >= 0 ) nbt.putInt( TAG_ID, m_computerId );
        if( m_label != null ) nbt.putString( TAG_LABEL, m_label );
        nbt.putInt( TAG_INSTANCE, createServerComputer().getInstanceID() );

        return super.writeDescription( nbt );
    }

    @Override
    protected void readDescription( NBTTagCompound nbt )
    {
        m_computerId = nbt.contains( TAG_ID ) ? nbt.getInt( TAG_ID ) : -1;
        m_label = nbt.contains( TAG_LABEL ) ? nbt.getString( TAG_LABEL ) : null;
        m_instanceId = nbt.contains( TAG_INSTANCE ) ? nbt.getInt( TAG_INSTANCE ) : -1;

        super.readDescription( nbt );
    }

    protected void transferStateFrom( TileComputerBase copy )
    {
        if( copy.m_computerId != m_computerId || copy.m_instanceId != m_instanceId )
        {
            unload();
            m_instanceId = copy.m_instanceId;
            m_computerId = copy.m_computerId;
            m_label = copy.m_label;
            m_on = copy.m_on;
            m_startOn = copy.m_startOn;
            updateBlock();
        }
        copy.m_instanceId = -1;
    }
}
