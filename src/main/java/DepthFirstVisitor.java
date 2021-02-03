import java.util.Objects;
import java.util.stream.Stream;

import io.trino.sql.tree.AstVisitor;
import io.trino.sql.tree.Node;
import io.trino.sql.tree.Table;
import io.trino.sql.tree.WithQuery;


public class DepthFirstVisitor<R, C> extends AstVisitor<Stream<R>, C> 
{
    private final AstVisitor<R, C> visitor;

    public DepthFirstVisitor(AstVisitor<R, C> visitor) {
        this.visitor = visitor;
    }

    public static <R, C> DepthFirstVisitor<R, C> by(AstVisitor<R, C> visitor) {
        return new DepthFirstVisitor<>(visitor);
    }

    @Override
    public final Stream<R> visitNode(Node node, C context) {
        Stream<R> nodeResult = Stream.of(visitor.process(node, context));
        Stream<R> childrenResult = node.getChildren().stream()
                .flatMap(child -> process(child, context));
        
        return Stream.concat(nodeResult, childrenResult)
                .filter(Objects::nonNull);
    }
}

class Extractors {
    public static AstVisitor<Table, Object> extractTables() {
        return new AstVisitor<Table, Object>() {
            @Override
            protected Table visitTable(Table node, Object context) {
                return node;
            }
        };
    }

    public static AstVisitor<WithQuery, Object> extractWithQueries() {
        return new AstVisitor<WithQuery, Object>() {
            @Override
            protected WithQuery visitWithQuery(WithQuery node, Object context) {
                // System.out.println(node.getName());
                return node;
            }
        };
    }
}
