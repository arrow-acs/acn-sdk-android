/*
 * Copyright (c) 2017-2019 Arrow Electronics, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License 2.0
 * which accompanies this distribution, and is available at
 * http://apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Arrow Electronics, Inc.
 *     Konexios, Inc.
 */

package com.konexios.api.rest;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.konexios.api.models.AccountRequest;
import com.konexios.api.models.AccountRequest2;
import com.konexios.api.models.AccountResponse;
import com.konexios.api.models.AccountResponse2;
import com.konexios.api.models.AuditLogModel;
import com.konexios.api.models.AvailableFirmwareResponse;
import com.konexios.api.models.CommonResponse;
import com.konexios.api.models.ConfigResponse;
import com.konexios.api.models.CreateAndStartSoftwareReleaseScheduleRequest;
import com.konexios.api.models.DeviceActionModel;
import com.konexios.api.models.DeviceActionTypeModel;
import com.konexios.api.models.DeviceEventModel;
import com.konexios.api.models.DeviceModel;
import com.konexios.api.models.DeviceRegistrationModel;
import com.konexios.api.models.DeviceRegistrationResponse;
import com.konexios.api.models.DeviceTypeModel;
import com.konexios.api.models.DeviceTypeRegistrationModel;
import com.konexios.api.models.ErrorBodyModel;
import com.konexios.api.models.FindDeviceStateResponse;
import com.konexios.api.models.FirmwareVersionModel;
import com.konexios.api.models.GatewayCommand;
import com.konexios.api.models.GatewayModel;
import com.konexios.api.models.GatewayResponse;
import com.konexios.api.models.ListResultModel;
import com.konexios.api.models.MessageStatusResponse;
import com.konexios.api.models.NewDeviceStateTransactionRequest;
import com.konexios.api.models.NodeModel;
import com.konexios.api.models.NodeRegistrationModel;
import com.konexios.api.models.NodeTypeModel;
import com.konexios.api.models.NodeTypeRegistrationModel;
import com.konexios.api.models.PagingResultModel;
import com.konexios.api.models.RequestedFirmwareResponse;
import com.konexios.api.models.SocialEventDevice;
import com.konexios.api.models.TelemetryCountResponse;
import com.konexios.api.models.TelemetryItemModel;

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
import rx.Observable;

/**
 * main retrofit service
 */

@Keep
public interface RestApiService {

    //Account com.konexios.com.konexios.api
    @NonNull
    @POST("/api/v1/kronos/accounts")
    Call<AccountResponse> registerAccount(@Body AccountRequest accountRequest);

    //Account com.konexios.com.konexios.api
    @NonNull
    @POST("/api/v1/pegasus/users/auth2")
    Call<AccountResponse2> registerAccount2(@Body AccountRequest2 accountRequest);

    //GatewayApi
    @NonNull
    @POST("/api/v1/kronos/gateways")
    Call<GatewayResponse> registerGateway(@Body GatewayModel gatewayModel);

    @NonNull
    @PUT("/api/v1/kronos/gateways/{hid}/checkin")
    Call<CommonResponse> checkin(@Path("hid") String hid);

    @NonNull
    @GET("/api/v1/kronos/gateways/{hid}/config")
    Call<ConfigResponse> getConfig(@Path("hid") String hid);

    @NonNull
    @PUT("/api/v1/kronos/gateways/{hid}")
    Call<GatewayResponse> updateGateway(@Path("hid") String hid, @Body GatewayModel gatewayModel);

    @NonNull
    @GET("/api/v1/kronos/gateways/{hid}/devices")
    Call<ListResultModel<DeviceModel>> getDevicesByGatewayHid(@Path("hid") String hid);

    @NonNull
    @PUT("/api/v1/kronos/gateways/{hid}/heartbeat")
    Call<CommonResponse> heartBeat(@Path("hid") String hid);

    @NonNull
    @GET("/api/v1/kronos/gateways")
    Call<List<GatewayModel>> findAllGateways();

    @NonNull
    @GET("/api/v1/kronos/gateways/{hid}")
    Call<GatewayModel> findGateway(@Path("hid") String hid);

    @NonNull
    @Deprecated
    @POST("/api/v1/kronos/gateways/{hid}/commands/device-command")
    Call<CommonResponse> sendGatewayCommand(@Path("hid") String hid, @Body GatewayCommand command);

