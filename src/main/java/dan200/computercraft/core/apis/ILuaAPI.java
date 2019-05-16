/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.core.apis;

/**
 * This exists purely to ensure binary compatibility.
 *
 * @see dan200.computercraft.api.lua.ILuaAPI
 */
@Deprecated
public interface ILuaAPI extends dan200.computercraft.api.lua.ILuaAPI
{
    void advance( double v );

    @Override
    default void update()
    {
        advance( 0.05 );
    }
}
