package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.ValueMenuContext;
import it.mikeslab.identity.inventory.action.ActionListener;
import it.mikeslab.identity.inventory.impl.template.GuiTemplate;
import it.mikeslab.identity.pojo.InventorySettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Getter @Setter
public class ValueMenu extends GuiTemplate implements ActionListener {

    private static final Map<String, BiFunction<Double, Double, Double>> OPERATIONS = Map.of(
            "+", (a, b) -> a + b,
            "-", (a, b) -> a - b,
            "*", (a, b) -> a * b,
            "/", (a, b) -> a / b
    );

    private double value;

    private final double min, max;


    public ValueMenu(IdentityPlugin instance, InventorySettings settings, ValueMenuContext context) {
        super(instance, settings);

        this.min = context.getMin();
        this.max = context.getMax();
        this.value = context.getBaseValue();

        Optional<Supplier<String>> valueSupplier = Optional.of(() -> String.valueOf(this.getValue()));

        this.injectAction("value", this.applyMathOperation());
        this.injectAction(
                "select",
                this.handleSelection(valueSupplier, true),
                this.isValueInRange()
        );
    }


    @Override
    public void setPlaceholders(Player player, GuiDetails guiDetails) {
        guiDetails.setPlaceholders(
                Map.of("%value%", value + "")
        );

        super.setPlaceholders(player, guiDetails);
    }


    @Override
    public void show(Player player) {

        // todo limits and other stuff

        GuiDetails detailsClone = this
                .getInventoryContext()
                .getDefaultGuiDetails()
                .clone();

        this.setPlaceholders(player, detailsClone);

        super.show(player);

    }


    /**
     * Inject the operator action inside the value menu
     * @return The action listener
     */
    private GuiAction applyMathOperation() {
        return new GuiAction((event, args) -> {

            String operator = args.charAt(0) + "";

            if(!isOperator(operator)) {
                operator = "+";
            }

            String amount = args.substring(1);

            if(!isInteger(amount)) {
                return;
            }

            double value = Double.parseDouble(amount);

            this.value = OPERATIONS
                    .get(operator)
                    .apply(this.value, value);

            this.show(event.getWhoClicked());
        });
    }

    /**
     * Handle the selection of the value
     * @return The action listener
     */
    private Predicate<Void> isValueInRange() {
        return (v) -> this.getValueSupplier().get() >= this.min && this.getValueSupplier().get() <= this.max;
    }

    private Supplier<Double> getValueSupplier() {
        return () -> this.value;
    }

    private boolean isOperator(String character) {
        return OPERATIONS.containsKey(character);
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
