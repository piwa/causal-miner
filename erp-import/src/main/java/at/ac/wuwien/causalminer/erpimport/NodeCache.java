package at.ac.wuwien.causalminer.erpimport;

import at.ac.wuwien.causalminer.erpimport.model.ErpId;
import at.ac.wuwien.causalminer.transformer.model.INode;

import java.util.HashMap;
import java.util.Map;

public class NodeCache {

    public static Map<ErpId, INode> nodes = new HashMap<>();

}
