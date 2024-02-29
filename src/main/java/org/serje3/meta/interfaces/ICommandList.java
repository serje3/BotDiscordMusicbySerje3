package org.serje3.meta.interfaces;

import lombok.Getter;
import org.serje3.meta.abs.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ICommandList {
    List<Class<?>> getCommands();
    void setCommands(List<Class<?>> commands);
    void forEachCommand(Function<Command, ?> func);
}
