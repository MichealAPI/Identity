# Layout

**Custom GUIs** use a **Layout-To-Char** system where each character corresponds to a slot in the inventory. To function correctly, the number of slots must match the inventory's type and size.

### Examples:

#### Chest:

```yaml
- "         "
- "         " # 3*9 = 27 slots/size
- "         "
```

#### Dropper:

```yaml
- "   "
- "   " # 3*3 = 9 slots/size
- "   "
```

**Double-Chest:**

```yaml
- "         "
- "         "
- "         "
- "         " # 9*6 = 54 slots/size
- "         "
- "         "
```

You can then replace one space with a character (a-z, 0-9) and edit it through the elements section. Take a look at the [Element ](broken-reference)documentation
