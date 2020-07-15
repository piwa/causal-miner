package at.ac.wuwien.causalminer.erp2graphdb.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class ViolatingNode {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Long nodeGraphDbId;
    private String originalId;
    private String eventType;

    public ViolatingNode(Long nodeGraphDbId, String originalId, String eventType) {
        this.nodeGraphDbId = nodeGraphDbId;
        this.originalId = originalId;
        this.eventType = eventType;
    }
}
