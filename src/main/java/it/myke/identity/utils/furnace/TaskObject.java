package it.myke.identity.utils.furnace;

import it.myke.identity.disk.Settings;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class TaskObject {
    private boolean completed;
    private ItemStack customStack;
    private int age;


    public static TaskObject defaultObject() {
        return new TaskObject(false, null, Settings.MIN_AGE);
    }
}
