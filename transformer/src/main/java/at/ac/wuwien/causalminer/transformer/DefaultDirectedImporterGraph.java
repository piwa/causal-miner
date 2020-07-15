package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.INode;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.function.Supplier;

@Slf4j
public class DefaultDirectedImporterGraph<V, E> extends DefaultDirectedGraph<V, E> {

    public DefaultDirectedImporterGraph(Class edgeClass) {
        super(edgeClass);
    }

    public DefaultDirectedImporterGraph(Supplier vertexSupplier, Supplier edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, weighted);
    }

    public boolean pathExists(V startNode, V endNode) {
        Iterator<V> iterator = new BreadthFirstIterator<>(this, startNode);
        while (iterator.hasNext()) {
            V currentNodeId = iterator.next();
            if(endNode.equals(currentNodeId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        try {
            ComponentNameProvider<V> vertexIdProvider = node -> String.valueOf(((INode) node).getId().hashCode());
            ComponentNameProvider<V> vertexLabelProvider = node -> ((INode) node).getLabel();

            GraphExporter<V, E> exporter = new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
            Writer writer = new StringWriter();

            exporter.exportGraph(this, writer);

            return writer.toString();
        } catch (ExportException e) {
            log.error("Exception", e);
        }
        return "";
    }

    public void saveSvg(String path) {
        try {
            Graphviz.fromString(toString()).render(Format.SVG).toFile(new File(path));
        } catch (IOException e) {
            log.error("Exception", e);
        }
    }

}
