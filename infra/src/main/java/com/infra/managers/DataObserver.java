package com.infra.managers;

import com.infra.managers.responses.ServiceResponse;

public interface DataObserver<T> {
    /**
     * @param collection
     */
    public void notifyDataChanged(ServiceResponse<T> collection);
}
