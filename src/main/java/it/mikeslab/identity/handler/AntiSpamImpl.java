package it.mikeslab.identity.handler;

import it.mikeslab.commons.api.config.Configurable;
import it.mikeslab.identity.util.AntiSpamUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AntiSpamImpl implements AntiSpam {

    private final Configurable spamConfigurable;

    private List<String> badWords;

    @Override
    public boolean isSpam(String message) {

        message = message.replace(" ", "");

        if(badWords.isEmpty()) {
            return false;
        }

        boolean isSpam = false;

        for(int i = 0; i < badWords.size() && !isSpam; i++) {

            String badWord = badWords.get(i);
            isSpam = AntiSpamUtil.isSpamming(message, badWord);

        }

        return isSpam;

    }

    @Override
    public boolean loadSpamWords() {

        try {

            badWords = new ArrayList<>();

            YamlConfiguration yamlConfig = spamConfigurable.getConfiguration();

            badWords = yamlConfig.getStringList("badWords");

            return true;

        } catch (Exception e) {
            return false;
        }


    }

    @Override
    public boolean addSpamWord(String word) {
        return this.badWords.add(word);
    }

    @Override
    public boolean removeSpamWord(String word) {
        return this.badWords.remove(word);
    }

    @Override
    public boolean isSpamWord(String word) {
        return this.badWords.contains(word);
    }

}
