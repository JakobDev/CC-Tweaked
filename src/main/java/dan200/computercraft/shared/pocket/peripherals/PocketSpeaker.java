/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.pocket.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.shared.peripheral.PeripheralType;
import dan200.computercraft.shared.peripheral.common.PeripheralItemFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PocketSpeaker extends AbstractPocketUpgrade
{
    public PocketSpeaker()
    {
        super(
            new ResourceLocation( "computercraft", "speaker" ),
            PeripheralItemFactory.create( PeripheralType.Speaker, null, 1 )
        );
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral( @Nonnull IPocketAccess access )
    {
        return new PocketSpeakerPeripheral();
    }

    @Override
    public void update( @Nonnull IPocketAccess access, @Nullable IPeripheral peripheral )
    {
        if( !(peripheral instanceof PocketSpeakerPeripheral) ) return;

        PocketSpeakerPeripheral speaker = (PocketSpeakerPeripheral) peripheral;

        Entity entity = access.getValidEntity();
        if( entity != null )
        {
            speaker.setLocation( entity.getEntityWorld(), entity.getPositionEyes( 1.0f ) );
        }

        speaker.update();
        access.setLight( speaker.madeSound( 20 ) ? 0x3320fc : -1 );
    }
}
