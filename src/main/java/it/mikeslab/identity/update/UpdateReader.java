package it.mikeslab.identity.update;

import it.mikeslab.identity.Identity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CharSequenceReader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class UpdateReader {

    public Reader getReaderFromStream(InputStream initialStream)
            throws IOException {

        byte[] buffer = IOUtils.toByteArray(initialStream);
        return new CharSequenceReader(new String(buffer));
    }


    public ArrayList<String> getVersions(Identity identity) {
        FileConfiguration fc = null;
        try {
            fc = YamlConfiguration.loadConfiguration(getReaderFromStream(identity.getResource("updates.yml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fc.getKeys(false).size() == 0) {
            return null;
        }

        return fc.getKeys(false).stream().map(Object::toString).collect(Collectors.toCollection(ArrayList::new));
    }


    public ArrayList<Update> getUpdates(Identity identity, String version) {
        FileConfiguration fc = null;
        try {
            fc = YamlConfiguration.loadConfiguration(getReaderFromStream(identity.getResource("updates.yml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fc.getConfigurationSection(version).getKeys(false).size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<Update> updateArrayList = new ArrayList<>();

        for (String updateNumber : fc.getConfigurationSection(version).getKeys(false)) {
            updateArrayList.add(new Update(UpdateType.valueOf(fc.getConfigurationSection(version).getString(updateNumber + ".type")), fc.getConfigurationSection(version).getString(updateNumber + ".description")));
        }


        return updateArrayList;
    }


}
