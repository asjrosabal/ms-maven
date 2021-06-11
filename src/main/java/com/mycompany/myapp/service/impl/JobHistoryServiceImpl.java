package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.JobHistory;
import com.mycompany.myapp.repository.JobHistoryRepository;
import com.mycompany.myapp.service.JobHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link JobHistory}.
 */
@Service
@Transactional
public class JobHistoryServiceImpl implements JobHistoryService {

    private final Logger log = LoggerFactory.getLogger(JobHistoryServiceImpl.class);

    private final JobHistoryRepository jobHistoryRepository;

    public JobHistoryServiceImpl(JobHistoryRepository jobHistoryRepository) {
        this.jobHistoryRepository = jobHistoryRepository;
    }

    @Override
    public Mono<JobHistory> save(JobHistory jobHistory) {
        log.debug("Request to save JobHistory : {}", jobHistory);
        return jobHistoryRepository.save(jobHistory);
    }

    @Override
    public Mono<JobHistory> partialUpdate(JobHistory jobHistory) {
        log.debug("Request to partially update JobHistory : {}", jobHistory);

        return jobHistoryRepository
            .findById(jobHistory.getId())
            .map(
                existingJobHistory -> {
                    if (jobHistory.getStartDate() != null) {
                        existingJobHistory.setStartDate(jobHistory.getStartDate());
                    }
                    if (jobHistory.getEndDate() != null) {
                        existingJobHistory.setEndDate(jobHistory.getEndDate());
                    }
                    if (jobHistory.getLanguage() != null) {
                        existingJobHistory.setLanguage(jobHistory.getLanguage());
                    }

                    return existingJobHistory;
                }
            )
            .flatMap(jobHistoryRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<JobHistory> findAll(Pageable pageable) {
        log.debug("Request to get all JobHistories");
        return jobHistoryRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return jobHistoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<JobHistory> findOne(Long id) {
        log.debug("Request to get JobHistory : {}", id);
        return jobHistoryRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete JobHistory : {}", id);
        return jobHistoryRepository.deleteById(id);
    }
}
