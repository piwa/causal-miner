package at.ac.wuwien.causalminer.transformer;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.util.TypeUtil;

import java.util.function.Supplier;

@Slf4j
public class DefaultDirectedTestGraph<V, E> extends DefaultDirectedTemplateGraph<V, E> {

    public DefaultDirectedTestGraph(Class edgeClass) {
        super(edgeClass);
    }

    public DefaultDirectedTestGraph(Supplier vertexSupplier, Supplier edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, weighted);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Graph<V, E> g = TypeUtil.uncheckedCast(obj);

        if (!vertexSet().equals(g.vertexSet())) {
            return false;
        }
        if (edgeSet().size() != g.edgeSet().size()) {
            return false;
        }

        for (E graph1Edge : edgeSet()) {
            V sourceEdgeGraph1 = getEdgeSource(graph1Edge);
            V targetEdgeGraph1 = getEdgeTarget(graph1Edge);

            if(!g.containsEdge(sourceEdgeGraph1, targetEdgeGraph1)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
