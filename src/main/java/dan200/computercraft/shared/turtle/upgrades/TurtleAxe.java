/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.turtle.upgrades;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class TurtleAxe extends TurtleTool
{
    public TurtleAxe( ResourceLocation id, int legacyId, String adjective, Item item )
    {
        super( id, legacyId, adjective, item );
    }

    public TurtleAxe( ResourceLocation id, int legacyId, Item item )
    {
        super( id, legacyId, item );
    }

    @Override
    protected float getDamageMultiplier()
    {
        return 6.0f;
    }
}
