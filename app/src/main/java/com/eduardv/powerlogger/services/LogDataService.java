package com.eduardv.powerlogger.services;

import com.eduardv.powerlogger.Env;
import com.eduardv.powerlogger.dto.logs.IntensityType;
import com.eduardv.powerlogger.dto.logs.LogDTO;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LogDataService {

    @GET(Env.BASE_API_PREFIX + "/logs")
    Call<List<LogDTO>> fetchAllLogs(@Header("Authorization") String token, @Query("date") LocalDate date);

    @GET(Env.BASE_API_PREFIX + "/logs/calories")
    Call<LogDTO> getCaloriesForLog(@Header("Authorization") String token,
                                   @Query("category") String category,
                                   @Query("intensity") float intensity,
                                   @Query("intensityType") IntensityType intensityType);

    @POST(Env.BASE_API_PREFIX + "/exercises/{exerciseId}/logs")
    Call<LogDTO> postNewLog(@Header("Authorization") String token, @Path("exerciseId") String exerciseId, @Body LogDTO log);

    @POST(Env.BASE_API_PREFIX + "/logs/batch")
    Call<List<LogDTO>> sendBatchLogs(@Header("Authorization") String token, @Body List<LogDTO> log);

    @PUT(Env.BASE_API_PREFIX + "/logs/{id}")
    Call<LogDTO> updateLog(@Header("Authorization") String token, @Path("id") String id, @Body LogDTO log);

    @DELETE(Env.BASE_API_PREFIX + "/logs/{id}")
    Call<Void> delete(@Header("Authorization") String token, @Path("id") String id);
}
