package org.serje3.utils.commands;

import org.serje3.commands.music.*;
import org.serje3.commands.music.filters.BassBoostCommand;
import org.serje3.commands.music.filters.EBANUTIYBassBoostCommand;
import org.serje3.commands.music.filters.NormalizeFilterCommand;
import org.serje3.commands.music.queue.*;
import org.serje3.meta.abs.Command;
import org.serje3.meta.abs.CommandList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MusicCommandList extends CommandList {
    public MusicCommandList() {
        this.setCommands(new ArrayList<>() {
            {
//              add(PlayCommand.class); deprecated, use QueueCommand instead
                add(GachiCommand.class);
                add(LofiCommand.class);
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
            }
        });
    }
}
