package com.belendia.ppmtool.repositories;

import org.springframework.data.repository.CrudRepository;

import com.belendia.ppmtool.domain.ProjectTask;

public interface ProjectTaskRepository extends CrudRepository<ProjectTask, Long> {

}
