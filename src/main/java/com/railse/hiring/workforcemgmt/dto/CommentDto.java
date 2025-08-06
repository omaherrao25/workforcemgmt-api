package com.railse.hiring.workforcemgmt.dto;

import lombok.Data;

@Data
public class CommentDto {

	private Long taskId;
	private String author;
	private String content;
	private Long timestamp;
}
