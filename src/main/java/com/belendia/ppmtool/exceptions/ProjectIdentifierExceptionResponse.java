package com.belendia.ppmtool.exceptions;

public class ProjectIdentifierExceptionResponse {

	private String projectIdentifier;
	
	public ProjectIdentifierExceptionResponse(String projectIdentifier) {
		this.setProjectIdentifier(projectIdentifier);
	}

	public String getProjectIdentifier() {
		return projectIdentifier;
	}

	public void setProjectIdentifier(String projectIdentifier) {
		this.projectIdentifier = projectIdentifier;
	}

}
