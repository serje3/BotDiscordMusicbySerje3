package org.serje3.utils.exceptions;

public class NoTrackIsPlayingNow extends Exception{
    public NoTrackIsPlayingNow() {
        super("Никаких треков сейчас не играет");
    }
}
