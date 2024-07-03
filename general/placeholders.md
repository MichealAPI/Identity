# Placeholders

Placeholders are automatic and follow this format `%identity_<inventory-id-name>%`. To better understand that, we can look at the **default configuration file**. This file will typically include multiple instances where placeholders are used to represent specific values, ensuring that the configuration remains flexible and adaptable to different environments and contexts.

```yaml
guis:
  first-inventory: # Inventory ID name
    path: "inventories/selector.yml"
    type: SELECTOR
    mandatory: true
    displayName: "First Inventory"

  second-inventory: # Inventory ID name
    path: "inventories/value.yml"
    type: VALUE
    mandatory: true
    displayName: "Second Inventory"
    base: 1
    max: 20
    min: -10

  third-inventory: # Inventory ID name
    path: "inventories/input.yml"
    type: INPUT
    mandatory: true
    displayName: "Third Inventory"
    # Other settings are inside the inventory file

  main-inventory:
    path: "inventories/main.yml"
    type: MAIN
```

The code above includes three inventories (VALUE, INPUT, SELECTOR), each with a unique Inventory ID. To retrieve a value from an inventory, use the following formats (`%identity_<inventory-id-name>%`):

* **`%identity_third-inventory%`**
* **`%identity_second-inventory%`**
* **`%identity_first-inventory%`**
