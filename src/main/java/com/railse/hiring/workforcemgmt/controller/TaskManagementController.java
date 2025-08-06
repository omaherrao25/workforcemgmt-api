package com.railse.hiring.workforcemgmt.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.railse.hiring.workforcemgmt.common.model.enums.Priority;
import com.railse.hiring.workforcemgmt.common.model.response.Response;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;

@RestController
@RequestMapping("/task-mgmt")
public class TaskManagementController {

	private final TaskManagementService taskManagementService;

	public TaskManagementController(TaskManagementService taskManagementService) {
		this.taskManagementService = taskManagementService;
	}

	@GetMapping("/")
	public Response<List<TaskManagementDto>> getAllTasks() {
		return new Response<>(taskManagementService.getAllTask());
	}

	@GetMapping("/{id}")
	public Response<TaskManagementDto> getTaskById(@PathVariable Long id) {
		return new Response<>(taskManagementService.findTaskById(id));
	}

	@PostMapping("/create")
	public Response<List<TaskManagementDto>> createTasks(@RequestBody TaskCreateRequest request) {
		return new Response<>(taskManagementService.createTasks(request));
	}

	@PostMapping("/update")
	public Response<List<TaskManagementDto>> updateTasks(@RequestBody UpdateTaskRequest request) {
		return new Response<>(taskManagementService.updateTasks(request));
	}

	@PostMapping("/assign-by-ref")
	public Response<String> assignByReference(@RequestBody AssignByReferenceRequest request) {
		return new Response<>(taskManagementService.assignByReference(request));
	}

	@PostMapping("/fetch-by-date/v2")
	public Response<List<TaskManagementDto>> fetchByDate(@RequestBody TaskFetchByDateRequest request) {
		return new Response<>(taskManagementService.fetchTasksByDate(request));
	}

	@PatchMapping("/{taskId}/priority")
	public Response<TaskManagementDto> updatePriority(@PathVariable Long taskId, @RequestParam Priority priority) {
		return new Response<>(taskManagementService.updateTaskPriority(taskId, priority));
	}

	@GetMapping("/priority/{priority}")
	public Response<List<TaskManagementDto>> getByPriority(@PathVariable Priority priority) {
		return new Response<>(taskManagementService.getTasksByPriority(priority));
	}

	@PostMapping("/{taskId}/comment")
	public Response<String> addComment(@PathVariable Long taskId, @RequestBody CommentRequest request) {
	    taskManagementService.addCommentToTask(taskId, request);
	    return new Response<>("Comment added successfully");
	}

	@GetMapping("/{taskId}/details")
	public Response<TaskFullViewDto> getTaskWithDetails(@PathVariable Long taskId) {
	    return new Response<>(taskManagementService.getTaskDetailsWithHistory(taskId));
	}
}