    @NonNull
    @GET("/api/v1/kronos/gateways/{hid}/logs")
    Call<PagingResultModel<AuditLogModel>> getGatewayLogs(@Path("hid") String hid,
                                                          @Query("createdDateFrom") String createdDateFrom,
                                                          @Query("createdDateTo") String createdDateTo,
                                                          @Query("userHids") List<String> userHids,
                                                          @Query("types") List<String> types,
                                                          @Query("sortField") String sortField,
                                                          @Query("sortDirection") String sortDirection,
                                                          @Query("_page") int page,
                                                          @Query("_size") int size);

    @POST("/api/v1/kronos/gateways/{hid}/errors")
    Call<CommonResponse> sendGatewayError(@Path("hid") String gatewayHid,
                                          @Body ErrorBodyModel error);

    @NonNull
    @GET("com.konexios.com.konexios.api/v1/kronos/devices/{hid}/firmware/available")
    Call<ListResultModel<FirmwareVersionModel>> getAvailableFirmwareForGatewayByHid(@Path("hid") String hid);

    //telemetry com.konexios.com.konexios.api
    @NonNull
    @POST("/api/v1/kronos/telemetries")
    Call<ResponseBody> sendTelemetry(@Body RequestBody body);

    @NonNull
    @GET("/api/v1/kronos/telemetries/applications/{applicationHid}")
    Call<PagingResultModel<TelemetryItemModel>> findTelemetryByAppHid(@Path("applicationHid") String applicationHid,
                                                                      @Query("fromTimestamp") String fromTimestamp,
                                                                      @Query("toTimestamp") String toTimestamp,
                                                                      @Query("telemetryNames") String telemetryNames,
                                                                      @Query("_page") int page,
                                                                      @Query("_size") int size);

    @NonNull
    @POST("/api/v1/kronos/telemetries/batch")
    Call<ResponseBody> sendBatchTelemetry(@Body RequestBody body);

    @NonNull
    @GET("/api/v1/kronos/telemetries/devices/{deviceHid}")
    Call<PagingResultModel<TelemetryItemModel>> findTelemetryByDeviceHid(@Path("deviceHid") String deviceHid,
                                                                         @Query("fromTimestamp") String fromTimestamp,
                                                                         @Query("toTimestamp") String toTimestamp,
                                                                         @Query("telemetryNames") String telemetryNames,
                                                                         @Query("_page") int page,
                                                                         @Query("_size") int size);

    @NonNull
    @GET("/api/v1/kronos/telemetries/nodes/{nodeHid}")
    Call<PagingResultModel<TelemetryItemModel>> findTelemetryByNodeHid(@Path("nodeHid") String nodeHid,
                                                                       @Query("fromTimestamp") String fromTimestamp,
                                                                       @Query("toTimestamp") String toTimestamp,
                                                                       @Query("telemetryNames") String telemetryNames,
                                                                       @Query("_page") int page,
                                                                       @Query("_size") int size);

    @NonNull
    @GET("/api/v1/kronos/telemetries/devices/{deviceHid}/count")
    Call<TelemetryCountResponse> getTelemetryItemsCount(@Path("deviceHid") String deviceHid,
                                                        @Query("telemetryName") String telemetryName,
                                                        @Query("fromTimestamp") String fromTimestamp,
                                                        @Query("toTimestamp") String toTimestamp);

    @NonNull
    @GET("/api/v1/kronos/telemetries/devices/{deviceHid}/latest")
    Call<ListResultModel<TelemetryItemModel>> getLastTelemetry(@Path("deviceHid") String deviceHid);

    @POST("/api/v1/kronos/devices/{hid}/errors")
    Call<CommonResponse> sendDeviceError(@Path("hid") String deviceHid,
                                         @Body ErrorBodyModel error);

    @NonNull
    @PUT("/api/v1/core/events/{hid}/received")
    Call<CommonResponse> putReceived(@Path("hid") String hid);

    @NonNull
    @PUT("/api/v1/core/events/{hid}/succeeded")
    Call<CommonResponse> putSucceeded(@Path("hid") String hid);

    @NonNull
    @PUT("/api/v1/core/events/{hid}/failed")
    Call<CommonResponse> putFailed(@Path("hid") String hid);

    //device-action com.konexios.com.konexios.api
    @NonNull
    @GET("/api/v1/kronos/devices/actions/types")
    Call<ListResultModel<DeviceActionTypeModel>> getActionTypes();

    @NonNull
    @GET("/api/v1/kronos/devices/{hid}/actions")
    Call<ListResultModel<DeviceActionModel>> getActions(@Path("hid") String hid);

