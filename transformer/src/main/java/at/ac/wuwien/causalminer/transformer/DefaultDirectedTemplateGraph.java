package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.template.TemplateNode;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Supplier;

@Slf4j
public class DefaultDirectedTemplateGraph<V, E> extends DefaultDirectedImporterGraph<V, E> {

    public DefaultDirectedTemplateGraph(Class edgeClass) {
        super(edgeClass);
    }

    public DefaultDirectedTemplateGraph(Supplier vertexSupplier, Supplier edgeSupplier, boolean weighted) {
        super(vertexSupplier, edgeSupplier, weighted);
    }


    @Override
    public String toString() {
        try {
            ComponentNameProvider<V> vertexIdProvider = node -> String.valueOf(((TemplateNode) node).getId().hashCode());
            ComponentNameProvider<V> vertexLabelProvider = node -> ((TemplateNode) node).getEventType();

            GraphExporter<V, E> exporter = new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
            Writer writer = new StringWriter();

            exporter.exportGraph(this, writer);

            return writer.toString();
        } catch (ExportException e) {
            log.error("Exception", e);
        }
        return "";
    }

}