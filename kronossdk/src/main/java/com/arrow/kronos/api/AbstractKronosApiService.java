package com.arrow.kronos.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.arrow.kronos.api.common.ApiRequestSigner;
import com.arrow.kronos.api.common.RetrofitHolder;
import com.arrow.kronos.api.listeners.DeviceActionTypesListener;
import com.arrow.kronos.api.listeners.DeviceActionsListener;
import com.arrow.kronos.api.listeners.DeviceHistoricalEventsListener;
import com.arrow.kronos.api.listeners.PostDeviceActionListener;
import com.arrow.kronos.api.listeners.RegisterAccountListener;
import com.arrow.kronos.api.listeners.RegisterDeviceListener;
import com.arrow.kronos.api.listeners.ServerCommandsListener;
import com.arrow.kronos.api.listeners.UpdateDeviceActionListener;
import com.arrow.kronos.api.models.AccountRequest;
import com.arrow.kronos.api.models.AccountResponse;
import com.arrow.kronos.api.models.ActionModel;
import com.arrow.kronos.api.models.ActionResponseModel;
import com.arrow.kronos.api.models.ActionTypeResponseModel;
import com.arrow.kronos.api.models.ConfigResponse;
import com.arrow.kronos.api.models.GatewayModel;
import com.arrow.kronos.api.models.GatewayResponse;
import com.arrow.kronos.api.models.GatewayType;
import com.arrow.kronos.api.models.HistoricalEventResponse;
import com.arrow.kronos.api.models.RegisterDeviceRequest;
import com.arrow.kronos.api.rest.IotConnectAPIService;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by osminin on 6/17/2016.
 */

public abstract class AbstractKronosApiService implements KronosApiService {
    private static final String TAG = AbstractKronosApiService.class.getName();

    private Context mContext;
    private IotConnectAPIService mService;
    private Gson mGson = new Gson();
    protected Handler mServiceThreadHandler;
    private GatewayRegisterListener mGatewayRegisterListener;
    private String mGatewayId;
    protected ServerCommandsListener mServerCommandsListener;