    @NonNull
    @POST("/api/v1/kronos/devices/{hid}/actions")
    Call<CommonResponse> postAction(@Path("hid") String hid, @Body DeviceActionModel action);

    @NonNull
    @PUT("/api/v1/kronos/devices/{hid}/actions/{index}")
    Call<CommonResponse> updateAction(@Path("hid") String hid, @Path("index") int index,
                                      @Body DeviceActionModel action);

    @NonNull
    @DELETE("/api/v1/kronos/devices/{hid}/actions/{index}")
    Call<CommonResponse> deleteAction(@Path("hid") String hid, @Path("index") int index);

    //Device com.konexios.com.konexios.api
    @NonNull
    @GET("/api/v1/kronos/devices/{hid}/events")
    Call<PagingResultModel<DeviceEventModel>> getHistoricalEvents(@Path("hid") String hid,
                                                                  @Query("createdDateFrom") String createdDateFrom,
                                                                  @Query("createdDateTo") String createdDateTo,
                                                                  @Query("sortField") String sortField,
                                                                  @Query("sortDirection") String sortDirection,
                                                                  @Query("statuses[]") List<String> statuses,
                                                                  @Query("systemNames[]") List<String> systemNames,
                                                                  @Query("_page") int page,
                                                                  @Query("_size") int size);

    @NonNull
    @GET("/api/v1/kronos/devices")
    Call<PagingResultModel<DeviceModel>> findAllDevices(@Query("userHid") String userHid,
                                                        @Query("uid") String uid,
                                                        @Query("type") String type,
                                                        @Query("gatewayHid") String gatewayHid,
                                                        @Query("createdBefore") String createdBefore,
                                                        @Query("createdAfter") String createdAfter,
                                                        @Query("updatedBefore") String updatedBefore,
                                                        @Query("updatedAfter") String updatedAfter,
                                                        @Query("enabled") String enabled,
                                                        @Query("_page") int page,
                                                        @Query("_size") int size);

    @NonNull
    @POST("/api/v1/kronos/devices")
    Call<DeviceRegistrationResponse> createOrUpdateDevice(@Body DeviceRegistrationModel deviceRequest);

    @NonNull
    @GET("/api/v1/kronos/devices/{hid}")
    Call<DeviceModel> findDeviceByHid(@Path("hid") String hid);

    @NonNull
    @PUT("/api/v1/kronos/devices/{hid}")
    Call<CommonResponse> updateExistingDevice(@Path("hid") String hid, @Body DeviceRegistrationModel model);

    @NonNull
    @GET("/api/v1/kronos/devices/{hid}/logs")
    Call<PagingResultModel<AuditLogModel>> listDeviceAuditLogs(@Path("hid") String hid,
                                                               @Query("createdDateFrom") String createdDateFrom,
                                                               @Query("createdDateTo") String createdDateTo,
                                                               @Query("userHids") List<String> userHids,
                                                               @Query("types") List<String> types,
                                                               @Query("sortField") String sortField,
                                                               @Query("sortDirection") String sortDirection,
                                                               @Query("_page") int page,
                                                               @Query("_size") int size);

    @NonNull
    @GET("/api/v1/kronos/devices/{hid}/firmware/available")
    Call<List<FirmwareVersionModel>> getAvailableFirmwareForDeviceByHid(@Path("hid") String hid);

    //node com.konexios.com.konexios.api

    @NonNull
    @GET("/api/v1/kronos/nodes")
    Call<ListResultModel<NodeModel>> getListExistingNodes();

    @NonNull
    @POST("/api/v1/kronos/nodes")
    Call<CommonResponse> createNewNode(@Body NodeRegistrationModel model);

    @NonNull
    @PUT("/api/v1/kronos/nodes/{hid}")
    Call<CommonResponse> updateExistingNode(@Path("hid") String nodeHid, @Body NodeRegistrationModel model);

    //node - type com.konexios.com.konexios.api

    @NonNull
    @GET("/api/v1/kronos/nodes/types")
    Call<ListResultModel<NodeTypeModel>> getListNodeTypes();

    @NonNull
    @POST("/api/v1/kronos/nodes/types")
    Call<CommonResponse> createNewNodeType(@Body NodeTypeRegistrationModel model);

    @NonNull
    @PUT("/api/v1/kronos/nodes/types/{hid}")
    Call<CommonResponse> updateExistingNodeType(@Path("hid") String hid, @Body NodeTypeRegistrationModel model);

