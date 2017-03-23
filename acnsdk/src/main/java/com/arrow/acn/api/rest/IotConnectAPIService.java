/*
 * Copyright (c) 2017 Arrow Electronics, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License 2.0
 * which accompanies this distribution, and is available at
 * http://apache.org/licenses/LICENSE-2.0
 *
 * Contributors: Arrow Electronics, Inc.
 */

package com.arrow.acn.api.rest;


import com.arrow.acn.api.models.AccountRequest;
import com.arrow.acn.api.models.AccountResponse;
import com.arrow.acn.api.models.AuditLogModel;
import com.arrow.acn.api.models.CommonResponse;
import com.arrow.acn.api.models.ConfigResponse;
import com.arrow.acn.api.models.DeviceActionModel;
import com.arrow.acn.api.models.DeviceActionTypeModel;
import com.arrow.acn.api.models.DeviceEventModel;
import com.arrow.acn.api.models.DeviceModel;
import com.arrow.acn.api.models.DeviceRegistrationModel;
import com.arrow.acn.api.models.DeviceRegistrationResponse;
import com.arrow.acn.api.models.DeviceTypeModel;
import com.arrow.acn.api.models.DeviceTypeRegistrationModel;
import com.arrow.acn.api.models.GatewayCommand;
import com.arrow.acn.api.models.GatewayModel;
import com.arrow.acn.api.models.GatewayResponse;
import com.arrow.acn.api.models.ListResultModel;
import com.arrow.acn.api.models.NodeModel;
import com.arrow.acn.api.models.NodeRegistrationModel;
import com.arrow.acn.api.models.NodeTypeModel;
import com.arrow.acn.api.models.NodeTypeRegistrationModel;
import com.arrow.acn.api.models.PagingResultModel;
import com.arrow.acn.api.models.TelemetryItemModel;
import com.arrow.acn.api.models.TelemetryStatsModel;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by osminin on 3/15/2016.
 */
public interface IotConnectAPIService {

    //Account api
    @POST("/api/v1/kronos/accounts")
    Call<AccountResponse> registerAccount(@Body AccountRequest accountRequest);

    //GatewayApi
    @POST("/api/v1/kronos/gateways")
    Call<GatewayResponse> registerGateway(@Body GatewayModel gatewayModel);

    @PUT("/api/v1/kronos/gateways/{hid}/checkin")
    Call<CommonResponse> checkin(@Path("hid") String hid);

    @GET("/api/v1/kronos/gateways/{hid}/config")
    Call<ConfigResponse> getConfig(@Path("hid") String hid);

    @PUT("/api/v1/kronos/gateways/{hid}")
    Call<GatewayResponse> updateGateway(@Path("hid") String hid, @Body GatewayModel gatewayModel);

    @GET("/api/v1/kronos/gateways/{hid}/devices")
    Call<ListResultModel<DeviceModel>> getDevicesByGatewayHid(@Path("hid") String hid);

    @PUT("/api/v1/kronos/gateways/{hid}/heartbeat")
    Call<CommonResponse> heartBeat(@Path("hid") String hid);

    @GET("/api/v1/kronos/gateways")
    Call<List<GatewayModel>> findAllGateways();

    @GET("/api/v1/kronos/gateways/{hid}")
    Call<GatewayModel> findGateway(@Path("hid") String hid);

    @POST("/api/v1/kronos/gateways/{hid}/commands/device-command")
    Call<GatewayResponse> sendGatewayCommand(@Path("hid") String hid, GatewayCommand command);

    @GET("/api/v1/kronos/gateways/{hid}/logs")
    Call<PagingResultModel<AuditLogModel>> getGatewayLogs(@Path("hid") String hid,
                                           @Query("createdDateFrom") String createdDateFrom,
                                           @Query("createdDateTo") String createdDateTo,
                                           @Query("userHids") String[] userHids,
                                           @Query("types") String[] types,
                                           @Query("sortField") String sortField,
                                           @Query("sortDirection") String sortDirection,
                                           @Query("_page") int page,
                                           @Query("_size") int size);

    //telemetry api
    @POST("/api/v1/kronos/telemetries")
    Call<ResponseBody> sendTelemetry(@Body RequestBody body);

    @GET("/api/v1/kronos/telemetries/applications/{applicationHid}")
    Call<PagingResultModel<TelemetryItemModel>> findTelemetryByAppHid(@Path("applicationHid") String applicationHid,
                                                                      @Query("fromTimestamp") String fromTimestamp,
                                                                      @Query("toTimestamp") String toTimestamp,
                                                                      @Query("telemetryNames") String telemetryNames,
                                                                      @Query("_page") int page,
                                                                      @Query("_size") int size);

    @POST("/api/v1/kronos/telemetries/batch")
    Call<ResponseBody> sendBatchTelemetry(@Body RequestBody body);

    @GET("/api/v1/kronos/telemetries/devices/{deviceHid}")
    Call<PagingResultModel<TelemetryItemModel>> findTelemetryByDeviceHid(@Path("deviceHid") String deviceHid,
                                                                         @Query("fromTimestamp") String fromTimestamp,
                                                                         @Query("toTimestamp") String toTimestamp,
                                                                         @Query("telemetryNames") String telemetryNames,
                                                                         @Query("_page") int page,
                                                                         @Query("_size") int size);

