package pro.biocontainers.pipelines.jobs.registries;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * ==Overview==
 * <p>
 * This class
 * <p>
 * Created by ypriverol (ypriverol@gmail.com) on 26/07/2018.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.biocontainers.mongodb.config.MongoDBConfiguration;
import pro.biocontainers.mongodb.model.BioContainerToolVersion;
import pro.biocontainers.mongodb.service.BioContainersService;
import pro.biocontainers.pipelines.configs.DataSourceConfiguration;
import pro.biocontainers.pipelines.jobs.AbstractJob;
import pro.biocontainers.pipelines.utilities.PipelineConstants;
import pro.biocontainers.readers.github.configs.GitHubConfiguration;
import pro.biocontainers.readers.github.services.GitHubFileNameList;
import pro.biocontainers.readers.github.services.GitHubFileReader;

import java.util.List;

@Configuration
@Slf4j
@Import({ DataSourceConfiguration.class, MongoDBConfiguration.class, GitHubConfiguration.class})
public class AnnotateToolFromContainerVersionsJob extends AbstractJob {

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Autowired
    BioContainersService mongoService;


    /**
     * This methods connects to the database read all the Oracle information for public
     * @return
     */
    @Bean
    Step annnotateTools() {
        return stepBuilderFactory
                .get(PipelineConstants.StepNames.ANNOTATE_DOCKERHUB_RECIPE.name())
                .tasklet((stepContribution, chunkContext) -> {

                    List<BioContainerToolVersion> allVersions = mongoService.findAllToolVersion();
                    allVersions.stream().forEach();


                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    /**
     * Defines the job to Sync all the projects from OracleDB into MongoDB database.
     *
     * @return the calculatePrideArchiveDataUsage job
     */
    @Bean
    public Job annotateToolFromContainers() {
        return jobBuilderFactory
                .get(PipelineConstants.JobNames.ANNOTATE_CONTAINERS_JOB.getName())
                .start(annnotateTools())
                .build();
    }



}

