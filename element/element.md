# Element

{% hint style="info" %}
For layout setup assistance, refer to the [**Layout** ](../gui/layout.md)documentation
{% endhint %}

{% hint style="info" %}
Need help coloring? Please, visit the [**Colors** ](../resource/colors.md)section
{% endhint %}

| Property       | Description                                                                                   | Mandatory |
| -------------- | --------------------------------------------------------------------------------------------- | --------- |
| displayName    | The Item's Display name                                                                       | Yes       |
| lore           | The Item's Lore                                                                               | No        |
| material       | The Item Material                                                                             | Yes       |
| internalValue  | Needed for paging purposes, refer to the **Page Systems** documentation                       | No        |
| glowing        | Item Glowing                                                                                  | No        |
| isGroupElement | If it is part of a Group of Elements. Please, refer to the **Page Systems** documentation     | No        |
| data           | Custom Model Data Value                                                                       | No        |
| actions        | List of Actions that it is going to perform on click. Please refer to the **Actions** section | No        |

## Example

```yaml
"a":
  displayName: "<animate:#FFFFF:#00000>Example"
  lore:
  - " "
  - "<gray>This is my favorite <blue>Example<gray>!"
  - " "
  material: RED_DYE
  glowing: true
  actions:
  - "message: What a wonderful button!"
  - "sound: BLOCK_NOTE_BLOCK_BELL, 0.5, 1"
  data: 9999
```

