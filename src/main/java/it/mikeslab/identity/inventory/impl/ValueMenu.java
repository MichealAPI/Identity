package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.inventory.ValueMenuContext;
import it.mikeslab.identity.inventory.action.ActionListener;
import it.mikeslab.identity.inventory.impl.template.GuiTemplate;
import it.mikeslab.identity.pojo.Condition;
import it.mikeslab.identity.pojo.InventorySettings;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Getter @Setter
public class ValueMenu extends GuiTemplate implements ActionListener {

    private static final Map<String, BiFunction<Double, Double, Double>> OPERATIONS;

    static {
        Map<String, BiFunction<Double, Double, Double>> operations = new HashMap<>();
        operations.put("+", (a, b) -> a + b);
        operations.put("-", (a, b) -> a - b);
        operations.put("*", (a, b) -> a * b);
        operations.put("/", (a, b) -> a / b);
        OPERATIONS = Collections.unmodifiableMap(operations);
    }

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
                this.handleSelection(valueSupplier, true, Optional.empty())
        );


    }


    @Override
    public void setPlaceholders(Player player, GuiDetails guiDetails) {

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%value%", this.getValue() + "");

        guiDetails.setPlaceholders(placeholders);

        super.setPlaceholders(player, guiDetails);
    }


    @Override
    public void show(Player player) {

        GuiDetails detailsClone = this
                .getInventoryContext()
                .getDefaultGuiDetails()
                .clone();

        detailsClone.setInjectedConditionPlaceholders(
                Collections.singletonMap(
                        "%value%",
                        ()-> String.valueOf(getValue())
                )
        );

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
            double result = OPERATIONS
                    .get(operator)
                    .apply(this.value, value);

            Condition condition = isValueInRange(result);
            boolean isValid = condition.isValid();
            String errorMessage = condition.getErrorMessage().orElse(null);

            if(!isValid) {

                if(errorMessage != null) {
                    event.getWhoClicked().sendMessage(errorMessage);
                }

                return;
            }

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
    private Condition isValueInRange(double value) {
        return new Condition(
                value >= this.min && value <= this.max,
                Optional.of(getInstance().getLanguage().getSerializedString(
                        LanguageKey.VALUE_OUT_OF_RANGE,
                        Placeholder.unparsed("min", this.min + ""),
                        Placeholder.unparsed("max", this.max + "")

                )));
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