    private Callback<GatewayResponse> mGatewayResponseCallback = new Callback<GatewayResponse>() {
        @Override
        public void onResponse(Call<GatewayResponse> call, final Response<GatewayResponse> response) {
            mServiceThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onGatewayResponse(response);
                }
            });
        }

        @Override
        public void onFailure(Call<GatewayResponse> call, Throwable t) {
            FirebaseCrash.report(t);
        }
    };

    private Callback<ConfigResponse> mConfigResponseCallback = new Callback<ConfigResponse>() {
        @Override
        public void onResponse(Call<ConfigResponse> call, final Response<ConfigResponse> response) {
            mServiceThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    onConfigResponse(response);
                }
            });
        }

        @Override
        public void onFailure(Call<ConfigResponse> call, Throwable t) {
            FirebaseCrash.report(t);
        }
    };

    private Runnable mHeartBeatTask = new Runnable() {

        @Override
        public void run() {
            Call<ResponseBody> request = mService.heartBeat(mGatewayId);
            request.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    FirebaseCrash.logcat(Log.VERBOSE, TAG, "heartBeat onResponse: " + response.code());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    FirebaseCrash.logcat(Log.VERBOSE, TAG, "heartBeat onFailure");
                    FirebaseCrash.report(t);
                }
            });
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            int heartBeatInterval = sp.getInt(Constants.SP_CLOUD_HERATBEAT_INTERVAL, Constants.HEART_BEAT_INTERVAL);
            mServiceThreadHandler.postDelayed(mHeartBeatTask, heartBeatInterval * 1000L);
        }
    };

    protected IotConnectAPIService getService() {
        return mService;
    }

    protected Gson getGson() {
        return mGson;
    }

    @Override
    public void setRestEndpoint(ServerEndpoint endpoint) {
        mService = RetrofitHolder.getIotConnectAPIService(endpoint);
    }

    @Override
    public void initialize(Context context) {
        mContext = context;
        mServiceThreadHandler = new Handler();
    }

    protected final void registerGateway(final GatewayRegisterListener listener) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mGatewayRegisterListener = listener;
        String gatewayHid = prefs.getString(Constants.Preference.KEY_GATEWAY_ID, null);
        if (TextUtils.isEmpty(gatewayHid)) {
            String uid = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            FirebaseCrash.logcat(Log.DEBUG, TAG, "registerGateway() UID: " + uid);

            String name = String.format("%s %s", Build.MANUFACTURER, Build.MODEL);
            String osName = String.format("Android %s", Build.VERSION.RELEASE);
            String swName = Constants.SOFTWARE_NAME;
            String userHid = prefs.getString(Constants.Preference.KEY_ACCOUNT_USER_ID, null);

            GatewayModel gatewayModel = new GatewayModel();
            gatewayModel.setName(name);
            gatewayModel.setOsName(osName);
            gatewayModel.setSoftwareName(swName);
            gatewayModel.setUid(uid);
            gatewayModel.setType(GatewayType.Mobile);
            gatewayModel.setSoftwareVersion(
                    String.format("%d.%d", Constants.MAJOR, Constants.MINOR));

            Call<GatewayResponse> call = mService.registerGateway(gatewayModel);
            call.enqueue(mGatewayResponseCallback);
        } else {
            Call<Void> call = mService.checkin(gatewayHid);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    FirebaseCrash.logcat(Log.VERBOSE, TAG, "checkin onResponse: " + response.code());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    FirebaseCrash.logcat(Log.VERBOSE, TAG, "checkin onFailure");
                    FirebaseCrash.report(t);
                }
            });
            getConfig(gatewayHid, listener);
        }
    }

    private void getConfig(final String gatewayId, final GatewayRegisterListener listener) {
        Call<ConfigResponse> call = mService.getConfig(gatewayId);
        mGatewayRegisterListener = listener;
        mGatewayId = gatewayId;
        call.enqueue(mConfigResponseCallback);
    }

    private void onGatewayResponse(Response<GatewayResponse> response) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "registerGateway() onResponse: " + response.code());
        if (response.body() != null && response.code() == HttpURLConnection.HTTP_OK) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            prefs.edit().putString(Constants.Preference.KEY_GATEWAY_ID, response.body().getHid()).commit();
            String gatewayHid = response.body().getHid();
            getConfig(gatewayHid, mGatewayRegisterListener);
            Call<Void> call = mService.checkin(gatewayHid);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    FirebaseCrash.logcat(Log.VERBOSE, TAG, "checkin onResponse: " + response.code());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    FirebaseCrash.logcat(Log.VERBOSE, TAG, "checkin onFailure");
                    FirebaseCrash.report(t);
                }
            });
        } else {
            FirebaseCrash.logcat(Log.ERROR, TAG, "Gateway registration failed: " + response.code());
        }
    }

    private void onConfigResponse(Response<ConfigResponse> response) {
        FirebaseCrash.logcat(Log.VERBOSE, TAG, "getConfig onResponse: " + response.code());
        if (response.body() != null && response.code() == HttpURLConnection.HTTP_OK) {
            ConfigResponse.Key keys = response.body().getKey();
            if (keys != null) {
                ApiRequestSigner.getInstance().setSecretKey(keys.getSecretKey());
                ApiRequestSigner.getInstance().apiKey(keys.getApiKey());
                if (mGatewayRegisterListener != null) {
                    mGatewayRegisterListener.onGatewayRegistered(mGatewayId);
                }
            }
            ConfigResponse.Aws awsData = response.body().getAws();
            ConfigResponse.Ibm ibmData = response.body().getIbm();
            if (awsData != null || ibmData != null) {
                if (mGatewayRegisterListener != null) {
                    mGatewayRegisterListener.onGatewayRegistered(response.body());
                }
            }
            startSendHeartBeat();
        }
    }

    private void startSendHeartBeat() {
        mServiceThreadHandler.post(mHeartBeatTask);
    }

    protected String formatBatchPayload(List<Bundle> telemetry) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Bundle bundle : telemetry) {
            String json = bundle.getString(Constants.EXTRA_DATA_LABEL_TELEMETRY);
            builder.append(json).append(",");
        }
        builder.replace(builder.length() - 1, builder.length(), "").append("]");
        return builder.toString();
    }

    @Override
    public void setServerCommandsListener(ServerCommandsListener listener) {
        mServerCommandsListener = listener;
    }

    @Override
    public void registerAccount(AccountRequest accountRequest, final RegisterAccountListener listener) {
        Call<AccountResponse> call = mService.registerAccount(accountRequest);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Log.v(TAG, "onResponse: " + response.code());
                try {
                    if (response.body() != null && response.code() == HttpURLConnection.HTTP_OK) {
                        listener.onAccountRegistered(response.body());
                    } else {
                        String code = Integer.toString(response.code());
                        listener.onAccountRegisterFailed(code);
                        Log.v(TAG, "data sent to cloud: " + code);
                    }
                } catch (Exception e) {
                    listener.onAccountRegisterFailed(e.toString());
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                Log.v(TAG, "onFailure: " + t.toString());
                listener.onAccountRegisterFailed(t.toString());
                FirebaseCrash.logcat(Log.ERROR, TAG, "postDelayed() failed");
                FirebaseCrash.report(t);
            }
        });
    }

    @Override
    public void getDeviceActionTypes(final DeviceActionTypesListener listener) {
        mService.getActionTypes().enqueue(new Callback<ActionTypeResponseModel>() {
            @Override
            public void onResponse(Call<ActionTypeResponseModel> call, Response<ActionTypeResponseModel> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "getActionTypes response");
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                    listener.onActionTypesReceived(response.body());
                } else {
                    listener.onActionTypesFailed("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ActionTypeResponseModel> call, Throwable t) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "getActionTypes error");
                listener.onActionTypesFailed("Fatal error");
            }
        });
    }

    @Override
    public void getDeviceActions(String deviceHid, final DeviceActionsListener listener) {
        mService.getActions(deviceHid).enqueue(new Callback<ActionResponseModel>() {
            @Override
            public void onResponse(Call<ActionResponseModel> call, Response<ActionResponseModel> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "getActions response");
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                    listener.onDeviceActionsReceived(response.body());
                } else {
                    listener.onDeviceActionsFailed("Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ActionResponseModel> call, Throwable t) {
                listener.onDeviceActionsFailed("fatal error");
            }
        });
    }

    @Override
    public void postDeviceAction(String deviceHid, ActionModel action, final PostDeviceActionListener listener) {
        mService.postAction(deviceHid, action).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "getActionTypes response");
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                    listener.postActionSucceed();
                } else {
                    listener.postActionFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.postActionFailed();
            }
        });
    }

    @Override
    public void updateDeviceAction(String deviceHid, int index, ActionModel model, final UpdateDeviceActionListener listener) {
        mService.updateAction(deviceHid, index, model).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "getActionTypes response");
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                    listener.onDeviceActionUpdated();
                } else {
                    listener.onDeviceActionUpdateFailed();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onDeviceActionUpdateFailed();
            }
        });
    }

    @Override
    public void getDeviceHistoricalEvents(String deviceHid, final DeviceHistoricalEventsListener listener) {
        mService.getHistoricalEvents(deviceHid).enqueue(new Callback<HistoricalEventResponse>() {
            @Override
            public void onResponse(Call<HistoricalEventResponse> call, Response<HistoricalEventResponse> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "getHistoricalEvents response");
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                    listener.onHistoricalEventsReceived(response.body());
                } else {
                    listener.onHistoricalEventsFailed("Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<HistoricalEventResponse> call, Throwable t) {
                listener.onHistoricalEventsFailed("Fatal error");
            }
        });
    }

    @Override
    public void registerDevice(RegisterDeviceRequest req, final RegisterDeviceListener listener) {
        mService.registerDevice(req).enqueue(new Callback<GatewayResponse>() {
            @Override
            public void onResponse(Call<GatewayResponse> call, Response<GatewayResponse> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "registerDevice response");
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                    listener.onDeviceRegistered(response.body());
                } else {
                    listener.onDeviceRegistrationFailed();
                }
            }

            @Override
            public void onFailure(Call<GatewayResponse> call, Throwable t) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "registerDevice error");
                listener.onDeviceRegistrationFailed();
            }
        });
    }

    @Override
    public void registerReceivedEvent(String eventHid) {
        mService.putReceived(eventHid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "registerReceivedEvent response");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "registerReceivedEvent error");
            }
        });
    }

    @Override
    public void eventHandlingSucceed(String eventHid) {
        mService.putSucceeded(eventHid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "eventHandlingSucceed response");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "eventHandlingSucceed error");
            }
        });
    }

    @Override
    public void eventHandlingFailed(String eventHid) {
        mService.putFailed(eventHid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                FirebaseCrash.logcat(Log.DEBUG, TAG, "eventHandlingSucceed response");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "eventHandlingSucceed error");
            }
        });
    }

    public interface GatewayRegisterListener {
        void onGatewayRegistered(String gatewayHid);

        void onGatewayRegistered(ConfigResponse aws);
    }
}