package org.serje3.meta.interfaces;

import org.serje3.meta.abs.AutoComplete;
import org.serje3.meta.abs.Command;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IAutoCompleteList {
    List<Class<? extends AutoComplete>> getAutoCompletes();
    void setAutoCompletes(List<Class<? extends AutoComplete>> commands);
    void forEachAutoComplete(Consumer<AutoComplete> func);
}
