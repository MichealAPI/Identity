# Page System

To apply Page Systems automatically, simply define more elements than there are slots for a group of elements.

{% hint style="info" %}
Refer to the [Element](broken-reference) documentation for other Element properties
{% endhint %}

{% hint style="warning" %}
To enhance performance, animations within page systems have been disabled.
{% endhint %}

```yaml
layout:
  - "   "
  - "agb"
  - "   "
...
elements:
  'g':
    isGroupElement: true
    ...
  'g-1':
    isGroupElement: true
    ...
  'g-2':
    isGroupElement: true
    ...
...
# Next and Previous Buttons
  'a':
    displayName: "<green>Next Page"
    material: ARROW
    internalValue: NEXT_PAGE_G # This is REQUIRED for it to work  
  'b':
    displayName: "<red>Previous Page"
    material: REDSTONE
    internalValue: PREVIOUS_PAGE_G # G refers to the group element we've 
                                   # previously defined
```
