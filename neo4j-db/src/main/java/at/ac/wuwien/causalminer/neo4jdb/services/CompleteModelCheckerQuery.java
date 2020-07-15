package at.ac.wuwien.causalminer.neo4jdb.services;

public class CompleteModelCheckerQuery extends AbstractModelCheckerQuery<CompleteModelCheckerQuery> {

    private final String returnMatchingModelIds =
            "MATCH tempPath=(allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
            "WHERE allStart.modelId IN matchingModelIds " +
            "WITH allStart, count(tempPath) AS pathAmount " +
            "WHERE pathAmount = %s " +
            "MATCH paths=(allStart)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
            "RETURN distinct paths";


    private final String returnAllNotMatchingModelStartActivities =
                            "MATCH tempPath=(allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                            "WHERE allStart.modelId IN matchingModelIds " +
                            "WITH allStart, count(tempPath) AS pathAmount " +
                            "WHERE pathAmount = %s " +
                            "WITH collect(distinct allStart.modelId) AS totalMatchingModelIds " +
                            "MATCH (allStart2:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart2.modelId}) " +
                            "WHERE NOT(allStart2.modelId IN totalMatchingModelIds) " +
                            "MATCH tempPath2=(allStart2)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
                            "RETURN distinct tempPath2";

    public CompleteModelCheckerQuery() {
        super();
    }

    public CompleteModelCheckerQuery(String matchNodeName) {
        super(matchNodeName);
    }


    public CompleteModelCheckerQuery addReturnAllNotMatchingModelStartActivities() {
        String str = String.format(returnAllNotMatchingModelStartActivities, matchingPathsCounter);
        super.queryString.append(str);
        return this;
    }

    public CompleteModelCheckerQuery addReturnAllMatchingModelIds() {
        String str = String.format(returnMatchingModelIds, matchingPathsCounter);
        super.queryString.append(str);
        return this;
    }

}
