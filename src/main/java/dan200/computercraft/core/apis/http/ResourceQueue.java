/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.core.apis.http;

import java.util.ArrayDeque;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * A {@link ResourceGroup} which will queue items when the group at capacity.
 */
public class ResourceQueue<T extends Resource<T>> extends ResourceGroup<T>
{
    private final ArrayDeque<Supplier<T>> pending = new ArrayDeque<>();

    public ResourceQueue( IntSupplier limit )
    {
        super( limit );
    }

    public ResourceQueue()
    {
    }

    @Override
    public synchronized void shutdown()
    {
        super.shutdown();
        pending.clear();
    }

    @Override
    public synchronized boolean queue( Supplier<T> resource )
    {
        if( !active ) return false;

        if( !super.queue( resource ) ) pending.add( resource );
        return true;
    }

    @Override
    public synchronized void release( T resource )
    {
        super.release( resource );

        if( !active ) return;

        int limit = this.limit.getAsInt();
        if( limit <= 0 || resources.size() < limit )
        {
            Supplier<T> next = pending.poll();
            if( next != null ) resources.add( next.get() );
        }
    }
}
