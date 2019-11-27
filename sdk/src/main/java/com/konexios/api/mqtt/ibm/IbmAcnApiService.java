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

package com.konexios.api.mqtt.ibm;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.konexios.api.listeners.ServerCommandsListener;
import com.konexios.api.models.ConfigResponse;
import com.konexios.api.mqtt.AbstractMqttAcnApiService;
import com.konexios.api.mqtt.common.NoSSLv3SocketFactory;
import com.konexios.api.rest.RestApiService;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import timber.log.Timber;

/**
 * ibm telemetry sender
 */

@Keep
public final class IbmAcnApiService extends AbstractMqttAcnApiService {

    private static final String IOT_ORGANIZATION_SSL = ".messaging.internetofthings.ibmcloud.com:8883";
    private static final String IOT_DEVICE_USERNAME = "use-token-auth";

    public IbmAcnApiService(String gatewayId,
                            ConfigResponse configResponse,
                            ServerCommandsListener listener,
                            RestApiService restService) {
        super(gatewayId, configResponse, listener, restService);
    }

    @Override
    protected String getPublisherTopic(String deviceType, String externalId) {
        Timber.v("getPublisherTopic: %s, %s", deviceType, externalId);
        return String.format("iot-2/type/%s/id/%s/evt/telemetry/fmt/json", deviceType, externalId);
    }

    @Override
    protected MqttConnectOptions getMqttOptions() {
        Timber.d("getMqttOptions");
        MqttConnectOptions options = super.getMqttOptions();
        options.setCleanSession(true);
        options.setUserName(IOT_DEVICE_USERNAME);
        options.setPassword(this.mConfigResponse.getIbm().getAuthToken().toCharArray());
        try {
            options.setSocketFactory(new NoSSLv3SocketFactory());
        } catch (Exception e) {
            Timber.e(e);
        }
        return options;
    }

    @NonNull
    @Override
    protected String getHost() {
        String host = "ssl://" + this.mConfigResponse.getIbm().getOrganicationId() + IOT_ORGANIZATION_SSL;
        Timber.d("getHost: %s", host);
        return host;
    }

    @NonNull
    @Override
    protected String getClientId() {
        ConfigResponse.Ibm ibm = this.mConfigResponse.getIbm();
        String res = "g:" + ibm.getOrganicationId() + ":" + ibm.getGatewayType() + ":" + ibm.getGatewayId();
        Timber.v("getClientId: %s", res);
        return res;
    }

    @Override
    public boolean hasBatchMode() {
        Timber.v("hasBatchMode: ");
        return false;
    }
}
