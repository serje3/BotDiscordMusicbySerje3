package org.serje3;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.serje3.adapters.MusicAdapter;


public class BotApplication {


    public static void main(String[] args) throws Exception {
            ListenerAdapter adapter = new MusicAdapter();
    }
}
