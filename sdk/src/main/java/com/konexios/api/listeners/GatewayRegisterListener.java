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

package com.konexios.api.listeners;

import com.konexios.api.models.ApiError;
import com.konexios.api.models.GatewayResponse;

/**
 * Created by osminin on 10/6/2016.
 */

public interface GatewayRegisterListener {
    void onGatewayRegistered(GatewayResponse response);

    void onGatewayRegisterFailed(ApiError error);
}
