package com.belendia.ppmtool.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.belendia.ppmtool.domain.ProjectTask;

public interface ProjectTaskRepository extends CrudRepository<ProjectTask, Long> {
	List<ProjectTask> findByProjectIdentifierOrderByPriority(String id);
}
