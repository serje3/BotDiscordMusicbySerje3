package org.serje3.meta.interfaces;

import lombok.Getter;
import org.serje3.meta.abs.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ICommandList {
    List<Class<? extends Command>> getCommands();
    void setCommands(List<Class<? extends Command>> commands);
    void forEachCommand(Consumer<Command> func);
}
