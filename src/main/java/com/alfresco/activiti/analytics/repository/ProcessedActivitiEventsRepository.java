package com.alfresco.activiti.analytics.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alfresco.activiti.analytics.entity.ActivitiEventAbstract;
import com.alfresco.activiti.analytics.entity.ProcessedActivitiEvents;

public interface ProcessedActivitiEventsRepository extends JpaRepository<ProcessedActivitiEvents, Long> {

    List<ActivitiEventAbstract> findByTypeInAndProcessInstanceId(List<String> types, String processInstanceId);


    @Query("select distinct evt.processInstanceId as processInstanceId,  evt.processDefinitionId as processDefinitionId from ProcessedActivitiEvents evt "
            + "where evt.processDefinitionId is not null and evt.timestamp > :lastUpdatedTimestamp and evt.timestamp <= :maxTimeStamp"
            + " and  evt.processDefinitionId not in :excludedProcessDefinitionIdList")
    List<Map<String, Object>> findUniqueProcessList(@Param("lastUpdatedTimestamp") Date lastUpdatedTimestamp,
                                                    @Param("maxTimeStamp") Date maxTimeStamp, @Param("excludedProcessDefinitionIdList") List<String> excludedProcessDefinitionIdList);

    @Query(nativeQuery = true, value = "select max(TIME_STAMP_) as TO_TIMESTAMP from (select TIME_STAMP_ from PROCESSED_ACTIVITI_EVENTS where "
            + "PROC_DEF_ID_ is not null and TIME_STAMP_ > :to_timestamp "
            + "and  PROC_DEF_ID_ not in :excludedProcessDefinitionIdList "
            + "group by TIME_STAMP_  order by TIME_STAMP_ asc) where rownum <= :queryBatchSize")
    String getMaxTimestamp(@Param("to_timestamp") Date to_timestamp,
                           @Param("queryBatchSize") String queryBatchSize, @Param("excludedProcessDefinitionIdList") List<String> excludedProcessDefinitionIdList);


        @Query(nativeQuery=true, value="select max(TIME_STAMP_) as TO_TIMESTAMP from (select TIME_STAMP_ from PROCESSED_ACTIVITI_EVENTS where "
        + "PROC_DEF_ID_ is not null and TIME_STAMP_ > :to_timestamp "
        + "and  PROC_DEF_ID_ not in :excludedProcessDefinitionIdList "
        + "group by TIME_STAMP_  order by TIME_STAMP_ asc LIMIT cast(:queryBatchSize as int)) as subquery" )
        String getMaxTimestampPostgres(@Param("to_timestamp") Date  to_timestamp, 
                @Param("queryBatchSize") String  queryBatchSize, @Param("excludedProcessDefinitionIdList") List<String> excludedProcessDefinitionIdList);
                
}
