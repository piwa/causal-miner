package at.ac.wuwien.causalminer.transformer;

import at.ac.wuwien.causalminer.transformer.model.EndNode;
import at.ac.wuwien.causalminer.transformer.model.StartNode;

import java.util.Map;
import java.util.UUID;

public class StartAndEndNodeUtility {

    public static StartNode createStartNode(UUID instanceId, Map<String, String> eventIds) {
        StartNode currentNode = new StartNode(instanceId, eventIds);
        return currentNode;
    }

    public static EndNode createEndNode(UUID instanceId) {
        EndNode currentNode = EndNode.of(instanceId);
        return currentNode;
    }

}
