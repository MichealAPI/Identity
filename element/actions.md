# Actions

### Actions in GUI Elements

Actions define the behavior of elements inside your GUI. There are both **global actions** and **gui-specific** actions.



{% hint style="info" %}
#### Format: `<action>: <args>`

**Note**: Actions are case-insensitive
{% endhint %}

## Global actions

<table><thead><tr><th width="127">Name</th><th width="352">Arguments</th><th>Description</th></tr></thead><tbody><tr><td>POTION</td><td><strong>Potion</strong>, <strong>Duration</strong> (in seconds), <strong>Amplifier</strong> (level) [%<strong>chance</strong>].<br>Examples:<br><strong><code>WEAKNESS</code></strong><code>, 30, 1</code><br><strong><code>SLOWNESS</code></strong><code> 200 10</code><br><code>1, 10000, 100 %50</code></td><td>Apply a <strong>potion effect</strong> when the player clicks the element</td></tr><tr><td>SOUND</td><td><strong>Sound</strong>, <strong>Volume</strong>, <strong>Pitch</strong> <br>Examples<strong>:</strong><br><strong><code>ENTITY_PLAYER_BURP, 0.5, 1</code></strong><br><strong><code>BURP, 0.5, 1</code></strong><br><strong><code>MUSIC_END, 10</code></strong></td><td>Play a <strong>sound</strong> when the player clicks the element</td></tr><tr><td>MESSAGE</td><td>Your Message with or without <a href="../resource/colors.md"><strong>Colors</strong></a><strong>. It doesn't support animations! Hovers and clicks are Paper-Only</strong><br>Examples:<br><code>"&#x3C;red>Your favorite &#x3C;#FFFFF>message&#x3C;gray>!"</code></td><td>Send a <strong>message</strong> to the player when he clicks the element</td></tr><tr><td>TITLE</td><td>&#x3C;title>;&#x3C;subtitle> with or without <a href="../resource/colors.md"><strong>Colors</strong></a><strong>. It doesn't support animations!</strong> <br>Examples:<br><code>"&#x3C;gradient:red:green>Title;Subtitle"</code></td><td>Send a <strong>title</strong> to the player when he clicks the element</td></tr><tr><td>CLOSE</td><td>None</td><td>Closes the inventory and returns to the Fallback Main GUI if any required GUIs are incomplete.</td></tr><tr><td>OPEN</td><td><a href="../config/how-to-install-your-gui-configs.md"><strong>&#x3C;inventory-id-name></strong></a><br>Examples:<br><code>"first-inventory"</code><br><code>"second-inventory"</code></td><td>Opens an inventory</td></tr><tr><td>PLAYER</td><td><strong>Command</strong> without the initial / (Slash)</td><td>Executes a command for the <strong>Player</strong></td></tr><tr><td>CONSOLE</td><td><strong>Command</strong> without the initial / (Slash)</td><td>Executes a command for the <strong>Console</strong></td></tr></tbody></table>



## GUI-Specific Actions

### Value Menu

| Name    | Arguments                                                                                                                                                                                                                                                                      | Description                                                                                                                                  |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------- |
| MESSAGE | <p>Your Message with or without <a href="../resource/colors.md"><strong>Colors</strong></a><strong>. It doesn't support animations! Hovers and clicks are Paper-Only</strong><br>Examples:<br><code>"&#x3C;red>Your favorite &#x3C;#FFFFF>&#x3C;value>&#x3C;gray>!"</code></p> | Send a **message** to the player when he clicks the element. Supports the **\<value>** (As it is, follows MiniMessage's format) placeholders |
| VALUE   | <p><strong>&#x3C;modifier>&#x3C;value></strong><br>Examples:<br>- <code>-2</code><br>- <code>+2</code><br>- <code>*2</code><br>- <code>/2</code></p>                                                                                                                           | Edit the GUI's value                                                                                                                         |
| SELECT  | None                                                                                                                                                                                                                                                                           | Confirm the GUI's current value                                                                                                              |

### Selector Menu

| Name   | Arguments                                                                                                                    | Description                        |
| ------ | ---------------------------------------------------------------------------------------------------------------------------- | ---------------------------------- |
| SELECT | <p>&#x3C;value><br>Examples:<br><code>"&#x3C;red>Selected Value"</code><br><code>"Male"</code><br><code>"Warrior"</code></p> | Select the clicked element's value |
