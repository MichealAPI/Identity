---
description: Establish your first MongoDB Connection
---

# MongoDB

Copy the URI string from MongoDB Compass or Atlas and set up your configuration as shown in the example below:

```yaml
database:
 type: MONGODB
 uri: "mongodb://localhost:27017" # Example URI
 database: YourDatabase # Your database name
 table: YourCollection # Stands for Collection, if it doesn't exists, it will be created
 username: "" # Not Needed 
 password: "Your Password"
```

<figure><img src="../../.gitbook/assets/MongoDB_Logo.svg" alt="" width="563"><figcaption></figcaption></figure>
