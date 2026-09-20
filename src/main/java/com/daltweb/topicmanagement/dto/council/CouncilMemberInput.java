package com.daltweb.topicmanagement.dto.council;

import com.daltweb.topicmanagement.constant.CouncilRole;
import jakarta.validation.constraints.NotNull;

public class CouncilMemberInput {
    @NotNull(message = "ID giảng viên không được để trống")
    private Long lecturerId;

    @NotNull(message = "Vai trò trong hội đồng không được để trống")
    private CouncilRole councilRole;

    public CouncilMemberInput() {}
    public CouncilMemberInput(Long lecturerId, CouncilRole councilRole) {
        this.lecturerId = lecturerId;
        this.councilRole = councilRole;
    }

    public Long getLecturerId() { return lecturerId; }
    public void setLecturerId(Long lecturerId) { this.lecturerId = lecturerId; }
    public CouncilRole getCouncilRole() { return councilRole; }
    public void setCouncilRole(CouncilRole councilRole) { this.councilRole = councilRole; }
}