    // device - type com.konexios.com.konexios.api

    @NonNull
    @GET("/api/v1/kronos/devices/types")
    Call<ListResultModel<DeviceTypeModel>> getListDeviceTypes();

    @NonNull
    @POST("/api/v1/kronos/devices/types")
    Call<CommonResponse> createNewDeviceType(@Body DeviceTypeRegistrationModel body);

    @NonNull
    @PUT("/api/v1/kronos/devices/types/{hid}")
    Call<CommonResponse> updateExistingDeviceType(@Path("hid") String hid, @Body DeviceTypeRegistrationModel body);

    @NonNull
    @GET("/api/v1/kronos/devices/{hid}/state")
    Call<FindDeviceStateResponse> findDeviceState(@Path("hid") String hid);

    @NonNull
    @POST("/api/v1/kronos/devices/{hid}/state/request")
    Call<CommonResponse> createNewDeviceStateTransaction(@Path("hid") String hid,
                                                         @Body NewDeviceStateTransactionRequest body);

    @NonNull
    @PUT("/api/v1/kronos/devices/{hid}/state/trans/{transHid}/succeeded")
    Call<MessageStatusResponse> deviceStateTransactionSucceeded(@Path("hid") String hid,
                                                                @Path("transHid") String transId);

    @NonNull
    @PUT("/api/v1/kronos/devices/{hid}/state/trans/{transHid}/failed")
    Call<MessageStatusResponse> deviceStateTransactionFailed(@Path("hid") String hid,
                                                             @Path("transHid") String transId,
                                                             @Body ErrorBodyModel error);

    @NonNull
    @PUT("/api/v1/kronos/devices/{hid}/state/trans/{transHid}/received")
    Call<MessageStatusResponse> deviceStateTransactionReceived(@Path("hid") String hid,
                                                               @Path("transHid") String transId);

    @NonNull
    @POST("/api/v1/kronos/devices/{hid}/state/update")
    Call<CommonResponse> updateDeviceStateTransaction(@Path("hid") String hid,
                                                      @Body NewDeviceStateTransactionRequest body);

    // RTU FIRMWARE API

    @NonNull
    @GET("/api/v1/kronos/rtu/find")
    Call<RequestedFirmwareResponse> getListRequestedFirmware(@Query("status") String status,
                                                             @Query("_page") int page,
                                                             @Query("_size") int size);

    @NonNull
    @GET("/api/v1/kronos/rtu/find/available")
    Call<AvailableFirmwareResponse> getListAvailableFirmware(@Query("deviceTypeHid") String deviceTypeHid);

    @NonNull
    @PUT("/api/v1/kronos/rtu/request/{softwareReleaseHid}")
    Call<MessageStatusResponse> requireRightToUseFirmware(@Path("softwareReleaseHid") String softwareReleaseHid);

    // Software Release Schedule ApI

    @NonNull
    @POST("/api/v1/kronos/software/releases/schedules/start")
    Call<CommonResponse> createAndStartNewSoftwareReleaseSchedule(@Body CreateAndStartSoftwareReleaseScheduleRequest body);

    // Software Release Trans Api

    @NonNull
    @PUT("/api/v1/kronos/software/releases/transactions/{hid}/failed")
    Call<MessageStatusResponse> markSoftwareReleaseTransFailed(@Path("hid") String hid,
                                                               @Body ErrorBodyModel model);

    @NonNull
    @PUT("/api/v1/kronos/software/releases/transactions/{hid}/received")
    Call<MessageStatusResponse> markSoftwareReleaseTransReceived(@Path("hid") String hid);

    @NonNull
    @PUT("/api/v1/kronos/software/releases/transactions/{hid}/succeeded")
    Call<MessageStatusResponse> markSoftwareReleaseTransSecceeded(@Path("hid") String hid);

    @NonNull
    @POST("/api/v1/kronos/software/releases/transactions/{hid}/start")
    Call<MessageStatusResponse> startSoftwareReleaseTrans(@Path("hid") String hid);

    @NonNull
    @GET("/api/v1/kronos/software/releases/transactions/{hid}/{token}/file")
    Call<ResponseBody> downloadSoftwareReleaseFile(@Path("hid") String hid, @Path("token") String token);

    @NonNull
    @GET("/api/v1/kronos/social/event/devices/device/type/simba-pro ")
    Observable<List<SocialEventDevice>> getSocialEventDevices();
}
