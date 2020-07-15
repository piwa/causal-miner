package at.ac.wuwien.causalminer.neo4jdb.domain.query.input;

import lombok.Data;

@Data
public class AsymmetricConflictInput {

    private String sourceNodeType;
    private String targetNodeType;

    public AsymmetricConflictInput(String sourceNodeType, String targetNodeType) {
        this.sourceNodeType = sourceNodeType;
        this.targetNodeType = targetNodeType;
    }
}
