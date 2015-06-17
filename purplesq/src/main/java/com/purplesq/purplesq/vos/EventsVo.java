
package com.purplesq.purplesq.vos;

import java.util.List;

/**
 * Created by nishant on 01/06/15.
 */
public class EventsVo {
    private String _id;
    private String name;
    private String code;
    private int num_days;
    private String status;
    private int batch_size;
    private String id;

    private List<SocialProfileVo> social_profiles;
    private List<AreaOfStudyVo> area_of_study;
    private List<FacilitiesVo> facilities;
    private List<ItinerariesVo> itineraries;
    private List<HighlightsVo> highlights;
    private List<MediaVo> media;
    private List<FaqsVo> faqs;
    private EligibilityVo eligibility;
    private RequirementsVo requirements;
    private InstructionsVo instructions;
    private LearningsVo learnings;
    private SummaryVo summary;
    private List<DropoffsVo> dropoffs;
    private List<PickupsVo> pickups;
    private EventLocationVo location;
    private ScheduleVo schedule;
    private CostVo cost;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNum_days() {
        return num_days;
    }

    public void setNum_days(int num_days) {
        this.num_days = num_days;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBatch_size() {
        return batch_size;
    }

    public void setBatch_size(int batch_size) {
        this.batch_size = batch_size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SocialProfileVo> getSocial_profiles() {
        return social_profiles;
    }

    public void setSocial_profiles(List<SocialProfileVo> social_profiles) {
        this.social_profiles = social_profiles;
    }

    public List<AreaOfStudyVo> getArea_of_study() {
        return area_of_study;
    }

    public void setArea_of_study(List<AreaOfStudyVo> area_of_study) {
        this.area_of_study = area_of_study;
    }

    public List<FacilitiesVo> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<FacilitiesVo> facilities) {
        this.facilities = facilities;
    }

    public List<ItinerariesVo> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List<ItinerariesVo> itineraries) {
        this.itineraries = itineraries;
    }

    public List<HighlightsVo> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<HighlightsVo> highlights) {
        this.highlights = highlights;
    }

    public List<MediaVo> getMedia() {
        return media;
    }

    public void setMedia(List<MediaVo> media) {
        this.media = media;
    }

    public List<FaqsVo> getFaqs() {
        return faqs;
    }

    public void setFaqs(List<FaqsVo> faqs) {
        this.faqs = faqs;
    }

    public EligibilityVo getEligibility() {
        return eligibility;
    }

    public void setEligibility(EligibilityVo eligibility) {
        this.eligibility = eligibility;
    }

    public RequirementsVo getRequirements() {
        return requirements;
    }

    public void setRequirements(RequirementsVo requirements) {
        this.requirements = requirements;
    }

    public InstructionsVo getInstructions() {
        return instructions;
    }

    public void setInstructions(InstructionsVo instructions) {
        this.instructions = instructions;
    }

    public LearningsVo getLearnings() {
        return learnings;
    }

    public void setLearnings(LearningsVo learnings) {
        this.learnings = learnings;
    }

    public SummaryVo getSummary() {
        return summary;
    }

    public void setSummary(SummaryVo summary) {
        this.summary = summary;
    }

    public List<DropoffsVo> getDropoffs() {
        return dropoffs;
    }

    public void setDropoffs(List<DropoffsVo> dropoffs) {
        this.dropoffs = dropoffs;
    }

    public List<PickupsVo> getPickups() {
        return pickups;
    }

    public void setPickups(List<PickupsVo> pickups) {
        this.pickups = pickups;
    }

    public EventLocationVo getLocation() {
        return location;
    }

    public void setLocation(EventLocationVo location) {
        this.location = location;
    }

    public ScheduleVo getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleVo schedule) {
        this.schedule = schedule;
    }

    public CostVo getCost() {
        return cost;
    }

    public void setCost(CostVo cost) {
        this.cost = cost;
    }
}
