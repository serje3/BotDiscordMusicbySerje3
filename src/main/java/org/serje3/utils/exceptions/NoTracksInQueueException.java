package org.serje3.utils.exceptions;

public class NoTracksInQueueException extends Exception{
    public NoTracksInQueueException (){
        super("Нет треков в очереди");
    }
}
