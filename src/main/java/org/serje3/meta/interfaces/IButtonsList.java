package org.serje3.meta.interfaces;

import org.serje3.meta.abs.Button;
import org.serje3.meta.abs.Command;

import java.util.List;
import java.util.function.Function;

public interface IButtonsList {
    List<Class<?>> getButtons();
    void setButtons(List<Class<?>> buttons);
    void forEachButton(Function<Button, ?> func);
}
