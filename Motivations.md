
- Dont hardcode above 10k requires a Chief approval, but simply specify role
- If no conditions are met, send a request to any FINANCE department employee
- Bigdecimals are saved as varchars in DB to maintain precision
- Priority of rules are in descending order of cutoff_amount
- Currently a rule can only have 1 condition and 1 set of actions and 1 action but this has been modelled to easily allow for 1 to many relationships, potentially allowing several conditions and actions per rule
- primary key uses auto incrementing int, but in production i would use UUID
- We currently retrieve the entire data set in the CompanyRepository, preventing lots of manual joins. On production a wiser thing would be to split this up into multiple repositories to avoid huge memory usage