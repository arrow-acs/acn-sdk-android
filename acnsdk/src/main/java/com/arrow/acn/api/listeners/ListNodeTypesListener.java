package com.arrow.acn.api.listeners;

import com.arrow.acn.api.models.ApiError;
import com.arrow.acn.api.models.ListResultModel;
import com.arrow.acn.api.models.NodeTypeModel;

/**
 * Created by osminin on 12.10.2016.
 */

public interface ListNodeTypesListener {
    void onListNodeTypesSuccess(ListResultModel<NodeTypeModel> result);

    void onListNodeTypesFiled(ApiError error);
}
