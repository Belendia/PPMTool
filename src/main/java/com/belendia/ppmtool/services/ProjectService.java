package com.belendia.ppmtool.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.belendia.ppmtool.domain.Project;
import com.belendia.ppmtool.exceptions.ProjectIdentifierException;
import com.belendia.ppmtool.repositories.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	public Project saveOrUpdateProject(Project project) {
		
		try {
			project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
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
