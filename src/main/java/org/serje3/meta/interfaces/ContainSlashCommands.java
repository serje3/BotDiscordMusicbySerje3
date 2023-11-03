package org.serje3.meta.interfaces;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public interface ContainSlashCommands {
    List<SlashCommandData> getSlashCommands();
}
