package org.serje3.adapters;


import org.serje3.meta.abs.AdapterContext;
import org.serje3.meta.abs.BaseListenerAdapter;
import org.serje3.utils.commands.LogAdapterContext;

public class LogAdapter extends BaseListenerAdapter {
    @Override
    protected AdapterContext getAdapterContext() {
        return new LogAdapterContext();
    }
}
