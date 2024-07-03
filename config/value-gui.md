---
cover: ../.gitbook/assets/Gitbook banner - Value.png
coverY: 0
---

# Value GUI

{% hint style="info" %}
The full configuration is not provided here, as it's included in the premium default resource. After installation, refer to the `plugins/Identity/inventories` folder for real-world examples.
{% endhint %}

Here are the properties you can configure in your **Value GUI**:

| Property | Description                                                                                        | Mandatory |
| -------- | -------------------------------------------------------------------------------------------------- | --------- |
| title    | The Value GUI title                                                                                | Yes       |
| layout   | The Value GUI layout, please refer to the Layout section for more information                      | Yes       |
| type     | The Inventory Type (**Dispenser**, **Chest**, **Hopper** or **Dropper**)                           | Yes       |
| size     | The Inventory Size, strictly related to the inventory type. Example: **DROPPER**'s size = (3\*3=9) | Yes       |
| elements | The GUI elements                                                                                   | No        |

{% hint style="info" %}
To modify the base, max, and min values, refer to the [**How to install your GUI configs**](how-to-install-your-gui-configs.md) documentation. For conditions, check the [**Condition**](../element/conditions.md)[**s**](../element/conditions.md) section. To create buttons for editing values, see the [**Action** ](../element/actions.md)section.
{% endhint %}
