package com.kaysiodl.service;

import com.kaysiodl.database.Result;
import com.kaysiodl.database.ResultsRepository;
import com.kaysiodl.database.User;
import com.kaysiodl.dto.PageResponse;
import com.kaysiodl.dto.ResultsResponseDTO;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * Service for handling point hit check results.
 */
@Stateless
public class ResultsService {
    @Inject
    private ResultsRepository resultsRepository;

    /**
     * Saves the result of the hit check for a user.
     * @param x X coordinate
     * @param y Y coordinate
     * @param r radius
     * @param user user
     * @return saved result
     */
    public Result save(double x, double y, double r, User user) {
        Long start = System.nanoTime();
        boolean hit = checkHit(x, y, r);
        Long end = System.nanoTime();

        Result result = Result.builder()
                .x(x)
                .y(y)
                .r(r)
                .hit(hit)
                .currentTime(
                        String.valueOf(LocalTime.now(ZoneId.of("Europe/Moscow"))
                                .withNano(0))
                )
                .executionTime(String.format("%.3f ms", (end - start) / 1_000_000.0))
                .user(user)
                .build();

        resultsRepository.add(result);
        return result;
    }

    /**
     * Finds all results for a user.
     * @param user user
     * @return list of results
     */
    public List<Result> findByUser(User user) {
        return resultsRepository.findByUser(user);
    }


    /**
     * Finds results for a user with pagination and filters.
     * @param user user
     * @param page page number
     * @param size page size
     * @param sortField sorting field
     * @param sortDir sorting direction
     * @param filters filters
     * @return paginated response
     */
    public PageResponse<Result> findByUserPaged(
            User user,
            int page,
            int size,
            String sortField,
            String sortDir,
            Map<String, Map<String, String>> filters
    ) {
        List<Result> data = resultsRepository.findByUserPaged(
                user,
                page,
                size,
                sortField,
                sortDir,
                filters
        );

        long total = resultsRepository.countByUser(user, filters);

        return new PageResponse<>(data, total, page, size);
    }


    /**
     * Deletes all results for a user.
     * @param user user
     */
    public void deleteByUser(User user) {
        resultsRepository.deleteAllByUser(user);
    }


    /**
     * Checks if the point is within the area.
     * @param x X coordinate
     * @param y Y coordinate
     * @param r radius
     * @return true if hit, false otherwise
     */
    public boolean checkHit(double x, double y, double r) {
        return ((x * x + y * y <= (r * r)) && x >= 0 && y <= 0) || // sector
                (x >= 0 && x <= r / 2 && y <= r && y >= 0) || //square
                ((y <= x / 2 + r / 2) && x <= 0 && y >= 0); //triangle
    }
}
