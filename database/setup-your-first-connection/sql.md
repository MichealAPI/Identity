---
description: Establish your first SQL Connection
---

# SQL



## MySQL

Follow the format and create your own MySQL Connection string:

```yaml
database:
  type: SQL
  # Format: protocol//[hosts][/database][?properties]
  uri: "jdbc:mysql://127.0.0.1:3307/api_testunit" # Example Connection String
  password: ""
  username: "root" # It's recommended NOT to use the root user, 
                   # please create one with writing and reading privileges
  table: "identities" # Auto-Generated if not exists
  database: ""
```

<figure><img src="../../.gitbook/assets/Mysql_logo.png" alt=""><figcaption></figcaption></figure>

## SQLite

Follow the format and create your own SQLite Connection String, if the file doesn't exists, it will be generated:

```yaml
database:
  type: SQL
  uri: "jdbc:sqlite:{dataFolder}/identity.db" # Example Connection String
  password: ""
  username: ""
  table: "identities" # Auto-Generated if not exists 
  database: ""
```
