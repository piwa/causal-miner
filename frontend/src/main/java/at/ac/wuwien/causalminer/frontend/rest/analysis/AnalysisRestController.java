package at.ac.wuwien.causalminer.frontend.rest.analysis;

import at.ac.wuwien.causalminer.neo4jdb.domain.query.results.InstancesWithSameProcessModelResult;
import at.ac.wuwien.causalminer.neo4jdb.services.ProcessInstanceServices;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/analysis")
public class AnalysisRestController {

    @Autowired
    private ProcessInstanceServices processInstanceServices;


    @GetMapping(value = "drift")
    public String getGlobalProcessModel() {
        List<InstancesWithSameProcessModelResult> neo4jResult = processInstanceServices.findAllInstancesWithSameProcessModel();

        Map<Interval, List<InstancesWithSameProcessModelResult.SimilarInstanceModels>> intervalMap = new HashMap<>();

        neo4jResult.forEach(entry -> {

            if (entry.getSimilarInstanceModels().size() == 1) {
                InstancesWithSameProcessModelResult.SimilarInstanceModels instanceModels = entry.getSimilarInstanceModels().get(0);
                Interval interval = new Interval(instanceModels.getTimestamp(), instanceModels.getTimestamp());
                intervalMap.put(interval, entry.getSimilarInstanceModels());
            } else {
                List<InstancesWithSameProcessModelResult.SimilarInstanceModels> list = entry.getSimilarInstanceModels();
                list.sort(Comparator.comparing(InstancesWithSameProcessModelResult.SimilarInstanceModels::getTimestamp));
                Interval interval = new Interval(list.get(0).getTimestamp(), list.get(list.size()-1).getTimestamp());
                intervalMap.put(interval, entry.getSimilarInstanceModels());
            }
        });

        intervalMap.forEach((k,v) -> {
            String str = v.stream().map(InstancesWithSameProcessModelResult.SimilarInstanceModels::getInstanceId).collect(Collectors.joining(", "));
            log.info(k.toString() + ": " + str);
        });

        List<List<InstancesWithSameProcessModelResult.SimilarInstanceModels>> alreadyChecked = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        intervalMap.forEach((k1,v1) -> {

            intervalMap.forEach((k2, v2) -> {

                if(k1 != k2 && k1.overlaps(k2) && !alreadyChecked.contains(v2)) {
                    stringBuilder.append("\n").append("Overlapping:").append("\n");
                    String str1 = v1.stream().map(InstancesWithSameProcessModelResult.SimilarInstanceModels::getInstanceId).collect(Collectors.joining(", "));
                    stringBuilder.append("  " + k1.toString() + ": " + str1).append("\n");
                    String str2 = v2.stream().map(InstancesWithSameProcessModelResult.SimilarInstanceModels::getInstanceId).collect(Collectors.joining(", "));
                    stringBuilder.append("  " + k2.toString() + ": " + str2).append("\n");
                }
            });
            alreadyChecked.add(v1);
        });
        log.warn(stringBuilder.toString());

        return stringBuilder.toString();
    }


}
