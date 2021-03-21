package com.oschain.fastchaindb.client.common.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.oschain.fastchaindb.client.common.utils.AbstractApi.JSON;


/**
 * The Class NetworkUtils.
 */
public class NetworkUtils {

	/**
	 *  The Constant JSON.
	 *
	 * @param url the url
	 * @param body the body
	 * @param callback the callback
	 */
	

	/**
	 * Send post request.
	 *
	 * @param url the url
	 * @param transaction the transaction
	 * @param callback the callback
	 */
	public static void sendPostRequest(String url, String jsonBody,
			final GenericCallback callback) {

		RequestBody body = RequestBody.create(JSON, jsonBody);
		Request request = new Request.Builder().url(url).post(body).build();
		getOkHttpClient().newCall(request).enqueue(new Callback() {

			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			public void onResponse(Call call, Response response) {
				if (response.code() == 202) {
					callback.pushedSuccessfully(response);
				} else if (response.code() == 400) {
					callback.transactionMalformed(response);
				} else {
					callback.otherError(response);
				}
				response.close();
			}
		});
	}
	
	/**
	 * Send post request.
	 *
	 * @param url the url
	 * @param body the body
	 * @return the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Response sendPostRequest(String url, String jsonBody) throws IOException {
		try {
			RequestBody body = RequestBody.create(JSON, jsonBody);
			Request request = new Request.Builder().url(url).post(body).build();
			return getOkHttpClient().newCall(request).execute();
		}catch (Exception ex){
			System.out.println(ex);
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Send get request.
	 *
	 * @param url the url
	 * @return the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Response sendGetRequest(String url) throws IOException {
		Request request = new Request.Builder().url(url).get().build();
		return getOkHttpClient().newCall(request).execute();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NetworkUtils []";
	}


	public static OkHttpClient getOkHttpClient(){
		OkHttpClient httpClient= new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS).build();
		return httpClient;
	}

}
