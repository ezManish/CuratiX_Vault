package com.curatix.vault.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boards")
@EntityListeners(AuditingEntityListener.class)
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_color", length = 7)
    private String coverColor;

    @Column(name = "cover_emoji", length = 10)
    private String coverEmoji;

    @Column(length = 100)
    private String platform;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(length = 255)
    private String venue;

    @Column(length = 255)
    private String theme;

    @Column(name = "team_name", length = 255)
    private String teamName;

    @Column(name = "problem_statement", columnDefinition = "TEXT")
    private String problemStatement;

    @Column(name = "project_idea", columnDefinition = "TEXT")
    private String projectIdea;

    @Enumerated(EnumType.STRING)
    private Result result = Result.PARTICIPATED;

    @Column(length = 255)
    private String prize;

    @Column(name = "submission_url", length = 500)
    private String submissionUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "board_repo_urls",
            joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "url", length = 500)
    private List<String> repoUrls = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BoardEntity() {}

    public static BoardEntityBuilder builder() {
        return new BoardEntityBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public UserEntity getOwner() { return owner; }
    public void setOwner(UserEntity owner) { this.owner = owner; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class BoardEntityBuilder {
        private BoardEntity instance = new BoardEntity();
        public BoardEntityBuilder id(Long id) { instance.setId(id); return this; }
        public BoardEntityBuilder name(String name) { instance.setName(name); return this; }
        public BoardEntityBuilder description(String description) { instance.setDescription(description); return this; }
        public BoardEntityBuilder coverColor(String coverColor) { instance.setCoverColor(coverColor); return this; }
        public BoardEntityBuilder coverEmoji(String coverEmoji) { instance.setCoverEmoji(coverEmoji); return this; }
        public BoardEntityBuilder platform(String platform) { instance.setPlatform(platform); return this; }
        public BoardEntityBuilder eventDate(LocalDate eventDate) { instance.setEventDate(eventDate); return this; }
        public BoardEntityBuilder venue(String venue) { instance.setVenue(venue); return this; }
        public BoardEntityBuilder theme(String theme) { instance.setTheme(theme); return this; }
        public BoardEntityBuilder teamName(String teamName) { instance.setTeamName(teamName); return this; }
        public BoardEntityBuilder problemStatement(String problemStatement) { instance.setProblemStatement(problemStatement); return this; }
        public BoardEntityBuilder projectIdea(String projectIdea) { instance.setProjectIdea(projectIdea); return this; }
        public BoardEntityBuilder result(Result result) { instance.setResult(result); return this; }
        public BoardEntityBuilder prize(String prize) { instance.setPrize(prize); return this; }
        public BoardEntityBuilder submissionUrl(String submissionUrl) { instance.setSubmissionUrl(submissionUrl); return this; }
        public BoardEntityBuilder repoUrls(List<String> repoUrls) { instance.setRepoUrls(repoUrls); return this; }
        public BoardEntityBuilder notes(String notes) { instance.setNotes(notes); return this; }
        public BoardEntityBuilder owner(UserEntity owner) { instance.setOwner(owner); return this; }
        public BoardEntityBuilder deleted(boolean deleted) { instance.setDeleted(deleted); return this; }
        public BoardEntity build() { return instance; }
    }
}
