# trino-sql-table-extractor
This extractor extract table names from Trino/Presto/Athena SQL by Trino native SQL parser.

I am not a Java developer. I just implement a stackoverflow answer ([link](https://stackoverflow.com/a/63182794/10783757)) and take a *frsyuki*'s repo ([frsyuki/trino_sql_parser](https://github.com/frsyuki/trino_sql_parser)) as a reference to create this.

# Build
```bash
./gradlew shadowJar
```
A jar file (trino-sql-table-extractor-x.x.x-all.jar) will be built under build/libs/

# Usage
```bash
echo "with t1 as (select c from foo.bar) select * from t1" | java -jar <.jar path>
```

## Integreation with Python
Require jnius
```python
import jnius_config
jnius_config.set_classpath('<.jar path>')

from jnius import autoclass

main = autoclass('Main')

sql = "with t1 as (select c from foo.bar) select * from t1"
tables = main.getTables(sql).toArray()

print(tables)
```
