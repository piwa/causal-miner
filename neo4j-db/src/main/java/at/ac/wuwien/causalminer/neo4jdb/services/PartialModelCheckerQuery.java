package at.ac.wuwien.causalminer.neo4jdb.services;

public class PartialModelCheckerQuery extends AbstractModelCheckerQuery<PartialModelCheckerQuery> {

    private final String returnMatchingModelIds = "MATCH p=(%s)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
                                                  "RETURN distinct p";
    private final String returnAllNotMatchingModelIds = "MATCH (allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                                                        "WHERE NOT(allStart.modelId IN matchingModelIds) " +
                                                        "RETURN distinct allStart.modelId AS notMatchingModels";
    private final String returnAllNotMatchingPaths = "MATCH p=(allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                                                     "WHERE NOT(allStart.modelId IN matchingModelIds) " +
                                                     "RETURN p";
    private final String returnAllNotMatchingModelStartActivities =
                    "MATCH (allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                    "WHERE NOT(allStart.modelId IN matchingModelIds) " +
                    "MATCH p=(allStart)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
                    "RETURN distinct p";


    public PartialModelCheckerQuery() {
        super();
    }

    public PartialModelCheckerQuery(String matchNodeName) {
        super(matchNodeName);
    }


    public PartialModelCheckerQuery addReturnAllNotMatchingModelIds() {
        super.queryString.append(returnAllNotMatchingModelIds);
        return this;
    }

    public PartialModelCheckerQuery addReturnAllNotMatchingModelStartActivities() {
        super.queryString.append(returnAllNotMatchingModelStartActivities);
        return this;
    }

    public PartialModelCheckerQuery addReturnAllNotMatchingPaths() {
        super.queryString.append(returnAllNotMatchingPaths);
        return this;
    }

    public PartialModelCheckerQuery addReturnAllMatchingModelIds() {
        String str = String.format(returnMatchingModelIds, getMatchNodeName());
        super.queryString.append(str);
        return this;
    }

}
