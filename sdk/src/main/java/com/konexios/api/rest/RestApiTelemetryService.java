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

import androidx.annotation.NonNull;

import com.konexios.api.AbstractTelemetrySenderService;
import com.konexios.api.Constants;
import com.konexios.api.common.ErrorUtils;
import com.konexios.api.listeners.ConnectionListener;
import com.konexios.api.listeners.TelemetryRequestListener;
import com.konexios.api.models.TelemetryModel;

import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * rest telemetry sender
 */

public final class RestApiTelemetryService extends AbstractTelemetrySenderService {
    private RestApiService mService;

    private CallbackHandler mCallbackHandler = new CallbackHandler();

    private Set<TelemetryRequestListener> telemetryRequestListeners = new HashSet<>();

    public RestApiTelemetryService(RestApiService service) {
        mService = service;
    }

    @Override
    public void connect(@NonNull ConnectionListener listener) {
        listener.onConnectionSuccess();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void addTelemetryRequestListener(@NonNull TelemetryRequestListener listener) {
        if (listener != null) {
            telemetryRequestListeners.add(listener);
        }
    }

    @Override
    public void sendSingleTelemetry(@NonNull TelemetryModel telemetry) {
        String json = telemetry.getTelemetry();
        RequestBody body = RequestBody.create(Constants.JSON, json);
        Call<ResponseBody> call = mService.sendTelemetry(body);
        call.enqueue(mCallbackHandler);
    }

    @Override
    public void sendBatchTelemetry(List<TelemetryModel> telemetry) {
        String json = formatBatchPayload(telemetry);
        RequestBody body = RequestBody.create(Constants.JSON, json);
        Call<ResponseBody> call = mService.sendBatchTelemetry(body);
        call.enqueue(mCallbackHandler);
    }

    @Override
    public boolean hasBatchMode() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    private class CallbackHandler implements Callback<ResponseBody> {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Timber.v("data sent to cloud: " + response.code());
            if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                for(TelemetryRequestListener listener : telemetryRequestListeners) {
                    listener.onTelemetrySendSuccess();
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Timber.e("data sent to cloud failed: " + t.toString());
            for(TelemetryRequestListener listener : telemetryRequestListeners) {
                listener.onTelemetrySendError(ErrorUtils.parseError(t));
            }
        }
    }
}
