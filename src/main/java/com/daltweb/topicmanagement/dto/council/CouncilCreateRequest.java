package com.daltweb.topicmanagement.dto.council;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class CouncilCreateRequest {

    @NotBlank(message = "Tên hội đồng không được để trống")
    private String name;

    @NotNull(message = "Đợt đăng ký không được để trống")
    private Long periodId;

    private LocalDate defenseDate;
    private String location;
    private String notes;

    @NotNull(message = "Danh sách thành viên hội đồng không được để trống")
    @Size(min = 3, max = 5, message = "Hội đồng phải bao gồm từ 3 đến 5 giảng viên")
    private List<CouncilMemberInput> members;

    public CouncilCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getPeriodId() { return periodId; }
    public void setPeriodId(Long periodId) { this.periodId = periodId; }
    public LocalDate getDefenseDate() { return defenseDate; }
    public void setDefenseDate(LocalDate defenseDate) { this.defenseDate = defenseDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<CouncilMemberInput> getMembers() { return members; }
    public void setMembers(List<CouncilMemberInput> members) { this.members = members; }
}
