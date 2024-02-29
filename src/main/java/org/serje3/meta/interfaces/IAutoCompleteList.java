package org.serje3.meta.interfaces;

import org.serje3.meta.abs.AutoComplete;
import org.serje3.meta.abs.Command;

import java.util.List;
import java.util.function.Function;

public interface IAutoCompleteList {
    List<Class<?>> getAutoCompletes();
    void setAutoCompletes(List<Class<?>> commands);
    void forEachAutoComplete(Function<AutoComplete, ?> func);
}
