# Approval workflow challenge

### How to build & run with a custom invoice
```sh
./gradlew clean build
./gradlew run --args='1 5000 Finance false'
```

- **arg1** is an _int_ for the `companyId` (already prefilled for _1_)
- **arg2** is a _decimal_ for the `invoiceAmount` in dollars
- **arg3** is an _enum_ for the `department` (_Finance, Marketing_)
- **arg4** is a _bool_ for `manager_approval`

#### Quick command list from the Flowchart scenarios

```sh
./gradlew run --args='1 15000 Marketing true' # Sends request to marketing chief via email
./gradlew run --args='1 15000 Finance true'   # Sends request to finance chief via slack
./gradlew run --args='1 6000 Finance true'    # Sends request to finance manager via email
./gradlew run --args='1 6000 Finance false'   # Sends request to finance employee via slack
./gradlew run --args='1 4000 Finance true'    # Sends request to finance employee via slack
```

Test them out and check the logging to verify! Note that the default `chief_threshold` is $10,000.

### Assumptions

- We assume every department has at least 1 Chief, 1 manager and 1 regular employee.
- All Chiefs are managers, but not all managers are Chiefs and the same goes for managers and employees.
- When we want to notify a manager we will not notify a chief unless the invoice amount exceeds the `chief_threshold`.
- The same goes for employees. When we fall back to some rule or invoice which does not require a manager, we will only notify an employee that is not a manager.

### Design:

- A workflow specifies the amount which will always requires a Chief.
- If no conditions are met, send the request to a regular employee of the department attached to the fall back rule.
- For currencies we use `BigDecimals` in the database and application to maintain precision.
- Priority of rules are evaluated by _descending_ order of the `cutoff_amount` then the `department` and finally
  the `requires_approval`.
- An approval request will be sent to a manager if both the `invoice` and the `rule` require manager approval.

### Possible Improvements:
- We currently retrieve big data sets from the Database at once, preventing lots of manual lookups. On production a wiser thing could be to split this up into multiple queries to avoid huge memory usage.
- The primary keys use auto-incrementing integers but in production probably a `UUID` would be a better idea.
- Currently a rule can only have 1 set of conditions and 1 set of actions but this can be modelled to allow for several conditions and actions per rule.
- Allow for the possibility to assign manual priority to a rule as now all conditions are simply evaluated one by one.

### Database ERD:
![database_diagram](database_light.png)

### Flowchart:
![code_exercise_diagram](https://user-images.githubusercontent.com/112865589/191920630-6c4e8f8e-a8d9-42c2-b31e-ab2c881ed297.jpg)

