package com.belendia.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.belendia.ppmtool.domain.Backlog;
import com.belendia.ppmtool.domain.Project;
import com.belendia.ppmtool.domain.User;
import com.belendia.ppmtool.exceptions.ProjectIdentifierException;
import com.belendia.ppmtool.exceptions.ProjectNotFoundException;
import com.belendia.ppmtool.repositories.BacklogRepository;
import com.belendia.ppmtool.repositories.ProjectRepository;
import com.belendia.ppmtool.repositories.UserRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private BacklogRepository backlogRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public Project saveOrUpdateProject(Project project, String username) {
		
		// if the user is updating a project then check if he/she owns the project.
		if(project.getId() != null) {
			Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
			
			if(existingProject != null && !existingProject.getProjectLeader().equals(username)) {
				throw new ProjectNotFoundException("Project not found in your account");
			} else if (existingProject == null) {
				throw new ProjectNotFoundException("Project with ID: '" + project.getProjectIdentifier() + "' cannot be updated because it doesn't exist");
			}
		}
		
		try {
			
			User user = userRepository.findByUsername(username);
			project.setUser(user);
			project.setProjectLeader(user.getUsername());
			
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
			
			if(project.getId() == null) { // if we are just creating project, create backlog for it as well.
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
	
	public Project findProjectByIdentifier(String projectIdentifier, String username) {
		Project project = projectRepository.findByProjectIdentifier(projectIdentifier.toUpperCase());
		if( project == null) {
			throw new ProjectIdentifierException("Project Identifier '" + projectIdentifier.toUpperCase() + "' doesn't exists");
		}
		
		if(!project.getProjectLeader().equals(username)) {
			throw new ProjectNotFoundException("Project not found in your account");
		}
		
		return project;
	}
	
	public Iterable<Project> findAllProjects(String username) {
		
		return projectRepository.findAllByProjectLeader(username);
	}

	public void deleteProjectByIdentifier(String projectIdentifier, String username) {
		Project project = this.findProjectByIdentifier(projectIdentifier, username);
		
		projectRepository.delete(project);
	}
}
