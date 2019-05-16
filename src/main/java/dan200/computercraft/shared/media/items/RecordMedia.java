/*
 * This file is part of CC-Tweaked which is based on ComputerCraft by dan200 - https://computercraft.cc/
 * This code is licensed under the ComputerCraft Public License
 */

package dan200.computercraft.shared.media.items;

import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.util.RecordUtil;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nonnull;

/**
 * An implementation of IMedia for ItemRecord's
 */
public final class RecordMedia implements IMedia
{
    public static final RecordMedia INSTANCE = new RecordMedia();

    private RecordMedia()
    {
    }

    @Override
    public String getLabel( @Nonnull ItemStack stack )
    {
        return getAudioTitle( stack );
    }

    @Override
    public String getAudioTitle( @Nonnull ItemStack stack )
    {
        return RecordUtil.getRecordInfo( stack );
    }

    @Override
    public SoundEvent getAudio( @Nonnull ItemStack stack )
    {
        ItemRecord itemRecord = (ItemRecord) stack.getItem();
        return itemRecord.sound;
    }
}
