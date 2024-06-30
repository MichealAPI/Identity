package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.GuiContext;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.inventory.action.ActionListener;
import it.mikeslab.identity.inventory.impl.template.GuiTemplate;
import it.mikeslab.identity.inventory.pojo.ValueMenuContext;
import it.mikeslab.identity.pojo.Condition;
import it.mikeslab.identity.util.SetupMap;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Getter @Setter
public class ValueMenu extends GuiTemplate implements ActionListener {

    private static final Map<String, BiFunction<Integer, Integer, Integer>> OPERATIONS;

    static {
        Map<String, BiFunction<Integer, Integer, Integer>> operations = new HashMap<>();
        operations.put("+", (a, b) -> a + b);
        operations.put("-", (a, b) -> a - b);
        operations.put("*", (a, b) -> a * b);
        operations.put("/", (a, b) -> a / b);
        OPERATIONS = Collections.unmodifiableMap(operations);
    }

    private int value;

    private final int min, max;


    public ValueMenu(IdentityPlugin instance, GuiContext guiContext, ValueMenuContext context) {
        super(instance, guiContext);

        this.min = context.getMin();
        this.max = context.getMax();
        this.value = context.getBaseValue();

        Optional<Supplier<String>> valueSupplier = Optional.of(() -> String.valueOf(this.getValue()));

        this.injectAction(instance, "value", this.applyMathOperation());
        this.injectAction(
                instance,
                "select",
                this.handleSelection(
                        instance,
                        valueSupplier,
                        true,
                        Optional.empty()
                )
        );

        // Overriding default to enable placeholder value
        this.injectAction(instance, "message", this.getMessageAction());


        GuiDetails detailsClone = this
                .getGuiContext()
                .getDefaultGuiDetails()
                .clone();

        detailsClone.setInjectedConditionPlaceholders(
                Collections.singletonMap(
                        "%value%",
                        ()-> String.valueOf(getValue())
                )
        );


        this.setPlaceholders(detailsClone);

    }


    @Override
    public void setPlaceholders(GuiDetails guiDetails) {

        Map<String, Supplier<String>> placeholders = new HashMap<>();
        placeholders.put("%value%", () -> String.valueOf(this.getValue()));

        guiDetails.setPlaceholders(placeholders);

        super.setPlaceholders(guiDetails);
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

            int value = Integer.parseInt(amount);
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

            String fieldIdentifier = this.getGuiContext().getFieldIdentifier();
            UUID uuid = event.getWhoClicked().getUniqueId();

            SetupMap setupMap = this.getInstance()
                    .getGuiConfigRegistrar()
                    .getPlayerInventories();

            setupMap.forceExpiration(uuid, fieldIdentifier); // updates the current inventory

            setupMap.getInventory(uuid, fieldIdentifier)
                    .show(event.getWhoClicked());
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

    /**
     * Get the message action
     * (Overrides the default action)
     * @return The action bi-consumer
     */
    private GuiAction getMessageAction() {
        return new GuiAction((event, args) -> {

            String message = ComponentsUtil.getSerializedComponent(
                    args,
                    Placeholder.unparsed("value", this.getValue() + "")
            );

            if(message == null) return;

            event.getWhoClicked().sendMessage(message);

        });
    }


}
