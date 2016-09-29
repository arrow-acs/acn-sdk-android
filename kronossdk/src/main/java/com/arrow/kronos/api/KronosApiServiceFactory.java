package com.arrow.kronos.api;

import com.arrow.kronos.api.mqtt.MqttKronosApiService;
import com.arrow.kronos.api.mqtt.aws.AwsKronosApiService;
import com.arrow.kronos.api.mqtt.ibm.IbmKronosApiService;
import com.arrow.kronos.api.rest.RestApiKronosApiService;


/**
 * Created by osminin on 9/21/2016.
 */

public final class KronosApiServiceFactory {

    private static KronosApiService service;

    public static KronosApiService createKronosApiService(ConnectionType type) {
        if (service == null) {
            switch (type) {
                case REST:
                    service = new RestApiKronosApiService();
                    break;
                case MQTT:
                    service = new MqttKronosApiService();
                    break;
                case AWS:
                    service = new AwsKronosApiService();
                    break;
                case IBM:
                    service = new IbmKronosApiService();
                    break;
            }
        }
        return service;
    }

    public static KronosApiService getKronosApiService() {
        return service;
    }
}