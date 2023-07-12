package it.mikeslab.identity.utils.postprocess;

import lombok.Getter;

public class PostCommand {
    @Getter
    private final String command;
    @Getter
    private final SenderType senderType;


    public PostCommand(final String command, final SenderType senderType) {
        this.command = command;
        this.senderType = senderType;
    }

}


