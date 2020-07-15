package at.ac.wuwien.causalminer.neo4jdb.services;

public abstract class QueryBuilder {

    protected final String matchClaus = "MATCH";
    protected final String startActivityLabel = "ModelStartActivity";
    protected final String endActivityLabel = "ModelEndActivity";
    protected final String defaultNodeLabel = "Individual";
    protected final String defaultRelationship = "-->";
    protected final String defaultUnboundRelationship = "-[*]->";

    protected StringBuilder queryString = new StringBuilder();

    public String build() {
        return queryString.toString();
    }


}
