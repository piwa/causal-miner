package at.ac.wuwien.causalminer.erp2graphdb.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Violation {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    private ViolatingNode violationStartNode;

    @OneToOne(cascade = CascadeType.ALL)
    private ViolatingNode violationEndNode;

    @Enumerated(EnumType.STRING)
    private ViolationType violationType;


    public Violation(ViolatingNode violationStartNode, ViolatingNode violationEndNode, ViolationType violationType) {
        this.violationStartNode = violationStartNode;
        this.violationEndNode = violationEndNode;
        this.violationType = violationType;
    }
}
