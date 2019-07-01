package com.belendia.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.belendia.ppmtool.domain.Backlog;
import com.belendia.ppmtool.domain.ProjectTask;
import com.belendia.ppmtool.repositories.BacklogRepository;
import com.belendia.ppmtool.repositories.ProjectTaskRepository;

@Service
public class ProjectTaskService {
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private ProjectTaskRepository projectTaskRepository;
	
	public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
		// Exceptions: Project not found
		
		//ProjectTasks to be added to a specific project, project != null, if projects are not null, then the backlogs exists
		Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);
		
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
		if(projectTask.getStatus() == "" || projectTask.getStatus() == null) {
			projectTask.setStatus("TO_DO");
		}
		
		
		return projectTaskRepository.save(projectTask);
	}
	
}
