package org.serje3.utils.context;

import org.serje3.components.commands.suno.SunoCommand;
import org.serje3.components.modals.suno.SunoGenerateModal;
import org.serje3.components.modals.suno.SunoLoginModal;
import org.serje3.meta.abs.AdapterContext;

import java.util.ArrayList;

public class SunoAdapterContext extends AdapterContext {
    public SunoAdapterContext() {
        this.setCommands(new ArrayList<>(){
            {
                add(SunoCommand.class);
            }
        });

        this.setModals(new ArrayList<>(){
            {
                add(SunoLoginModal.class);
                add(SunoGenerateModal.class);
            }
        });
    }
}
