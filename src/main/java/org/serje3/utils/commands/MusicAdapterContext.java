package org.serje3.utils.commands;

import org.serje3.components.autocomplete.music.SearchAutocomplete;
import org.serje3.components.buttons.music.*;
import org.serje3.components.commands.music.*;
import org.serje3.components.commands.music.filters.BassBoostCommand;
import org.serje3.components.commands.music.filters.EBANUTIYBassBoostCommand;
import org.serje3.components.commands.music.filters.NormalizeFilterCommand;
import org.serje3.components.commands.music.queue.*;
import org.serje3.meta.abs.AdapterContext;

import java.util.ArrayList;

public class MusicAdapterContext extends AdapterContext {
    public MusicAdapterContext() {
        this.setCommands(new ArrayList<>() {
            {
                add(RadioCommand.class);
                add(PauseCommand.class);
                add(JoinCommand.class);
                add(LeaveCommand.class);
                add(TTSCommand.class);
                add(QueueCommand.class);
                add(QueueTracksCommand.class);
                add(QueueSkipCommand.class);
                add(QueueClearCommand.class);
                add(QueueNowCommand.class);
                add(NormalizeFilterCommand.class);
                add(BassBoostCommand.class);
                add(EBANUTIYBassBoostCommand.class);
                add(QueueCockinizeCommand.class);
            }
        });
        this.setButtons(new ArrayList<>(){
            {
                add(PauseButton.class);
                add(PausePlayButton.class);
                add(SkipButton.class);
                add(RepeatButton.class);
                add(RepeatButton.On.class);
                add(AddToQueueButton.class);
            }
        });
        this.setAutoCompletes(new ArrayList<>(){
            {
                add(SearchAutocomplete.class);
            }
        });
    }
}
