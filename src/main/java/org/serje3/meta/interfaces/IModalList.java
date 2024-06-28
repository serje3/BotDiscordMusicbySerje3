package org.serje3.meta.interfaces;

import org.serje3.meta.abs.Modal;

import java.util.List;
import java.util.function.Consumer;

public interface IModalList {
    List<Class<? extends Modal>> getModals();
    void setModals(List<Class<? extends Modal>> modals);
    void forEachModal(Consumer<Modal> func);
}
