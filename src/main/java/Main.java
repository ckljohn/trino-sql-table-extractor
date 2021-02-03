import static io.trino.sql.parser.ParsingOptions.DecimalLiteralTreatment.AS_DOUBLE;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.trino.sql.parser.ParsingOptions;
import io.trino.sql.parser.SqlParser;
import io.trino.sql.tree.Statement;
import io.trino.sql.tree.Table;
import io.trino.sql.tree.WithQuery;


public class Main {
    
    public static void main(String[] args) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, UTF_8));
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return;
            }

            List<String> tables = getTables(line);
            String result = mapper.writeValueAsString(tables);
            System.out.println(result);
        }
    }

    public static List<String> getTables(String sql) {
        DepthFirstVisitor<Table, ?> tableVisitor = DepthFirstVisitor.by(Extractors.extractTables());
        DepthFirstVisitor<WithQuery, ?> withQueryVisitor = DepthFirstVisitor.by(Extractors.extractWithQueries());

        Statement statement = new SqlParser().createStatement(sql, new ParsingOptions(AS_DOUBLE));

        List<Table> tables = statement.accept(tableVisitor, null)
                .collect(Collectors.toList());

        List<WithQuery> withQueries = statement.accept(withQueryVisitor, null)
                .collect(Collectors.toList());

        List<String> tableNames = tables.stream().map(table -> table.getName().toString()).distinct().collect(Collectors.toList());
        
        List<String> withQueryNames = withQueries.stream().map(withQuery -> withQuery.getName().getValue()).collect(Collectors.toList());
        
        tableNames.removeAll(withQueryNames);
        return tableNames;
    }
}
