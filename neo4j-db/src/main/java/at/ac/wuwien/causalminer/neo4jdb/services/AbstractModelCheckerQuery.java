package at.ac.wuwien.causalminer.neo4jdb.services;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class AbstractModelCheckerQuery<T extends AbstractModelCheckerQuery<T>> extends QueryBuilder {

    private final String collectMatchingModelIds = "WITH collect(distinct %s) AS matchingModelIds ";


    private String modelIdSearch = "";
    private String matchNodeName = "abc";
    protected int matchingPathsCounter = 0;


    public AbstractModelCheckerQuery() {
    }

    public AbstractModelCheckerQuery(String matchNodeName) {
        this.matchNodeName = matchNodeName;
    }

    public T addCollectMatchingModelIds() {
        String str = String.format(collectMatchingModelIds, getMatchNodeName() + ".modelId");
        super.queryString.append(str);
        return (T) this;
    }

    public T addMatchClauses(List<List<String>> types) {
        types.sort(Comparator.comparingInt(List::size));

        types.forEach(list -> {
            super.queryString.append(super.matchClaus).append(" ");
            createMatch(list, super.queryString);
            super.queryString.append(" ");
            matchingPathsCounter = matchingPathsCounter + 1;
        });
        return (T) this;
    }

    // TODO - Can we simplify the query by skipping nodes that where already checked?
    //	MATCH (A)—>(B)—>(C)—>(D)—>(E)
    //	MATCH (A)—>(B)—>(C)—>(D)—>(F)
    //		=
    //	MATCH (A)—>(B)—>(C)—>(D)—>(E)
    //	MATCH (A)-[*4]->(F)
    private void createMatch(List<String> types, StringBuilder stringBuilder) {

        createStartActivity(stringBuilder);

        int startIndex = 0;
        int endIndex = types.size() - 1;
        if(types.get(0).toLowerCase().equals("start")) {
            stringBuilder.append(defaultRelationship);
            startIndex = startIndex +  1;
        } else {
            stringBuilder.append(defaultUnboundRelationship);
        }
        if(types.get(endIndex).toLowerCase().equals("end")) {
            endIndex = endIndex -  1;
        }

        for (int i = startIndex; i <= endIndex; i++) {
            stringBuilder.append("(:" + defaultNodeLabel + " " + getTypeSearchString(types.get(i)) + ")");
            if(i < endIndex) {
                stringBuilder.append(defaultRelationship);
            }
        }

        if(types.get(types.size() - 1).toLowerCase().equals("end")) {
            stringBuilder.append(defaultRelationship);
        } else {
            stringBuilder.append(defaultUnboundRelationship);
        }
        createEndActivity(stringBuilder);
    }


    private void createStartActivity(StringBuilder stringBuilder) {
        if(StringUtils.isEmpty(modelIdSearch)) {
            stringBuilder.append("(" + getMatchNodeName() + ":" + defaultNodeLabel + ":" + startActivityLabel + ")");
        } else {
            stringBuilder.append("(:" + defaultNodeLabel + ":" + startActivityLabel + " " + getModelIdSearch() + ")");
        }
    }

    private void createEndActivity(StringBuilder stringBuilder) {
        stringBuilder.append("(:" + defaultNodeLabel + ":" + endActivityLabel + " " + getModelIdSearch() + ")");
    }

    private String getTypeSearchString(String type) {
        return "{type: '" + type + "'}";
    }

    private String getModelIdSearch() {
        if(StringUtils.isEmpty(modelIdSearch)) {
            modelIdSearch = "{modelId: " + getMatchNodeName() + ".modelId}";
        }
        return modelIdSearch;
    }

    protected String getMatchNodeName() {
        if(StringUtils.isEmpty(matchNodeName)) {
            Random rnd = new Random();
            matchNodeName = String.valueOf((char) (rnd.nextInt(26) + 'a'));
            matchNodeName = matchNodeName + String.valueOf((char) (rnd.nextInt(26) + 'a'));
            matchNodeName = matchNodeName + String.valueOf((char) (rnd.nextInt(26) + 'a'));
        }
        return matchNodeName;
    }
}
