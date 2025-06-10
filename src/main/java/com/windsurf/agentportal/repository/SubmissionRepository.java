package com.windsurf.agentportal.repository;

import com.windsurf.agentportal.model.Submission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Submission entity
 */
@Repository
public interface SubmissionRepository extends CrudRepository<Submission, String> {
    
    /**
     * Find submissions by user ID
     * @param userId User ID
     * @return List of submissions
     */
    List<Submission> findByUserId(String userId);
    
    /**
     * Find submissions by agent ID
     * @param agentId Agent ID
     * @return List of submissions
     */
    List<Submission> findByAgentId(String agentId);
    
    /**
     * Find submissions by status
     * @param status Status
     * @return List of submissions
     */
    List<Submission> findByStatus(String status);
}
