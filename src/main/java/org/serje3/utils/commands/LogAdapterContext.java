package org.serje3.utils.commands;

import org.serje3.components.commands.log.DickCommand;
import org.serje3.components.commands.log.InfoCommand;
import org.serje3.meta.abs.AdapterContext;

import java.util.ArrayList;

public class LogAdapterContext extends AdapterContext {
    public LogAdapterContext() {
        this.setCommands(new ArrayList<>() {
            {
                add(InfoCommand.class);
                add(DickCommand.class);
            }
        });
    }
}
