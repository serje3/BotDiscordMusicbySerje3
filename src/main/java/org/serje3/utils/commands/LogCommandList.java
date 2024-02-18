package org.serje3.utils.commands;

import org.serje3.commands.log.InfoCommand;
import org.serje3.commands.log.TestCommand;
import org.serje3.commands.music.*;
import org.serje3.commands.music.filters.BassBoostCommand;
import org.serje3.commands.music.filters.EBANUTIYBassBoostCommand;
import org.serje3.commands.music.filters.NormalizeFilterCommand;
import org.serje3.commands.music.queue.*;
import org.serje3.meta.abs.CommandList;

import java.util.ArrayList;

public class LogCommandList extends CommandList {
    public LogCommandList() {
        this.setCommands(new ArrayList<>() {
            {
                add(TestCommand.class);
                add(InfoCommand.class);
            }
        });
    }
}
