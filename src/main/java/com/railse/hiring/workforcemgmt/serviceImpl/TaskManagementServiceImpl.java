package com.railse.hiring.workforcemgmt.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.common.model.enums.Priority;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.InMemoryTaskRepository;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {

	private final TaskRepository taskRepository;
	private final ITaskManagementMapper taskMapper;

	public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
		this.taskRepository = taskRepository;
		this.taskMapper = taskMapper;
	}

	@Override
	public List<TaskManagementDto> getAllTask() {
		List<TaskManagement> tasks = taskRepository.findAll();
		return taskMapper.modelListToDtoList(tasks);
	}

	@Override
	public TaskManagementDto findTaskById(Long id) {
		TaskManagement task = taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
		return taskMapper.modelToDto(task);
	}

	@Override
	public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
		List<TaskManagement> createdTasks = new ArrayList<>();
		for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
			TaskManagement newTask = new TaskManagement();
			newTask.setReferenceId(item.getReferenceId());
			newTask.setReferenceType(item.getReferenceType());
			newTask.setTask(item.getTask());
			newTask.setAssigneeId(item.getAssigneeId());
			newTask.setPriority(item.getPriority());
			newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
			newTask.setStatus(TaskStatus.ASSIGNED);
			newTask.setDescription("New task created.");
			createdTasks.add(taskRepository.save(newTask));
		}
		return taskMapper.modelListToDtoList(createdTasks);
	}

	@Override
	public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
		List<TaskManagement> updatedTasks = new ArrayList<>();
		for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
			TaskManagement task = taskRepository.findById(item.getTaskId())
					.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));

			if (item.getTaskStatus() != null) {
				task.setStatus(item.getTaskStatus());
			}
			if (item.getDescription() != null) {
				task.setDescription(item.getDescription());
			}
			updatedTasks.add(taskRepository.save(task));
		}
		return taskMapper.modelListToDtoList(updatedTasks);
	}

	@Override
	public String assignByReference(AssignByReferenceRequest request) {
		List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
		List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(),
				request.getReferenceType());
		for (Task taskType : applicableTasks) {
			List<TaskManagement> tasksOfType = existingTasks.stream()
					.filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
					.collect(Collectors.toList());
//			Before
//			if (!tasksOfType.isEmpty()) {
//				for (TaskManagement taskToUpdate : tasksOfType) {
//					taskToUpdate.setAssigneeId(request.getAssigneeId());
//					taskRepository.save(taskToUpdate);
//				}
//			} else {
//				TaskManagement newTask = new TaskManagement();
//				newTask.setReferenceId(request.getReferenceId());
//				newTask.setReferenceType(request.getReferenceType());
//				newTask.setTask(taskType);
//				newTask.setAssigneeId(request.getAssigneeId());
//				newTask.setStatus(TaskStatus.ASSIGNED);
//				taskRepository.save(newTask);
//			}
// 			After
			if (!tasksOfType.isEmpty()) {
				for (TaskManagement taskToUpdate : tasksOfType) {
					taskToUpdate.setStatus(TaskStatus.CANCELLED);
					taskRepository.save(taskToUpdate);
				}
			}
			TaskManagement newTask = new TaskManagement();
			newTask.setReferenceId(request.getReferenceId());
			newTask.setReferenceType(request.getReferenceType());
			newTask.setTask(taskType);
			newTask.setAssigneeId(request.getAssigneeId());
			newTask.setStatus(TaskStatus.ASSIGNED);
			taskRepository.save(newTask);
		}
		return "Tasks assigned successfully for reference " + request.getReferenceId();
	}

	@Override
	public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
		List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());
		// Before
//		List<TaskManagement> filteredTasks = tasks.stream().filter(task -> {
//			return true;
//		}).collect(Collectors.toList());
		// After
		Long start = request.getStartDate();
		Long end = request.getEndDate();
		List<TaskManagement> filteredTasks = tasks.stream().filter(task -> task.getStatus() != TaskStatus.CANCELLED)
				.filter(task -> {
					Long deadline = task.getTaskDeadlineTime();
					return (deadline >= start && deadline <= end)
							|| (deadline < start && task.getStatus() != TaskStatus.COMPLETED);
				}).collect(Collectors.toList());
		return taskMapper.modelListToDtoList(filteredTasks);
	}

	@Override
	public TaskManagementDto updateTaskPriority(Long taskId, Priority priority) {
		TaskManagement task = taskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
		task.setPriority(priority);
		taskRepository.save(task);
		((InMemoryTaskRepository) taskRepository).addActivity(taskId, "Priority changed to: " + priority);
		return taskMapper.modelToDto(task);
	}

	@Override
	public List<TaskManagementDto> getTasksByPriority(Priority priority) {
		return taskRepository.findAll().stream()
				.filter(task -> task.getPriority() == priority && task.getStatus() != TaskStatus.CANCELLED)
				.map(taskMapper::modelToDto).collect(Collectors.toList());
	}
	
	@Override
	public void addCommentToTask(Long taskId, CommentRequest request) {
	    TaskManagement task = taskRepository.findById(taskId)
	        .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

	    CommentDto comment = new CommentDto();
	    comment.setTaskId(taskId);
	    comment.setAuthor(request.getAuthor());
	    comment.setContent(request.getContent());
	    comment.setTimestamp(System.currentTimeMillis());

	    ((InMemoryTaskRepository) taskRepository).addComment(taskId, comment);
	    ((InMemoryTaskRepository) taskRepository).addActivity(taskId, "Comment added by: " + request.getAuthor());
	}

	@Override
	public TaskFullViewDto getTaskDetailsWithHistory(Long taskId) {
	    TaskManagementDto task = findTaskById(taskId);
	    List<CommentDto> comments = ((InMemoryTaskRepository) taskRepository).getComments(taskId);
	    List<ActivityLogDto> logs = ((InMemoryTaskRepository) taskRepository).getActivityLogs(taskId);

	    TaskFullViewDto dto = new TaskFullViewDto();
	    dto.setTask(task);
	    dto.setComments(comments);
	    dto.setActivityLogs(logs);
	    return dto;
	}
}
