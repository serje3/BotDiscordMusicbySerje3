package org.serje3.utils.commands;

import org.serje3.commands.base.DonateCommand;
import org.serje3.commands.base.HelpCommand;
import org.serje3.meta.abs.CommandList;

import java.util.ArrayList;

public class DefaultCommandList extends CommandList {
    public DefaultCommandList() {
        this.setCommands(new ArrayList<>() {
            {
                add(HelpCommand.class);
                add(DonateCommand.class);
            }
        });
    }
}
