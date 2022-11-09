
- Dont hardcode above 10k requires a Chief approval, but simply specify role
- If no conditions are met, send a request to any FINANCE department employee
- A rule can only have one action
- Due to highly relation content, chose sql database as was included // misschien niet deze includen
- Bigdecimals are saved as varchars in DB to maintain precision
- Priority of rules are in descending order of cutoff_amount
- Currently a rule can only have 1 condition and 1 action but this has been modelled to easily allow for 1 to many relationships, potentially allowing several conditions and actions per rule
- primary key uses auto incrementing int, but in production i would use UUID
- Used the compository pattern to allow for nested entities and thus preventing lots of manual joins