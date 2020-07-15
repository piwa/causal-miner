package at.ac.wuwien.causalminer.frontend.model.jsgantt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GanttEntry {

    @JsonProperty("pID")
    private Long id;

    @JsonProperty("pName")
    private String name;

    @JsonProperty("pStart")
    private String startDate;

    @JsonProperty("pEnd")
    private String endDate;

    @JsonProperty("pPlanStart")
    private String planStartDate;

    @JsonProperty("pPlanEnd")
    private String planEndDate;

    @JsonProperty("pClass")
    private String cssClass;

//    @JsonProperty("pLink")
//    private String moreInformationHttpLink;

//    @JsonProperty("pMile")
//    private Boolean isMilestoneTask;

//    @JsonProperty("pRes")
//    private String resourceName;

    @JsonProperty("pComp")
    private double completionPercentage;

//    @JsonProperty("pGroup")
//    private Boolean isAGroupTask;

    @JsonProperty("pParent")
    private Long parentId;

    @JsonProperty("pOpen")
    private Integer standardGroupTaskIsOpen;

    @JsonProperty("pDepend")
    private String dependsOnIds;

//    @JsonProperty("pCaption")
//    private String caption;

//    @JsonProperty("pCost")
//    private String cost;

//    @JsonProperty("pNotes")
//    private String notes;

//    @JsonProperty("pBarText")
//    private String taskBarText;

}
