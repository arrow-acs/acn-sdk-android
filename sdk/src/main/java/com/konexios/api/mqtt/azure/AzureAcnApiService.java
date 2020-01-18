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

package com.konexios.api.mqtt.azure;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.konexios.api.listeners.ServerCommandsListener;
import com.konexios.api.mqtt.AbstractMqttAcnApiService;
import com.konexios.api.mqtt.azure.auth.IotHubSasToken;
import com.konexios.api.mqtt.azure.transport.TransportUtils;
import com.konexios.api.rest.RestApiService;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import timber.log.Timber;

/**
 * azure telemetry sender
 */

@Keep
public final class AzureAcnApiService extends AbstractMqttAcnApiService {
    private static final String TAG = AzureAcnApiService.class.getName();

    @NonNull
    private static String sslPrefix = "ssl://";
    @NonNull
    private static String sslPortSuffix = ":8883";

    private final String mAccessKey;
    private final String mHost;

    public AzureAcnApiService(String gatewayUid,
                              String accessKey,
                              String host,
                              ServerCommandsListener listener,
                              RestApiService restService) {
        super(gatewayUid, listener, restService);
        Timber.d("AzureAcnApiService: ");
        mAccessKey = accessKey;
        mHost = host;
    }

    @NonNull
    @Override
    protected String getPublisherTopic(String deviceType, String externalId) {
        Timber.v("getPublisherTopic: ");
        return "devices/" + mGatewayId + "/messages/events/";
    }

    @NonNull
    @Override
    protected String getHost() {
        Timber.v("getHost: ");
        return sslPrefix + mHost + sslPortSuffix;
    }

    @Override
    public boolean hasBatchMode() {
        return true;
    }

    @Override
    protected MqttConnectOptions getMqttOptions() {
        Timber.v("getMqttOptions: ");
        MqttConnectOptions options = super.getMqttOptions();
        try {
            DeviceClientConfig config = new DeviceClientConfig(mHost, mGatewayId, mAccessKey, null);
            IotHubSasToken sasToken = new IotHubSasToken(config, System.currentTimeMillis() / 1000l +
                    config.getTokenValidSecs() + 1l);
            options.setCleanSession(false);
            String clientIdentifier = "DeviceClientType="
                    + URLEncoder.encode(TransportUtils.getJavaServiceClientIdentifier()
                    + TransportUtils.getServiceVersion(), "UTF-8");
            String iotHubUserName = config.getIotHubHostname() + "/" + config.getDeviceId() + "/" + clientIdentifier;
            options.setUserName(iotHubUserName);
            options.setPassword(sasToken.toString().toCharArray());
        } catch (URISyntaxException e) {
            reportError(e);
        } catch (UnsupportedEncodingException e) {
            reportError(e);
        }
        return options;
    }

    @Override
    protected String getClientId() {
        Timber.v("getClientId: ");
        return mGatewayId;
    }

    @NonNull
    @Override
    protected String getSubscribeTopic() {
        String res = "devices/" + mGatewayId + "/messages/devicebound/#";
        Timber.v("getSubscribeTopic: %s", res);
        return res;
    }

    private void reportError(@NonNull Exception e) {
        Timber.e(e);
    }
}
