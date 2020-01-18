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

package com.konexios.api.mqtt.aws;

import androidx.annotation.Keep;

import com.konexios.api.listeners.ServerCommandsListener;
import com.konexios.api.models.ConfigResponse;
import com.konexios.api.mqtt.AbstractMqttAcnApiService;
import com.konexios.api.mqtt.common.SslUtil;
import com.konexios.api.rest.RestApiService;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import timber.log.Timber;

/**
 * aws telemetry sender
 */

@Keep
public final class AwsAcnApiService extends AbstractMqttAcnApiService {

    public AwsAcnApiService(String gatewayId,
                            ConfigResponse configResponse,
                            ServerCommandsListener listener,
                            RestApiService restService) {
        super(gatewayId, configResponse, listener, restService);
        Timber.v("AwsAcnApiService: ");
    }

    @Override
    protected MqttConnectOptions getMqttOptions() {
        Timber.d("getMqttOptions");
        MqttConnectOptions options = super.getMqttOptions();
        String rootCert = this.mConfigResponse.getAws().getCaCert();
        String clientCert = this.mConfigResponse.getAws().getClientCert();
        String privateKey = this.mConfigResponse.getAws().getPrivateKey();
        try {
            options.setSocketFactory(SslUtil.getSocketFactory(rootCert, clientCert, privateKey));
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e(e);
        }
        return options;
    }

    @Override
    protected String getPublisherTopic(String deviceType, String externalId) {
        Timber.d("getPublisherTopic");
        return String.format("telemetries/devices/%s", mGatewayId);
    }

    @Override
    protected String getHost() {
        Timber.d("getHost");
        return "ssl://".concat(this.mConfigResponse.getAws().getHost());
    }

    @Override
    public boolean hasBatchMode() {
        return false;
    }
}
