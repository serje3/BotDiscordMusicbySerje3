package org.serje3.adapters;

import org.serje3.meta.abs.AdapterContext;
import org.serje3.meta.abs.BaseListenerAdapter;
import org.serje3.utils.context.DefaultAdapterContext;

public class DefaultAdapter extends BaseListenerAdapter {
    @Override
    protected AdapterContext getAdapterContext() {
        return new DefaultAdapterContext();
    }
}
