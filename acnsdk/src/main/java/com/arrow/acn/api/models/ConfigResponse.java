/*
 * Copyright (c) 2017 Arrow Electronics, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License 2.0
 * which accompanies this distribution, and is available at
 * http://apache.org/licenses/LICENSE-2.0
 *
 * Contributors: Arrow Electronics, Inc.
 */

package com.arrow.acn.api.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by osminin on 4/14/2016.
 */
public final class ConfigResponse implements Parcelable {
    @SerializedName("cloudPlatform")
    private String mCloudPlatform;
    @SerializedName("key")
    private Key mKey;
    @SerializedName("aws")
    private Aws mAws;
    @SerializedName("ibm")
    private Ibm mIbm;
    @SerializedName("azure")
    private Azure mAzure;

    public ConfigResponse() {
    }

    public Ibm getIbm() {
        return mIbm;
    }

    public void setIbm(Ibm ibm) {
        mIbm = ibm;
    }

    public CloudPlatform getCloudPlatform() {
        return CloudPlatform.getPlatform(mCloudPlatform);
    }

    public void setCloudPlatform(String cloudPlatform) {
        mCloudPlatform = cloudPlatform;
    }

    public Key getKey() {
        return mKey;
    }

    public void setKey(Key key) {
        mKey = key;
    }

    public Aws getAws() {
        return mAws;
    }

    public void setAws(Aws aws) {
        mAws = aws;
    }

    public Azure getAzure() {
        return mAzure;
    }

    public void setAzure(Azure azure) {
        mAzure = azure;
    }

    public static class Key implements Parcelable {
        @SerializedName("apiKey")
        private String mApiKey;
        @SerializedName("secretKey")
        private String mSecretKey;

        public String getApiKey() {
            return mApiKey;
        }

        public void setApiKey(String apiKey) {
            mApiKey = apiKey;
        }

        public String getSecretKey() {
            return mSecretKey;
        }

        public void setSecretKey(String secretKey) {
            mSecretKey = secretKey;
        }

        public Key() {
        }

        protected Key(@NonNull Parcel in) {
            mApiKey = in.readString();
            mSecretKey = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(mApiKey);
            dest.writeString(mSecretKey);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Key> CREATOR = new Parcelable.Creator<Key>() {
            @NonNull
            @Override
            public Key createFromParcel(@NonNull Parcel in) {
                return new Key(in);
            }

            @NonNull
            @Override
            public Key[] newArray(int size) {
                return new Key[size];
            }
        };
    }

    public static class Aws implements Parcelable {
        @SerializedName("host")
        private String mHost;
        @SerializedName("port")
        private String mPort;
        @SerializedName("caCert")
        private String mCaCert;
        @SerializedName("clientCert")
        private String mClientCert;
        @SerializedName("privateKey")
        private String mPrivateKey;

        public String getHost() {
            return mHost;
        }

        public void setHost(String host) {
            mHost = host;
        }

        public String getPort() {
            return mPort;
        }

        public void setPort(String port) {
            mPort = port;
        }

        public String getCaCert() {
            return mCaCert;
        }

        public void setCaCert(String caCert) {
            mCaCert = caCert;
        }

        public String getClientCert() {
            return mClientCert;
        }

        public void setClientCert(String clientCert) {
            mClientCert = clientCert;
        }

        public String getPrivateKey() {
            return mPrivateKey;
        }

        public void setPrivateKey(String privateKey) {
            mPrivateKey = privateKey;
        }

        protected Aws(@NonNull Parcel in) {
            mHost = in.readString();
            mPort = in.readString();
            mCaCert = in.readString();
            mClientCert = in.readString();
            mPrivateKey = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(mHost);
            dest.writeString(mPort);
            dest.writeString(mCaCert);
            dest.writeString(mClientCert);
            dest.writeString(mPrivateKey);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Aws> CREATOR = new Parcelable.Creator<Aws>() {
            @NonNull
            @Override
            public Aws createFromParcel(@NonNull Parcel in) {
                return new Aws(in);
            }

            @NonNull
            @Override
            public Aws[] newArray(int size) {
                return new Aws[size];
            }
        };
    }

