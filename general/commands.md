# Commands

{% hint style="info" %}
**Important Information**

You can add as many aliases as you want to the main command inside the `config.yml`. To add multiple aliases, use the `|` character to separate each alias.

**Example:**

```
aliases: "first|second|third"
```
{% endhint %}

## <mark style="color:red;">**Administrator Commands**</mark>

| Command              | Description                            | Permission          |
| -------------------- | -------------------------------------- | ------------------- |
| /identity reload     | Reloads the configs                    | identity.reload     |
| /identity reset      | Reset the Identity of a player         | identity.reset      |
| /identity loadPreset | Loads a preset from the presets folder | identity.loadpreset |

## <mark style="color:blue;">Player Commands</mark>

| Command         | Description                                                                                                       | Permission     |
| --------------- | ----------------------------------------------------------------------------------------------------------------- | -------------- |
| /identity setup | Initiates the identity setup process when the identity is not configured and automatic setup on join is disabled. | identity.setup |
