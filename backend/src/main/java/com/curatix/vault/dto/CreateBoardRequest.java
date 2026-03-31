package com.curatix.vault.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateBoardRequest {
    @NotBlank(message = "Board name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @Size(max = 7)
    private String coverColor = "#6366f1";

    @Size(max = 10)
    private String coverEmoji;

    private String platform;
    private LocalDate eventDate;
    private String venue;
    private String theme;
    private String teamName;
    private String problemStatement;
    private String projectIdea;
    private com.curatix.vault.entity.BoardEntity.Result result;
    private String prize;
    private String submissionUrl;
    private String repoUrl;
    private String notes;
}
