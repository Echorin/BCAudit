package com.oschain.fastchaindb.client.common.utils;

public interface HttpCallback {
    /**
     * The pushed transaction was accepted in the BACKLOG, but the processing has not been completed.
     *
     * @param response the response
     */
    void success(String response);

    /**
     * All other errors, including server malfunction or network error.
     *
     * @param response the response
     */
    void error(String response);
}
