package com.curatix.vault.dto;

import com.curatix.vault.entity.Result;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateBoardRequest {
    @NotBlank(message = "Board name is required")
    @Size(min = 3, max = 100, message = "Board name must be between 3 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Description exceeds maximum length")
    private String description;

    @NotBlank(message = "Cover color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid HEX color format")
    private String coverColor = "#6366f1";

    @Size(max = 10)
    private String coverEmoji;

    @Size(max = 100)
    private String platform;

    private LocalDate eventDate;

    @Size(max = 255)
    private String venue;

    @Size(max = 255)
    private String theme;

    @Size(max = 255)
    private String teamName;

    private String problemStatement;
    private String projectIdea;
    private Result result;
    private String prize;

    @URL(message = "Invalid submission URL format")
    private String submissionUrl;

    private List<@URL(message = "Invalid repository URL format") String> repoUrls = new ArrayList<>();

    private String notes;

    public CreateBoardRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCoverColor() { return coverColor; }
    public void setCoverColor(String coverColor) { this.coverColor = coverColor; }
    public String getCoverEmoji() { return coverEmoji; }
    public void setCoverEmoji(String coverEmoji) { this.coverEmoji = coverEmoji; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getProblemStatement() { return problemStatement; }
    public void setProblemStatement(String problemStatement) { this.problemStatement = problemStatement; }
    public String getProjectIdea() { return projectIdea; }
    public void setProjectIdea(String projectIdea) { this.projectIdea = projectIdea; }
    public Result getResult() { return result; }
    public void setResult(Result result) { this.result = result; }
    public String getPrize() { return prize; }
    public void setPrize(String prize) { this.prize = prize; }
    public String getSubmissionUrl() { return submissionUrl; }
    public void setSubmissionUrl(String submissionUrl) { this.submissionUrl = submissionUrl; }
    public List<String> getRepoUrls() { return repoUrls; }
    public void setRepoUrls(List<String> repoUrls) { this.repoUrls = repoUrls; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
