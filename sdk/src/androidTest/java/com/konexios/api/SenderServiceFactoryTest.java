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

package com.konexios.api;

import com.konexios.api.common.RetrofitHolder;
import com.konexios.api.fakes.FakeRestService;
import com.konexios.api.fakes.FakeRetrofitHolder;
import com.konexios.api.listeners.ServerCommandsListener;
import com.konexios.api.models.ConfigResponse;
import com.konexios.api.mqtt.AbstractMqttAcnApiService;
import com.konexios.api.mqtt.MqttAcnApiService;
import com.konexios.api.mqtt.aws.AwsAcnApiService;
import com.konexios.api.mqtt.azure.AzureAcnApiService;
import com.konexios.api.mqtt.ibm.IbmAcnApiService;
import com.konexios.api.rest.RestApiService;

import org.junit.Before;
import org.junit.Test;

import static com.konexios.api.fakes.FakeData.AUTH_METHOD;
import static com.konexios.api.fakes.FakeData.AUTH_TOKEN;
import static com.konexios.api.fakes.FakeData.AWS_HOST;
import static com.konexios.api.fakes.FakeData.AWS_PORT;
import static com.konexios.api.fakes.FakeData.AWS_PRIVATE_KEY;
import static com.konexios.api.fakes.FakeData.AZURE_ACCESS_KEY;
import static com.konexios.api.fakes.FakeData.AZURE_HOST;
import static com.konexios.api.fakes.FakeData.CA_CERT;
import static com.konexios.api.fakes.FakeData.CLIENT_CERT;
import static com.konexios.api.fakes.FakeData.GATEWAY_HID;
import static com.konexios.api.fakes.FakeData.GATEWAY_TYPE;
import static com.konexios.api.fakes.FakeData.GATEWAY_UID;
import static com.konexios.api.fakes.FakeData.IBM_GATEWAY_ID;
import static com.konexios.api.fakes.FakeData.MQTT_HOST;
import static com.konexios.api.fakes.FakeData.MQTT_PREFIX;
import static com.konexios.api.fakes.FakeData.ORGANIZATION_ID;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public final class SenderServiceFactoryTest {

    private SenderServiceFactory mFactory;

    @Before
    public void setUp() throws Exception {
        mFactory = new SenderServiceFactoryImpl();
    }

    @Test
    public void test_arrowconnect() throws Exception {
        String cloud = "ArrowConnect";
        final ConfigResponse response = new ConfigResponse();
        response.setCloudPlatform(cloud);
        TelemetrySenderInterface sender = getTelemetrySenderInterface(response);
        assertNotNull(sender);
        assertThat(sender, instanceOf(MqttAcnApiService.class));
        assertThat(sender, instanceOf(AbstractMqttAcnApiService.class));
        assertThat(sender, instanceOf(AbstractTelemetrySenderService.class));
        assertThat(sender, instanceOf(TelemetrySenderInterface.class));
    }

    @Test
    public void test_ibm() throws Exception {
        String cloud = "IBM";
        ConfigResponse.Ibm ibm = new ConfigResponse.Ibm();
        ibm.setAuthMethod(AUTH_METHOD);
        ibm.setAuthToken(AUTH_TOKEN);
        ibm.setGatewayId(IBM_GATEWAY_ID);
        ibm.setGatewayType(GATEWAY_TYPE);
        ibm.setOrganicationId(ORGANIZATION_ID);
        final ConfigResponse response = new ConfigResponse();
        response.setCloudPlatform(cloud);
        response.setIbm(ibm);
        TelemetrySenderInterface sender = getTelemetrySenderInterface(response);
        assertNotNull(sender);
        assertThat(sender, instanceOf(IbmAcnApiService.class));
        assertThat(sender, instanceOf(AbstractMqttAcnApiService.class));
        assertThat(sender, instanceOf(AbstractTelemetrySenderService.class));
        assertThat(sender, instanceOf(TelemetrySenderInterface.class));
    }

    @Test
    public void test_aws() throws Exception {
        String cloud = "AwS";
        ConfigResponse.Aws aws = new ConfigResponse.Aws();
        aws.setCaCert(CA_CERT);
        aws.setClientCert(CLIENT_CERT);
        aws.setHost(AWS_HOST);
        aws.setPort(AWS_PORT);
        aws.setPrivateKey(AWS_PRIVATE_KEY);
        final ConfigResponse response = new ConfigResponse();
        response.setCloudPlatform(cloud);
        response.setAws(aws);
        TelemetrySenderInterface sender = getTelemetrySenderInterface(response);
        assertNotNull(sender);
        assertThat(sender, instanceOf(AwsAcnApiService.class));
        assertThat(sender, instanceOf(AbstractMqttAcnApiService.class));
        assertThat(sender, instanceOf(AbstractTelemetrySenderService.class));
        assertThat(sender, instanceOf(TelemetrySenderInterface.class));
    }

    @Test
    public void test_azure() throws Exception {
        String cloud = "Azure";
        ConfigResponse.Azure azure = new ConfigResponse.Azure();
        azure.setHost(AZURE_HOST);
        azure.setAccessKey(AZURE_ACCESS_KEY);
        final ConfigResponse response = new ConfigResponse();
        response.setCloudPlatform(cloud);
        response.setAzure(azure);
        TelemetrySenderInterface sender = getTelemetrySenderInterface(response);
        assertNotNull(sender);
        assertThat(sender, instanceOf(AzureAcnApiService.class));
        assertThat(sender, instanceOf(AbstractMqttAcnApiService.class));
        assertThat(sender, instanceOf(AbstractTelemetrySenderService.class));
        assertThat(sender, instanceOf(TelemetrySenderInterface.class));
    }

    private TelemetrySenderInterface getTelemetrySenderInterface(final ConfigResponse response) {
        TelemetrySenderInterface sender = mFactory.createTelemetrySender(new SenderServiceArgsProvider() {
            @Override
            public RetrofitHolder getRetrofitHolder() {
                return new FakeRetrofitHolder(new FakeRestService());
            }

            @Override
            public ConfigResponse getConfigResponse() {
                return response;
            }

            @Override
            public ServerCommandsListener getServerCommandsListener() {
                return null;
            }

            @Override
            public String getGatewayUid() {
                return GATEWAY_UID;
            }

            @Override
            public String getGatewayId() {
                return GATEWAY_HID;
            }

            @Override
            public String getMqttHost() {
                return MQTT_HOST;
            }

            @Override
            public String getMqttPrefix() {
                return MQTT_PREFIX;
            }

            @Override
            public RestApiService getIotConnectApiService() {
                return new FakeRestService();
            }
        });
        return sender;
    }
}
