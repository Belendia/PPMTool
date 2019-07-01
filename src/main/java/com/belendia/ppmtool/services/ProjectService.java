package com.belendia.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.belendia.ppmtool.domain.Backlog;
import com.belendia.ppmtool.domain.Project;
import com.belendia.ppmtool.exceptions.ProjectIdentifierException;
import com.belendia.ppmtool.repositories.BacklogRepository;
import com.belendia.ppmtool.repositories.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	public Project saveOrUpdateProject(Project project) {
		
		try {
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
			if(project.getId() == null) {
				Backlog backlog = new Backlog();
				//set the relationship
				project.setBacklog(backlog);
				backlog.setProject(project);
				backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			}
			
			//While updating, if the client doesn't send the backlog together with the project, the backlog field will be set to null...
			// not on the database but on the hibernate side. To solve this, use the below code
			if(project.getId() != null) {
				project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
			}
			
			return projectRepository.save(project);
		} catch (Exception e) {
			throw new ProjectIdentifierException("Project Identifier '" + project.getProjectIdentifier() + "' already exists");
		}
		
	}
	
	public Project findProjectByIdentifier(String projectIdentifier) {
		Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());
		if( project == null) {
			throw new ProjectIdentifierException("Project Identifier '" + projectIdentifier.toUpperCase() + "' doesn't exists");
		}
		
		return project;
	}
	
	public Iterable<Project> findAllProjects() {
		
		return projectRepository.findAll();
	}

	public void deleteProjectByIdentifier(String projectIdentifier) {
		Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());
		
		if( project == null) {
			throw new ProjectIdentifierException("Cannot delete Project with ID '" + projectIdentifier.toUpperCase() + "'. This project does not exist.");
		}
		
		projectRepository.delete(project);
	}
}
