/*
 * Copyright (c) 2017 Arrow Electronics, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License 2.0
 * which accompanies this distribution, and is available at
 * http://apache.org/licenses/LICENSE-2.0
 *
 * Contributors: Arrow Electronics, Inc.
 */

package com.arrow.acn.api;


import com.arrow.acn.api.common.RetrofitHolderImpl;

/**
 * Created by osminin on 9/21/2016.
 */

public final class AcnApiServiceFactory {

    private static AcnApiService service;

    public static AcnApiService createAcnApiService() {
        service = new AcnApiImpl(new RetrofitHolderImpl(),
                new SenderServiceFactoryImpl());
        return service;
    }

    public static AcnApiService getAcnApiService() {
        return service;
    }
}
