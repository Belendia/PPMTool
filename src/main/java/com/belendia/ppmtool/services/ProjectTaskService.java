package com.belendia.ppmtool.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.belendia.ppmtool.domain.Backlog;
import com.belendia.ppmtool.domain.ProjectTask;
import com.belendia.ppmtool.exceptions.ProjectNotFoundException;
import com.belendia.ppmtool.repositories.BacklogRepository;
import com.belendia.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {
//	@Autowired
//	private BacklogRepository backlogRepository;

	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	@Autowired
	private ProjectService projectService;
	
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
		
		//ProjectTasks to be added to a specific project, project != null, if projects are not null, then the backlogs exists
		Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();//backlogRepository.findByProjectIdentifier(projectIdentifier);
		
		if(backlog == null) {
			throw new ProjectNotFoundException("Project Not Found");
		}
		
		//Set the backlog to the ProjectTask
		projectTask.setBacklog(backlog);
		
		//projectSequence - We want our project sequence to be like: (ProjectID-PTSequence) e.g. TIRE-1, TIRE-2)
		Integer backlogSequence = backlog.getPTSequence();
		// Update the Backlog sequence
		backlogSequence++;
		backlog.setPTSequence(backlogSequence);
		
		//Add sequence to projectTask
		projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
		projectTask.setProjectIdentifier(projectIdentifier);
		
		//INITIAL priority when priority is null
		if(projectTask.getPriority() == null || projectTask.getPriority() == 0) {
			projectTask.setPriority(3); //set the priority to low if it is null.
		}
		
		//INITIAL status when status is null
		if(projectTask.getStatus() == null || projectTask.getStatus() == "") {
			projectTask.setStatus("TO_DO");
		}
		
		
		return projectTaskRepository.save(projectTask);
	}

	public Iterable<ProjectTask> findBacklogById(String backlogId, String username) {
		//if the backlog doesn't exist implies that the project doesn't exist
//		Backlog backlog = backlogRepository.findByProjectIdentifier(backlogId);
//		
//		if(backlog == null) {
//			throw new ProjectNotFoundException("Project with ID: '" + backlogId +  "' does not exist");
//		}
		
		projectService.findProjectByIdentifier(backlogId, username);
		
		return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlogId);
	}
	
	
	public ProjectTask findProjectByProjectSequence(String backlogId, String projectTaskId, String username) {
		// make sure we are searching on the right backlog
//		Backlog backlog = backlogRepository.findByProjectIdentifier(backlogId);
//		
//		if(backlog == null) {
//			throw new ProjectNotFoundException("Project with ID: '" + backlogId +  "' does not exist");
//		}
		
		projectService.findProjectByIdentifier(backlogId, username);
		
		// make sure that our task exists
		
		ProjectTask projectTask = projectTaskRepository.findByProjectSequence(projectTaskId);
		
		if(projectTask == null) {
			throw new ProjectNotFoundException("Project Task with ID: '" + projectTaskId +  "' not found");
		}
		
		// make sure that the backlog/project id in the path corresponds to the right project
		if(!projectTask.getProjectIdentifier().equals(backlogId)) {
			throw new ProjectNotFoundException("Project Task '" + projectTaskId +  "' does not exist in project '" + backlogId + "'");
		}
		
		return projectTask;
	}
	
	// update project task
	public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlogId, String projectTaskId, String username) {
		//find existing project task
		ProjectTask projectTask = findProjectByProjectSequence(backlogId, projectTaskId, username);
		
		// replace it with updated task
		projectTask = updatedTask;
		
		// save update
		return projectTaskRepository.save(projectTask);
	}
	
	public void deleteProjectTaskByProjectSequence(String backlogId, String projectTaskId, String username) {
		ProjectTask projectTask = findProjectByProjectSequence(backlogId, projectTaskId, username);
		
		projectTaskRepository.delete(projectTask);
	}
}
