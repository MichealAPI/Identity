# How to install your GUI configs

{% hint style="warning" %}
A Main GUI must be defined; otherwise, the plugin will not work.
{% endhint %}

Congratulations on creating your first setup! This is the last step to bring your inventories into reality. It's super simple: open your `config.yml` and follow the provided scheme to finalize each Custom GUI. Once you're done, save and execute `/identity reload` or restart your server.

```yaml
guis:
  <inventory-id-name>:
    path: "inventories/selector.yml" # Relative Path, starting from plugins/Identity
    type: SELECTOR # SELECTOR, VALUE, INPUT, MAIN
    mandatory: true # true or false, if it must be completed
    displayName: "First Inventory" # This is what will be displayed when you're closing or confirming the Main GUI, 
                                   # and this one is not completed but mandatory.
    # Value Menu Only Properties
    base: 1 # The base value
    min: -10 # The minimum value
    max: 20 # The maximum value
```

