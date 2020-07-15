package at.ac.wuwien.causalminer.erp2graphdb.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class TransferredProcessInstance {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

//    @Convert(converter = UuidConverter.class)
    private UUID instanceId;
    private String startEventId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "transferred_process_instance_id")
    private List<Violation> violations = new ArrayList<>();

    public TransferredProcessInstance(UUID instanceId, String startEventId) {
        this.instanceId = instanceId;
        this.startEventId = startEventId;
    }

    public TransferredProcessInstance(UUID instanceId) {
        this.instanceId = instanceId;
        this.startEventId = "";
    }

}
