package com.purplesq.purplesq.interfces;

public interface GenericAsyncTaskListener {
    public void genericAsyncTaskOnSuccess(Object obj);

    public void genericAsyncTaskOnError(Object obj);

    public void genericAsyncTaskOnProgress(Object obj);

    public void genericAsyncTaskOnCancelled(Object obj);
}
