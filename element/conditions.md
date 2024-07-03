# Conditions

## Visibility Conditions

Conditions are based on expressions containing one or more operators. You can use both Placeholders from **PlaceholderAPI** and internals from the **Value GUI** (`%value%`).

{% hint style="warning" %}
## Item Visibility Conditions

Conditions dictate item visibility. If you don't define a base element **without** a condition, the last condition-satisfied element may remain visible even if its condition is no longer met.
{% endhint %}

## Logical Operators

| Operator | Description                                                                                                                                                                                                                                                                                                                                                                                                                         |
| -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ==       | An equality relationship between two elements can be represented using the double-equals sign                                                                                                                                                                                                                                                                                                                                       |
| !=       | **A** not equals **B**                                                                                                                                                                                                                                                                                                                                                                                                              |
| >=       | **A** greater than or equal **B**                                                                                                                                                                                                                                                                                                                                                                                                   |
| <=       | **A** less than or equal **B**                                                                                                                                                                                                                                                                                                                                                                                                      |
| <        | **A** less than **B**                                                                                                                                                                                                                                                                                                                                                                                                               |
| >        | **A** greater than **B**                                                                                                                                                                                                                                                                                                                                                                                                            |
| \|\|     | <p><strong>OR</strong>, at least one condition needs to be true Example:<br><strong>A &#x3C;= B</strong> || <strong>B == B</strong><br><strong>B == B</strong>: <mark style="color:green;">True</mark><br><strong>A &#x3C;= B</strong>: <mark style="color:red;">False</mark><br><strong>Result</strong>: <mark style="color:green;">True</mark></p>                                                                                |
| &&       | <p><strong>AND</strong>, a pair of conditions needs to be true<br>Example:<br><strong>A</strong> &#x3C;= <strong>B</strong> &#x26;&#x26; <strong>B</strong> == <strong>B</strong><br><strong>B</strong> == <strong>B</strong>: <mark style="color:green;">True</mark><br><strong>A</strong> &#x3C;= <strong>B</strong>: <mark style="color:red;">False</mark><br><strong>Result:</strong> <mark style="color:red;">False</mark></p> |

## Examples

> "%value% <= 5 && %player\_name% == Michael"
>
> "%playerpoints\_value% > 1 || %your\_favorite\_placeholder% != 0"



## Resources

* **Logical Operators:** [https://press.rebus.community/programmingfundamentals/chapter/logical-operators/](https://press.rebus.community/programmingfundamentals/chapter/logical-operators/)
