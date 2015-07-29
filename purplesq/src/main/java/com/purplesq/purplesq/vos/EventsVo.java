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
    private boolean soldout;
    private int consumed;
    private String id;
    private String thumbnail;
    private String eligibility;
    private String requirements;
    private String instructions;
    private String learnings;
    private String summary;
    private List<String> media;


    private List<EventSocialProfileVo> social_profiles;
    private List<EventAreaOfStudyVo> area_of_study;
    private List<EventFacilitiesVo> facilities;
    private List<EventItinerariesVo> itineraries;
    private List<EventHighlightsVo> highlights;
    private List<EventFaqsVo> faqs;
    private List<EventDropoffsVo> dropoffs;
    private List<EventPickupsVo> pickups;
    private EventLocationVo location;
    private EventScheduleVo schedule;
    private EventCostVo cost;

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

    public List<EventSocialProfileVo> getSocial_profiles() {
        return social_profiles;
    }

    public void setSocial_profiles(List<EventSocialProfileVo> social_profiles) {
        this.social_profiles = social_profiles;
    }

    public List<EventAreaOfStudyVo> getArea_of_study() {
        return area_of_study;
    }

    public void setArea_of_study(List<EventAreaOfStudyVo> area_of_study) {
        this.area_of_study = area_of_study;
    }

    public List<EventFacilitiesVo> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<EventFacilitiesVo> facilities) {
        this.facilities = facilities;
    }

    public List<EventItinerariesVo> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List<EventItinerariesVo> itineraries) {
        this.itineraries = itineraries;
    }

    public List<EventHighlightsVo> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<EventHighlightsVo> highlights) {
        this.highlights = highlights;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public List<EventFaqsVo> getFaqs() {
        return faqs;
    }

    public void setFaqs(List<EventFaqsVo> faqs) {
        this.faqs = faqs;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getLearnings() {
        return learnings;
    }

    public void setLearnings(String learnings) {
        this.learnings = learnings;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<EventDropoffsVo> getDropoffs() {
        return dropoffs;
    }

    public void setDropoffs(List<EventDropoffsVo> dropoffs) {
        this.dropoffs = dropoffs;
    }

    public List<EventPickupsVo> getPickups() {
        return pickups;
    }

    public void setPickups(List<EventPickupsVo> pickups) {
        this.pickups = pickups;
    }

    public EventLocationVo getLocation() {
        return location;
    }

    public void setLocation(EventLocationVo location) {
        this.location = location;
    }

    public EventScheduleVo getSchedule() {
        return schedule;
    }

    public void setSchedule(EventScheduleVo schedule) {
        this.schedule = schedule;
    }

    public EventCostVo getCost() {
        return cost;
    }

    public void setCost(EventCostVo cost) {
        this.cost = cost;
    }

    public boolean isSoldout() {
        return soldout;
    }

    public void setSoldout(boolean soldout) {
        this.soldout = soldout;
    }

    public int getConsumed() {
        return consumed;
    }

    public void setConsumed(int consumed) {
        this.consumed = consumed;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
