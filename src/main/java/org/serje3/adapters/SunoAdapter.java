package org.serje3.adapters;

import org.serje3.meta.abs.AdapterContext;
import org.serje3.meta.abs.BaseListenerAdapter;
import org.serje3.utils.context.SunoAdapterContext;

public class SunoAdapter extends BaseListenerAdapter {
    @Override
    protected AdapterContext getAdapterContext() {
        return new SunoAdapterContext();
    }
}
