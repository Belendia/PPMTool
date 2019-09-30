package com.belendia.ppmtool.web;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.belendia.ppmtool.domain.ProjectTask;
import com.belendia.ppmtool.services.MapValidationErrorService;
import com.belendia.ppmtool.services.ProjectTaskService;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {
	
	@Autowired
	private ProjectTaskService projectTaskService;
	
	@Autowired
	private MapValidationErrorService mapValidationErrorService;
	
	
	@PostMapping("/{backlog_id}")
	public ResponseEntity<?> addProjectTaskToBacklog(@Valid @RequestBody ProjectTask projectTask, 
			BindingResult result, @PathVariable String backlog_id, Principal principal) {
		
		ResponseEntity<?> errMap = mapValidationErrorService.MapValidationService(result);
		if(errMap != null) return errMap;
		
		ProjectTask projectTaskAdded = projectTaskService.addProjectTask(backlog_id, projectTask, principal.getName());
		
		return new ResponseEntity<ProjectTask>(projectTaskAdded, HttpStatus.CREATED);
	}
	
	@GetMapping("/{backlog_id}")
	public Iterable<ProjectTask> getProjectBacklog(@PathVariable String backlog_id, Principal principal) {
		return projectTaskService.findBacklogById(backlog_id, principal.getName());
	}
	
	@GetMapping("/{backlog_id}/{project_task_id}")
	public ResponseEntity<?> getProjectTask(@PathVariable String backlog_id, @PathVariable String project_task_id, Principal principal) {
		ProjectTask projectTask = projectTaskService.findProjectByProjectSequence(backlog_id, project_task_id, principal.getName());
		return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
		
	}
	
	@PatchMapping("/{backlogId}/{projectTaskId}")
	public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask, BindingResult result, 
											   @PathVariable String backlogId, @PathVariable String projectTaskId, Principal principal) {
		ResponseEntity<?> errMap = mapValidationErrorService.MapValidationService(result);
		if(errMap != null) return errMap;
		
		ProjectTask updatedProjectTask = projectTaskService.updateByProjectSequence(projectTask, backlogId, projectTaskId, principal.getName());
		
		return new ResponseEntity<ProjectTask>(updatedProjectTask, HttpStatus.OK);
	}
	
	@DeleteMapping("/{backlogId}/{projectTaskId}")
	public ResponseEntity<?> deleteProjectTask(@PathVariable String backlogId, @PathVariable String projectTaskId, Principal principal) {
		projectTaskService.deleteProjectTaskByProjectSequence(backlogId, projectTaskId, principal.getName());
		
		return new ResponseEntity<String>("Project Task " + projectTaskId + " was deleted successfully", HttpStatus.OK);
	}
	
}
