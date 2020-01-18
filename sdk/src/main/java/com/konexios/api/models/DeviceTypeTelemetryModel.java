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

package com.konexios.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by osminin on 12.10.2016.
 */

final public class DeviceTypeTelemetryModel implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DeviceTypeTelemetryModel> CREATOR = new Parcelable.Creator<DeviceTypeTelemetryModel>() {
        @NonNull
        @Override
        public DeviceTypeTelemetryModel createFromParcel(@NonNull Parcel in) {
            return new DeviceTypeTelemetryModel(in);
        }

        @NonNull
        @Override
        public DeviceTypeTelemetryModel[] newArray(int size) {
            return new DeviceTypeTelemetryModel[size];
        }
    };
    @SerializedName("variables")
    @Expose
    private Map<String, String> variables = new HashMap<>();
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;

    public DeviceTypeTelemetryModel() {

    }

    protected DeviceTypeTelemetryModel(@NonNull Parcel in) {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            String value = in.readString();
            variables.put(key, value);
        }
        description = in.readString();
        name = in.readString();
        type = in.readString();
    }

    /**
     * @return The variables
     */
    public Map<String, String> getVariables() {
        return variables;
    }

    /**
     * @param variables The variables
     */
    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceTypeTelemetryModel that = (DeviceTypeTelemetryModel) o;
        return Objects.equals(variables, that.variables) &&
                Objects.equals(description, that.description) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables, description, name, type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(variables.size());
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(type);
    }
}