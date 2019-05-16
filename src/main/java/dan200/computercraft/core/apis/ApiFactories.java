/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.core.apis;

import dan200.computercraft.api.lua.ILuaAPIFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;

public final class ApiFactories
{
    private ApiFactories()
    {
    }

    private static final Collection<ILuaAPIFactory> factories = new LinkedHashSet<>();
    private static final Collection<ILuaAPIFactory> factoriesView = Collections.unmodifiableCollection( factories );

    public static void register( @Nonnull ILuaAPIFactory factory )
    {
        Objects.requireNonNull( factory, "provider cannot be null" );
        factories.add( factory );
    }

    public static Iterable<ILuaAPIFactory> getAll()
    {
        return factoriesView;
    }
}