    public static class Ibm implements Parcelable {
        @SerializedName("organizationId")
        private String mOrganicationId;
        @SerializedName("gatewayType")
        private String mGatewayType;
        @SerializedName("gatewayId")
        private String mGatewayId;
        @SerializedName("authMethod")
        private String mAuthMethod;
        @SerializedName("authToken")
        private String mAuthToken;

        public String getOrganicationId() {
            return mOrganicationId;
        }

        public void setOrganicationId(String organicationId) {
            mOrganicationId = organicationId;
        }

        public String getGatewayType() {
            return mGatewayType;
        }

        public void setGatewayType(String gatewayType) {
            mGatewayType = gatewayType;
        }

        public String getGatewayId() {
            return mGatewayId;
        }

        public void setGatewayId(String gatewayId) {
            mGatewayId = gatewayId;
        }

        public String getAuthMethod() {
            return mAuthMethod;
        }

        public void setAuthMethod(String authMethod) {
            mAuthMethod = authMethod;
        }

        public String getAuthToken() {
            return mAuthToken;
        }

        public void setAuthToken(String authToken) {
            mAuthToken = authToken;
        }

        public Ibm() {
        }

        protected Ibm(@NonNull Parcel in) {
            mOrganicationId = in.readString();
            mGatewayType = in.readString();
            mGatewayId = in.readString();
            mAuthMethod = in.readString();
            mAuthToken = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(mOrganicationId);
            dest.writeString(mGatewayType);
            dest.writeString(mGatewayId);
            dest.writeString(mAuthMethod);
            dest.writeString(mAuthToken);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Ibm> CREATOR = new Parcelable.Creator<Ibm>() {
            @NonNull
            @Override
            public Ibm createFromParcel(@NonNull Parcel in) {
                return new Ibm(in);
            }

            @NonNull
            @Override
            public Ibm[] newArray(int size) {
                return new Ibm[size];
            }
        };
    }

    public ConfigResponse(@NonNull Parcel in) {
        mCloudPlatform = in.readString();
        mKey = (Key) in.readValue(Key.class.getClassLoader());
        mAws = (Aws) in.readValue(Aws.class.getClassLoader());
        mIbm = (Ibm) in.readValue(Ibm.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mCloudPlatform);
        dest.writeValue(mKey);
        dest.writeValue(mAws);
        dest.writeValue(mIbm);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ConfigResponse> CREATOR = new Parcelable.Creator<ConfigResponse>() {
        @NonNull
        @Override
        public ConfigResponse createFromParcel(@NonNull Parcel in) {
            return new ConfigResponse(in);
        }

        @NonNull
        @Override
        public ConfigResponse[] newArray(int size) {
            return new ConfigResponse[size];
        }
    };

    public static class Azure implements Parcelable {
        @SerializedName("accessKey")
        private String accessKey;
        @SerializedName("host")
        private String host;

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        protected Azure(@NonNull Parcel in) {
            accessKey = in.readString();
            host = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(accessKey);
            dest.writeString(host);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Azure> CREATOR = new Parcelable.Creator<Azure>() {
            @NonNull
            @Override
            public Azure createFromParcel(@NonNull Parcel in) {
                return new Azure(in);
            }

            @NonNull
            @Override
            public Azure[] newArray(int size) {
                return new Azure[size];
            }
        };
    }

    public enum CloudPlatform {
        NONE (""),
        ARROW_CONNECT ("ARROWCONNECT"),
        IBM ("IBM"),
        AWS ("AWS"),
        AZURE ("AZURE");

        CloudPlatform(String str) {
            mString = str;
        }
        private String mString;

        public String getString() {
            return mString;
        }

        public static CloudPlatform getPlatform(String str) {
            CloudPlatform res = NONE;
            for (CloudPlatform tmp : CloudPlatform.values()) {
                if (tmp.getString().equalsIgnoreCase(str)) {
                    res = tmp;
                }
            }
            return res;
        }

        @Override
        public String toString() {
            return mString;
        }
    }
}