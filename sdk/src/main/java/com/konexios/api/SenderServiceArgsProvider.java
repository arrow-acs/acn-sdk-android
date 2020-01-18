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
import com.konexios.api.listeners.ServerCommandsListener;
import com.konexios.api.models.ConfigResponse;
import com.konexios.api.rest.RestApiService;

public interface SenderServiceArgsProvider {

    RetrofitHolder getRetrofitHolder();

    ConfigResponse getConfigResponse();

    ServerCommandsListener getServerCommandsListener();

    String getGatewayUid();

    String getGatewayId();

    String getMqttHost();

    String getMqttPrefix();

    RestApiService getIotConnectApiService();
}