package at.ac.wuwien.causalminer.erpimport.model;


import at.ac.wuwien.causalminer.transformer.model.INode;

import java.util.UUID;

public interface GraphNodeBuilder {

    INode getNode(INode parentNode, UUID instanceId);

}
