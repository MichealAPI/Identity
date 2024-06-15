package it.mikeslab.identity.handler;

public interface AntiSpam {

    boolean isSpam(String message);

    boolean loadSpamWords();

    boolean addSpamWord(String word);

    boolean removeSpamWord(String word);

    boolean isSpamWord(String word);

}
