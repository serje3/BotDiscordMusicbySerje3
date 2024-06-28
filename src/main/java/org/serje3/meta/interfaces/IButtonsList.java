package org.serje3.meta.interfaces;

import org.serje3.meta.abs.Button;
import org.serje3.meta.abs.Command;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IButtonsList {
    List<Class<? extends Button>> getButtons();
    void setButtons(List<Class<? extends Button>> buttons);
    void forEachButton(Consumer<Button> func);
}
