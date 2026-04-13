package com.curatix.vault.controller;

import com.curatix.vault.entity.MemberProfileEntity;
import com.curatix.vault.security.FirebasePrincipal;
import com.curatix.vault.service.MemberProfileService;
import com.curatix.vault.service.PermissionService;
import com.curatix.vault.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/export")
@RequiredArgsConstructor
@Tag(name = "Data Export", description = "Endpoints for exporting board data in various formats like CSV")
public class CsvExportController {

        private final MemberProfileService memberProfileService;
        private final PermissionService permissionService;
        private final UserService userService;

    @Operation(summary = "Export Members to CSV", description = "Generates a CSV file containing all member profiles for a specific board. Useful for hackathon submissions.")
    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
                        @AuthenticationPrincipal FirebasePrincipal principal,
                        @PathVariable Long boardId) throws IOException {

                // Access check (any member can export)
                var user = userService.getByFirebaseUid(principal.getUid());
                permissionService.getRole(boardId, user.getId());

                List<MemberProfileEntity> profiles = memberProfileService.getProfiles(principal.getUid(), boardId);

                StringWriter sw = new StringWriter();
                CSVFormat format = CSVFormat.DEFAULT.builder()
                                .setHeader(
                                                "Full Name", "Admission No", "Enrollment No",
                                                "Email", "Phone", "GitHub", "LinkedIn",
                                                "Repo URL", "Role in Team", "Year/Branch",
                                                "Skills", "Bio")
                                .build();

                try (CSVPrinter printer = new CSVPrinter(sw, format)) {
                        for (MemberProfileEntity p : profiles) {
                                printer.printRecord(
                                                p.getFullName(),
                                                p.getAdmissionNo(),
                                                p.getEnrollmentNo(),
                                                p.getEmail(),
                                                p.getPhone(),
                                                p.getGithubUrl(),
                                                p.getLinkedinUrl(),
                                                p.getRepoUrl(),
                                                p.getRoleInTeam(),
                                                p.getYearBranch(),
                                                p.getSkills(),
                                                p.getBio());
                        }
                }

                byte[] csvBytes = sw.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"board-" + boardId + "-members.csv\"")
                                .contentType(MediaType.parseMediaType("text/csv"))
                                .body(csvBytes);
        }
}
