package org.serje3.utils.context;

import org.serje3.components.commands.base.DonateCommand;
import org.serje3.components.commands.base.HelpCommand;
import org.serje3.meta.abs.AdapterContext;

import java.util.ArrayList;

public class DefaultAdapterContext extends AdapterContext {
    public DefaultAdapterContext() {
        this.setCommands(new ArrayList<>() {
            {
                add(HelpCommand.class);
                add(DonateCommand.class);
            }
        });
    }
}
