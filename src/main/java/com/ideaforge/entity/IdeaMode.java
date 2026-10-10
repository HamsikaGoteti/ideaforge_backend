package com.ideaforge.entity;

public enum IdeaMode {
    HACKATHON("Hackathon", "Fast-paced prototype, MVP differentiation, team tasks & demo pitch"),
    COLLEGE_PROJECT("College Project", "Academic rigor, SRS, system architecture, milestones & testing"),
    STARTUP("Startup", "Value proposition, business model, go-to-market, costs & validation metrics"),
    VALIDATE_IDEA("Validate Idea", "Assumptions, risk analysis, user research questions & evidence tests"),
    OPEN_INNOVATION("Open Innovation", "Problem definition, multi-angle trade-offs, impact & ecosystem collaboration");

    private final String displayName;
    private final String summary;

    IdeaMode(String displayName, String summary) {
        this.displayName = displayName;
        this.summary = summary;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSummary() {
        return summary;
    }
}
