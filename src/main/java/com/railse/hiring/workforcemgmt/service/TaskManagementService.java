package com.railse.hiring.workforcemgmt.service;

import java.util.List;

import com.railse.hiring.workforcemgmt.common.model.enums.Priority;
import com.railse.hiring.workforcemgmt.dto.*;

public interface TaskManagementService {

	List<TaskManagementDto> createTasks(TaskCreateRequest request);

	List<TaskManagementDto> updateTasks(UpdateTaskRequest request);

	String assignByReference(AssignByReferenceRequest request);

	List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);

	TaskManagementDto findTaskById(Long id);

	List<TaskManagementDto> getAllTask();

	TaskManagementDto updateTaskPriority(Long taskId, Priority priority);

	List<TaskManagementDto> getTasksByPriority(Priority priority);

	void addCommentToTask(Long taskId, CommentRequest request);

	TaskFullViewDto getTaskDetailsWithHistory(Long taskId);
}
