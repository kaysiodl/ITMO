package com.kaysiodl.rest;

import com.kaysiodl.database.Result;
import com.kaysiodl.database.User;
import com.kaysiodl.dto.PageResponse;
import com.kaysiodl.dto.ResultsRequestDTO;
import com.kaysiodl.dto.ResultsResponseDTO;
import com.kaysiodl.service.AuthService;
import com.kaysiodl.service.ResultsService;
import com.kaysiodl.utils.ValidationUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST resource for handling point hit check results.
 */
@Path("/check")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ResultsResource {
    @Inject
    private ResultsService resultsService;

    @Inject
    private AuthService authService;

    /**
     * Adds a new result of the point check.
     *
     * @param dto       point data
     * @param sessionId session identifier
     * @return response DTO with the result
     */
    @POST
    public ResultsResponseDTO add(
            ResultsRequestDTO dto,
            @HeaderParam("X-Session-Id") String sessionId
    ) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        try {
            ValidationUtil.validatePoint(dto);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
        User user;
        try {
            user = authService.getUserBySession(sessionId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        Result result = resultsService.save(
                dto.getX(), dto.getY(), dto.getR(), user
        );
        return toDto(result);
    }


    /**
     * Gets all results of the user.
     *
     * @param sessionId session identifier
     * @return list of result DTOs
     */
    @GET
    @Path("/all")
    public List<ResultsResponseDTO> getUserResults(
            @HeaderParam("X-Session-Id") String sessionId
    ) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        User user = authService.getUserBySession(sessionId);
        return resultsService.findByUser(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets user results with pagination and filters.
     *
     * @param sessionId session identifier
     * @param page      page number
     * @param size      page size
     * @param sort      sorting parameters
     * @param uriInfo   URI information for filters
     * @return paginated response
     */
    @GET
    public PageResponse<ResultsResponseDTO> getResults(
            @HeaderParam("X-Session-Id") String sessionId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") String sort,
            @Context UriInfo uriInfo
    ) {
        User user = authService.getUserBySession(sessionId);

        String sortField = null;
        String sortDir = null;

        if (sort != null) {
            String[] parts = sort.split(",");
            sortField = parts[0];
            sortDir = parts.length > 1 ? parts[1] : "asc";
        }

        Map<String, Map<String, String>> filters = new HashMap<>();

        uriInfo.getQueryParameters().forEach((key, values) -> {
            if (key.startsWith("filter.")) {
                String[] parts = key.split("\\.");
                String field = parts[1];
                String op = parts[2];

                filters
                        .computeIfAbsent(field, f -> new HashMap<>())
                        .put(op, values.get(0));
            }
        });

        PageResponse<Result> pageResult =
                resultsService.findByUserPaged(
                        user, page, size, sortField, sortDir, filters
                );

        return new PageResponse<>(
                pageResult.getResults().stream().map(this::toDto).toList(),
                pageResult.getTotal(),
                page,
                size
        );
    }


    /**
     * Deletes all results of the user.
     *
     * @param sessionId session identifier
     */
    @DELETE
    public void clearUserPoints(@HeaderParam("X-Session-Id") String sessionId) {
        User user = authService.getUserBySession(sessionId);
        resultsService.deleteByUser(user);
    }

    /**
     * Converts Result entity to DTO.
     *
     * @param result entity result
     * @return result DTO
     */
    private ResultsResponseDTO toDto(Result result) {
        return ResultsResponseDTO
                .builder()
                .id(result.getId())
                .x(result.getX())
                .y(result.getY())
                .r(result.getR())
                .hit(result.isHit())
                .currentTime(result.getCurrentTime())
                .executionTime(result.getExecutionTime())
                .build();
    }


}