    @GET("/api/v1/kronos/telemetries/nodes/{nodeHid}")
    Call<PagingResultModel<TelemetryItemModel>> findTelemetryByNodeHid(@Path("nodeHid") String nodeHid,
                                                                       @Query("fromTimestamp") String fromTimestamp,
                                                                       @Query("toTimestamp") String toTimestamp,
                                                                       @Query("telemetryNames") String telemetryNames,
                                                                       @Query("_page") int page,
                                                                       @Query("_size") int size);

    @GET("/api/v1/kronos/telemetries/devices/{deviceHid}/latest")
    Call<ListResultModel<TelemetryItemModel>> getLastTelemetry(@Path("deviceHid") String deviceHid);

    @GET("/api/kronos/device/{deviceHid}/stats")
    Call<TelemetryStatsModel> getTelemetryStats(@Path("deviceHid") String deviceHid);

    @PUT("/api/v1/core/events/{hid}/received")
    Call<ResponseBody> putReceived(@Path("hid") String hid);

    @PUT("/api/v1/core/events/{hid}/succeeded")
    Call<ResponseBody> putSucceeded(@Path("hid") String hid);

    @PUT("/api/v1/core/events/{hid}/failed")
    Call<ResponseBody> putFailed(@Path("hid") String hid);

    //device-action api
    @GET("/api/v1/kronos/devices/actions/types")
    Call<ListResultModel<DeviceActionTypeModel>> getActionTypes();

    @GET("/api/v1/kronos/devices/{hid}/actions")
    Call<ListResultModel<DeviceActionModel>> getActions(@Path("hid") String hid);

    @POST("/api/v1/kronos/devices/{hid}/actions")
    Call<ResponseBody> postAction(@Path("hid") String hid, @Body DeviceActionModel action);

    @PUT("/api/v1/kronos/devices/{hid}/actions/{index}")
    Call<ResponseBody> updateAction(@Path("hid") String hid, @Path("index") int index,
                                    @Body DeviceActionModel action);
    @DELETE("/api/v1/kronos/devices/{hid}/actions/{index}")
    Call<ResponseBody> deleteAction(@Path("hid") String hid, @Path("index") int index);

    //Device api
    @GET("/api/v1/kronos/devices/{hid}/events")
    Call<PagingResultModel<DeviceEventModel>> getHistoricalEvents(@Path("hid") String hid);

    @GET("/api/v1/kronos/devices")
    Call<PagingResultModel<DeviceModel>> findAllDevices(@Query("userHid") String userHid,
                                                @Query("uid") String uid,
                                                @Query("type") String type,
                                                @Query("gatewayHid") String gatewayHid,
                                                @Query("enabled") String enabled,
                                                @Query("_page") int page,
                                                @Query("_size") int size);
    @POST("/api/v1/kronos/devices")
    Call<DeviceRegistrationResponse> createOrUpdateDevice(@Body DeviceRegistrationModel deviceRequest);

    @GET("/api/v1/kronos/devices/{hid}")
    Call<DeviceModel> findDeviceByHid(@Path("hid") String hid);

    @PUT("/api/v1/kronos/devices/{hid}")
    Call<CommonResponse> updateExistingDevice(@Path("hid") String hid, @Body DeviceRegistrationModel model);

    @GET("/api/v1/kronos/devices/{hid}/logs")
    Call<PagingResultModel<AuditLogModel>> listDeviceAuditLogs(@Path("hid") String hid,
                                                               @Query("createdDateFrom") String createdDateFrom,
                                                               @Query("createdDateTo") String createdDateTo,
                                                               @Query("userHids") String[] userHids,
                                                               @Query("types") String[] types,
                                                               @Query("sortField") String sortField,
                                                               @Query("sortDirection") String sortDirection,
                                                               @Query("_page") int page,
                                                               @Query("_size") int size);
    //node api

    @GET("/api/v1/kronos/nodes")
    Call<ListResultModel<NodeModel>> getListExistingNodes();

    @POST("/api/v1/kronos/nodes")
    Call<CommonResponse> createNewNode(@Body NodeRegistrationModel model);

    @PUT("/api/v1/kronos/nodes/{hid}")
    Call<CommonResponse>  updateExistingNode(@Path("hid") String nodeHid, @Body NodeRegistrationModel model);

    //node - type api

    @GET("/api/v1/kronos/nodes/types")
    Call<ListResultModel<NodeTypeModel>> getListNodeTypes();

    @POST("/api/v1/kronos/nodes/types")
    Call<CommonResponse> createNewNodeType(NodeTypeRegistrationModel model);

    @PUT("/api/v1/kronos/nodes/types/{hid}")
    Call<CommonResponse> updateExistingNodeType(@Path("hid")String hid, @Body NodeTypeRegistrationModel model);

    // device - type api

    @GET("/api/v1/kronos/devices/types")
    Call<ListResultModel<DeviceTypeModel>> getListDeviceTypes();

    @POST("/api/v1/kronos/devices/types")
    Call<CommonResponse> createNewDeviceType(@Body DeviceTypeRegistrationModel body);

    @PUT("/api/v1/kronos/devices/types/{hid}")
    Call<CommonResponse> updateExistingDeviceType(@Path("hid")String hid, @Body DeviceTypeRegistrationModel body);

}