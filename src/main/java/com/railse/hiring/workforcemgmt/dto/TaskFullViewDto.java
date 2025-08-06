package com.railse.hiring.workforcemgmt.dto;

import java.util.List;

import lombok.Data;

@Data
public class TaskFullViewDto {

	private TaskManagementDto task;
	private List<CommentDto> comments;
	private List<ActivityLogDto> activityLogs;
}
