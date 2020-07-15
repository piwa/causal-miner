package at.ac.wuwien.causalminer.neo4jdb.services;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {

    @Test
    void PartialModelChecker_returnMatchingModelIds() {

        String correctResult =
                "MATCH (n:Individual:ModelStartActivity)-[*]->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-[*]->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                "MATCH (:Individual:ModelStartActivity {modelId: n.modelId})-[*]->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual {type: 'C'})-->(:Individual {type: 'D'})-[*]->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                "WITH collect(distinct n.modelId) AS matchingModelIds " +
                "MATCH p=(n)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
                "RETURN distinct p";

        PartialModelCheckerQuery query = new PartialModelCheckerQuery("n");

        List<String> list1 = new ArrayList<>(List.of("A", "B", "C", "D"));
        List<String> list2 = new ArrayList<>(List.of("A", "B"));
        List<List<String>> combinedList = new ArrayList<>(List.of(list1, list2));

        query = query.addMatchClauses(combinedList).addCollectMatchingModelIds().addReturnAllMatchingModelIds();

        assertEquals(correctResult, query.build());

    }

    @Test
    void PartialModelChecker_returnAllNotMatchingModelIds() {

        String correctResult =
                "MATCH (n:Individual:ModelStartActivity)-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "MATCH (:Individual:ModelStartActivity {modelId: n.modelId})-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual {type: 'C'})-->(:Individual {type: 'D'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "WITH collect(distinct n.modelId) AS matchingModelIds " +
                        "MATCH (allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                        "WHERE NOT(allStart.modelId IN matchingModelIds) " +
                        "RETURN distinct allStart.modelId AS notMatchingModels";

        PartialModelCheckerQuery query = new PartialModelCheckerQuery("n");

        List<String> list1 = new ArrayList<>(List.of("Start", "A", "B", "C", "D", "End"));
        List<String> list2 = new ArrayList<>(List.of("Start", "A", "B", "End"));
        List<List<String>> combinedList = new ArrayList<>(List.of(list1, list2));

        query = query.addMatchClauses(combinedList).addCollectMatchingModelIds().addReturnAllNotMatchingModelIds();

        assertEquals(correctResult, query.build());
    }

    @Test
    void PartialModelChecker_returnAllNotMatchingPaths() {

        String correctResult =
                "MATCH (n:Individual:ModelStartActivity)-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "MATCH (:Individual:ModelStartActivity {modelId: n.modelId})-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual {type: 'C'})-->(:Individual {type: 'D'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "WITH collect(distinct n.modelId) AS matchingModelIds " +
                        "MATCH p=(allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                        "WHERE NOT(allStart.modelId IN matchingModelIds) " +
                        "RETURN p";

        PartialModelCheckerQuery query = new PartialModelCheckerQuery("n");

        List<String> list1 = new ArrayList<>(List.of("Start", "A", "B", "C", "D", "End"));
        List<String> list2 = new ArrayList<>(List.of("Start", "A", "B", "End"));
        List<List<String>> combinedList = new ArrayList<>(List.of(list1, list2));

        query = query.addMatchClauses(combinedList).addCollectMatchingModelIds().addReturnAllNotMatchingPaths();

        assertEquals(correctResult, query.build());
    }

    @Test
    void PartialModelChecker_returnAllNotMatchingModelStartActivities() {

        String correctResult =
                "MATCH (n:Individual:ModelStartActivity)-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "MATCH (:Individual:ModelStartActivity {modelId: n.modelId})-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual {type: 'C'})-->(:Individual {type: 'D'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "WITH collect(distinct n.modelId) AS matchingModelIds " +
                        "MATCH (allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                        "WHERE NOT(allStart.modelId IN matchingModelIds) " +
                        "MATCH p=(allStart)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
                        "RETURN distinct p";

        PartialModelCheckerQuery query = new PartialModelCheckerQuery("n");

        List<String> list1 = new ArrayList<>(List.of("Start", "A", "B", "C", "D", "End"));
        List<String> list2 = new ArrayList<>(List.of("Start", "A", "B", "End"));
        List<List<String>> combinedList = new ArrayList<>(List.of(list1, list2));

        query = query.addMatchClauses(combinedList).addCollectMatchingModelIds().addReturnAllNotMatchingModelStartActivities();

        assertEquals(correctResult, query.build());
    }

    // --- Complete Model Checker

    @Test
    void CompleteModelChecker_returnMatchingModelIds() {

        String correctResult =
                "MATCH (n:Individual:ModelStartActivity)-[*]->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-[*]->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "MATCH (:Individual:ModelStartActivity {modelId: n.modelId})-[*]->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual {type: 'C'})-->(:Individual {type: 'D'})-[*]->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "WITH collect(distinct n.modelId) AS matchingModelIds " +
                        "MATCH tempPath=(allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                        "WHERE allStart.modelId IN matchingModelIds " +
                        "WITH allStart, count(tempPath) AS pathAmount " +
                        "WHERE pathAmount = 2 " +
                        "MATCH paths=(allStart)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
                        "RETURN distinct paths";

        CompleteModelCheckerQuery query = new CompleteModelCheckerQuery("n");

        List<String> list1 = new ArrayList<>(List.of("A", "B", "C", "D"));
        List<String> list2 = new ArrayList<>(List.of("A", "B"));
        List<List<String>> combinedList = new ArrayList<>(List.of(list1, list2));

        query = query.addMatchClauses(combinedList).addCollectMatchingModelIds().addReturnAllMatchingModelIds();

        assertEquals(correctResult, query.build());

    }


    @Test
    void CompleteModelChecker_returnAllNotMatchingModelStartActivities() {

        String correctResult =
                "MATCH (n:Individual:ModelStartActivity)-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "MATCH (:Individual:ModelStartActivity {modelId: n.modelId})-->(:Individual {type: 'A'})-->(:Individual {type: 'B'})-->(:Individual {type: 'C'})-->(:Individual {type: 'D'})-->(:Individual:ModelEndActivity {modelId: n.modelId}) " +
                        "WITH collect(distinct n.modelId) AS matchingModelIds " +
                        "MATCH tempPath=(allStart:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart.modelId}) " +
                        "WHERE allStart.modelId IN matchingModelIds " +
                        "WITH allStart, count(tempPath) AS pathAmount " +
                        "WHERE pathAmount = 2 " +
                        "WITH collect(distinct allStart.modelId) AS totalMatchingModelIds " +
                        "MATCH (allStart2:Individual:ModelStartActivity)-[*]->(allEnd:Individual:ModelEndActivity {modelId: allStart2.modelId}) " +
                        "WHERE NOT(allStart2.modelId IN totalMatchingModelIds) " +
                        "MATCH tempPath2=(allStart2)<-[:INSTANCE_OF]-(:InstanceStartActivity) " +
                        "RETURN distinct tempPath2";

        CompleteModelCheckerQuery query = new CompleteModelCheckerQuery("n");

        List<String> list1 = new ArrayList<>(List.of("Start", "A", "B", "C", "D", "End"));
        List<String> list2 = new ArrayList<>(List.of("Start", "A", "B", "End"));
        List<List<String>> combinedList = new ArrayList<>(List.of(list1, list2));

        query = query.addMatchClauses(combinedList).addCollectMatchingModelIds().addReturnAllNotMatchingModelStartActivities();

        assertEquals(correctResult, query.build());
    }
}