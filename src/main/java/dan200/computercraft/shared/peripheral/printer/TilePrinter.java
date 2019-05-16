/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.peripheral.printer;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.media.items.ItemPrintout;
import dan200.computercraft.shared.network.Containers;
import dan200.computercraft.shared.peripheral.PeripheralType;
import dan200.computercraft.shared.peripheral.common.BlockPeripheral;
import dan200.computercraft.shared.peripheral.common.TilePeripheralBase;
import dan200.computercraft.shared.util.DefaultSidedInventory;
import dan200.computercraft.shared.util.WorldUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class TilePrinter extends TilePeripheralBase implements DefaultSidedInventory
{
    // Statics

    private static final int[] bottomSlots = new int[] { 7, 8, 9, 10, 11, 12 };
    private static final int[] topSlots = new int[] { 1, 2, 3, 4, 5, 6 };
    private static final int[] sideSlots = new int[] { 0 };

    // Members

    private final NonNullList<ItemStack> m_inventory = NonNullList.withSize( 13, ItemStack.EMPTY );
    private final IItemHandlerModifiable m_itemHandlerAll = new InvWrapper( this );
    private IItemHandlerModifiable[] m_itemHandlerSides;

    private final Terminal m_page = new Terminal( ItemPrintout.LINE_MAX_LENGTH, ItemPrintout.LINES_PER_PAGE );
    private String m_pageTitle = "";
    private boolean m_printing = false;

    @Override
    public void destroy()
    {
        ejectContents();
    }

    @Override
    public boolean onActivate( EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ )
    {
        if( player.isSneaking() ) return false;

        if( !getWorld().isRemote ) Containers.openPrinterGUI( player, this );
        return true;
    }

    @Override
    public void readFromNBT( NBTTagCompound nbt )
    {
        super.readFromNBT( nbt );

        // Read page
        synchronized( m_page )
        {
            m_printing = nbt.getBoolean( "printing" );
            m_pageTitle = nbt.getString( "pageTitle" );
            m_page.readFromNBT( nbt );
        }

        // Read inventory
        synchronized( m_inventory )
        {
            NBTTagList nbttaglist = nbt.getTagList( "Items", Constants.NBT.TAG_COMPOUND );
            for( int i = 0; i < nbttaglist.tagCount(); i++ )
            {
                NBTTagCompound itemTag = nbttaglist.getCompoundTagAt( i );
                int j = itemTag.getByte( "Slot" ) & 0xff;
                if( j < m_inventory.size() )
                {
                    m_inventory.set( j, new ItemStack( itemTag ) );
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT( NBTTagCompound nbt )
    {
        // Write page
        synchronized( m_page )
        {
            nbt.setBoolean( "printing", m_printing );
            nbt.setString( "pageTitle", m_pageTitle );
            m_page.writeToNBT( nbt );
        }

        // Write inventory
        synchronized( m_inventory )
        {
            NBTTagList nbttaglist = new NBTTagList();
            for( int i = 0; i < m_inventory.size(); i++ )
            {
                if( !m_inventory.get( i ).isEmpty() )
                {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setByte( "Slot", (byte) i );
                    m_inventory.get( i ).writeToNBT( tag );
                    nbttaglist.appendTag( tag );
                }
            }
            nbt.setTag( "Items", nbttaglist );
        }

        return super.writeToNBT( nbt );
    }

    @Override
    public final void readDescription( @Nonnull NBTTagCompound nbt )
    {
        super.readDescription( nbt );
        updateBlock();
    }

    @Override
    public boolean shouldRefresh( World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState )
    {
        return super.shouldRefresh( world, pos, oldState, newState ) || BlockPeripheral.getPeripheralType( newState ) != PeripheralType.Printer;
    }

    public boolean isPrinting()
    {
        return m_printing;
    }

    // IInventory implementation

    @Override
    public int getSizeInventory()
    {
        return m_inventory.size();
    }

    @Override
    public boolean isEmpty()
    {
        for( ItemStack stack : m_inventory )
        {
            if( !stack.isEmpty() ) return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot( int i )
    {
        return m_inventory.get( i );
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot( int i )
    {
        synchronized( m_inventory )
        {
            ItemStack result = m_inventory.get( i );
            m_inventory.set( i, ItemStack.EMPTY );
            markDirty();
            updateAnim();
            return result;
        }
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize( int i, int j )
    {
        synchronized( m_inventory )
        {
            if( m_inventory.get( i ).isEmpty() ) return ItemStack.EMPTY;

            if( m_inventory.get( i ).getCount() <= j )
            {
                ItemStack itemstack = m_inventory.get( i );
                m_inventory.set( i, ItemStack.EMPTY );
                markDirty();
                updateAnim();
                return itemstack;
            }

            ItemStack part = m_inventory.get( i ).splitStack( j );
            if( m_inventory.get( i ).isEmpty() )
            {
                m_inventory.set( i, ItemStack.EMPTY );
                updateAnim();
            }
            markDirty();
            return part;
        }
    }

    @Override
    public void setInventorySlotContents( int i, @Nonnull ItemStack stack )
    {
        synchronized( m_inventory )
        {
            m_inventory.set( i, stack );
            markDirty();
            updateAnim();
        }
    }

    @Override
    public void clear()
    {
        synchronized( m_inventory )
        {
            for( int i = 0; i < m_inventory.size(); i++ ) m_inventory.set( i, ItemStack.EMPTY );
            markDirty();
            updateAnim();
        }
    }

    @Override
    public boolean isItemValidForSlot( int slot, @Nonnull ItemStack stack )
    {
        if( slot == 0 )
        {
            return isInk( stack );
        }
        else if( slot >= topSlots[0] && slot <= topSlots[topSlots.length - 1] )
        {
            return isPaper( stack );
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean hasCustomName()
    {
        return getLabel() != null;
    }

    @Nonnull
    @Override
    public String getName()
    {
        String label = getLabel();
        return label != null ? label : "tile.computercraft:printer.name";
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName()
    {
        return hasCustomName() ? new TextComponentString( getName() ) : new TextComponentTranslation( getName() );
    }

    @Override
    public boolean isUsableByPlayer( @Nonnull EntityPlayer player )
    {
        return isUsable( player, false );
    }

    // ISidedInventory implementation

    @Nonnull
    @Override
    public int[] getSlotsForFace( @Nonnull EnumFacing side )
    {
        switch( side )
        {
            case DOWN: // Bottom (Out tray)
                return bottomSlots;
            case UP: // Top (In tray)
                return topSlots;
            default: // Sides (Ink)
                return sideSlots;
        }
    }

    // IPeripheralTile implementation

    @Override
    public IPeripheral getPeripheral( @Nonnull EnumFacing side )
    {
        return new PrinterPeripheral( this );
    }

    public Terminal getCurrentPage()
    {
        return m_printing ? m_page : null;
    }

    public boolean startNewPage()
    {
        synchronized( m_inventory )
        {
            if( !canInputPage() ) return false;
            if( m_printing && !outputPage() ) return false;
            return inputPage();
        }
    }

    public boolean endCurrentPage()
    {
        synchronized( m_inventory )
        {
            if( m_printing && outputPage() )
            {
                return true;
            }
        }
        return false;
    }

    public int getInkLevel()
    {
        synchronized( m_inventory )
        {
            ItemStack inkStack = m_inventory.get( 0 );
            return isInk( inkStack ) ? inkStack.getCount() : 0;
        }
    }

    public int getPaperLevel()
    {
        int count = 0;
        synchronized( m_inventory )
        {
            for( int i = 1; i < 7; i++ )
            {
                ItemStack paperStack = m_inventory.get( i );
                if( !paperStack.isEmpty() && isPaper( paperStack ) )
                {
                    count += paperStack.getCount();
                }
            }
        }
        return count;
    }

    public void setPageTitle( String title )
    {
        if( m_printing )
        {
            m_pageTitle = title;
        }
    }

    private static boolean isInk( @Nonnull ItemStack stack )
    {
        return stack.getItem() == Items.DYE;
    }

    private static boolean isPaper( @Nonnull ItemStack stack )
    {
        Item item = stack.getItem();
        return item == Items.PAPER || item instanceof ItemPrintout && ItemPrintout.getType( stack ) == ItemPrintout.Type.Single;
    }

    private boolean canInputPage()
    {
        synchronized( m_inventory )
        {
            ItemStack inkStack = m_inventory.get( 0 );
            return !inkStack.isEmpty() && isInk( inkStack ) && getPaperLevel() > 0;
        }
    }

    private boolean inputPage()
    {
        synchronized( m_inventory )
        {
            ItemStack inkStack = m_inventory.get( 0 );
            if( !isInk( inkStack ) ) return false;

            for( int i = 1; i < 7; i++ )
            {
                ItemStack paperStack = m_inventory.get( i );
                if( !paperStack.isEmpty() && isPaper( paperStack ) )
                {
                    // Setup the new page
                    int colour = inkStack.getItemDamage();
                    m_page.setTextColour( colour >= 0 && colour < 16 ? 15 - colour : 15 );

                    m_page.clear();
                    if( paperStack.getItem() instanceof ItemPrintout )
                    {
                        m_pageTitle = ItemPrintout.getTitle( paperStack );
                        String[] text = ItemPrintout.getText( paperStack );
                        String[] textColour = ItemPrintout.getColours( paperStack );
                        for( int y = 0; y < m_page.getHeight(); y++ )
                        {
                            m_page.setLine( y, text[y], textColour[y], "" );
                        }
                    }
                    else
                    {
                        m_pageTitle = "";
                    }
                    m_page.setCursorPos( 0, 0 );

                    // Decrement ink
                    inkStack.shrink( 1 );
                    if( inkStack.isEmpty() ) m_inventory.set( 0, ItemStack.EMPTY );

                    // Decrement paper
                    paperStack.shrink( 1 );
                    if( paperStack.isEmpty() )
                    {
                        m_inventory.set( i, ItemStack.EMPTY );
                        updateAnim();
                    }

                    markDirty();
                    m_printing = true;
                    return true;
                }
            }
            return false;
        }
    }

    private boolean outputPage()
    {
        synchronized( m_page )
        {
            int height = m_page.getHeight();
            String[] lines = new String[height];
            String[] colours = new String[height];
            for( int i = 0; i < height; i++ )
            {
                lines[i] = m_page.getLine( i ).toString();
                colours[i] = m_page.getTextColourLine( i ).toString();
            }

            ItemStack stack = ItemPrintout.createSingleFromTitleAndText( m_pageTitle, lines, colours );
            synchronized( m_inventory )
            {
                for( int slot : bottomSlots )
                {
                    if( m_inventory.get( slot ).isEmpty() )
                    {
                        setInventorySlotContents( slot, stack );
                        m_printing = false;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void ejectContents()
    {
        synchronized( m_inventory )
        {
            for( int i = 0; i < 13; i++ )
            {
                ItemStack stack = m_inventory.get( i );
                if( !stack.isEmpty() )
                {
                    // Remove the stack from the inventory
                    setInventorySlotContents( i, ItemStack.EMPTY );

                    // Spawn the item in the world
                    BlockPos pos = getPos();
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 0.75;
                    double z = pos.getZ() + 0.5;
                    WorldUtil.dropItemStack( stack, getWorld(), x, y, z );
                }
            }
        }
    }

    private void updateAnim()
    {
        synchronized( m_inventory )
        {
            int anim = 0;
            for( int i = 1; i < 7; i++ )
            {
                ItemStack stack = m_inventory.get( i );
                if( !stack.isEmpty() && isPaper( stack ) )
                {
                    anim += 1;
                    break;
                }
            }
            for( int i = 7; i < 13; i++ )
            {
                ItemStack stack = m_inventory.get( i );
                if( !stack.isEmpty() && isPaper( stack ) )
                {
                    anim += 2;
                    break;
                }
            }
            setAnim( anim );
        }
    }

    @Override
    public boolean hasCapability( @Nonnull Capability<?> capability, @Nullable EnumFacing facing )
    {
        return capability == ITEM_HANDLER_CAPABILITY || super.hasCapability( capability, facing );
    }

    @Nullable
    @Override
    public <T> T getCapability( @Nonnull Capability<T> capability, @Nullable EnumFacing facing )
    {
        if( capability == ITEM_HANDLER_CAPABILITY )
        {
            if( facing == null )
            {
                return ITEM_HANDLER_CAPABILITY.cast( m_itemHandlerAll );
            }
            else
            {
                IItemHandlerModifiable[] handlers = m_itemHandlerSides;
                if( handlers == null ) handlers = m_itemHandlerSides = new IItemHandlerModifiable[6];

                int i = facing.ordinal();
                IItemHandlerModifiable handler = handlers[i];
                if( handler == null ) handler = handlers[i] = new SidedInvWrapper( this, facing );

                return ITEM_HANDLER_CAPABILITY.cast( handler );
            }
        }
        return super.getCapability( capability, facing );
    }
}
