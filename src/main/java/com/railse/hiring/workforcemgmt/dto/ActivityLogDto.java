package com.railse.hiring.workforcemgmt.dto;

import lombok.Data;

@Data
public class ActivityLogDto {
	private Long taskId;
	private String message;
	private Long timestamp;
}