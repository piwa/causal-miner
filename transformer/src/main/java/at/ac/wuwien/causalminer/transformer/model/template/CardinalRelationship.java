package at.ac.wuwien.causalminer.transformer.model.template;

import lombok.Data;
import lombok.Getter;
import org.jgrapht.graph.DefaultEdge;

@Getter
public class CardinalRelationship extends DefaultEdge {

    private int cardinalityStart;
    private int cardinalityEnd;

    private boolean asymetric = false;

    public CardinalRelationship(int cardinalityStart, int cardinalityEnd, boolean asymetric) {
        this.cardinalityStart = cardinalityStart;
        this.cardinalityEnd = cardinalityEnd;
        this.asymetric = asymetric;
    }
}
