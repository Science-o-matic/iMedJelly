package com.science_o_matic.imedjelly.data;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.stream.JsonReader;

public class TaskLoader extends AsyncTask<TaskLoader.Task, Integer, TaskLoader.Task[]> {
	private ApiClient mApi = null;
	
	public ApiClient getApiClient() {
		return mApi;
	}
	
	public class Task {
		public final static String SUCCESS = "SUCCESS";

		private HttpRequestBase request;
		private JsonObject object;
		public String result;
		public int status;

		public Task(HttpRequestBase request, JsonObject object) {
			this.request = request;
			this.object = object;
			this.result = null;
		}
	};

	public TaskLoader(Context context) {
		mApi = new ApiClient(context);
	}

	@Override
	protected Task[] doInBackground(Task... tasks) {
		for(int i=0; i<tasks.length; i++) {
			try {
				// Perform request.
				HttpResponse response = mApi.performRequest(tasks[i].request);
				StatusLine statusLine = response.getStatusLine();
				tasks[i].status = statusLine.getStatusCode();
				if (tasks[i].status == HttpStatus.SC_OK) {
					// Get body.
					HttpEntity entity = response.getEntity();
					InputStream inStream = entity.getContent();
					BufferedInputStream bufferedStream = new BufferedInputStream(inStream);
					InputStreamReader streamReader = new InputStreamReader(bufferedStream, "UTF-8");
					// Parse JSON.
					JsonReader reader = new JsonReader(streamReader);
					try {
						tasks[i].object.parseJson(reader);
						tasks[i].result = Task.SUCCESS;
					}
					finally {
						reader.close();
					}
				}
				else {
					tasks[i].result =  String.valueOf(tasks[i].status);
				}
			}
			catch (Exception e) {
				tasks[i].result =  e.getLocalizedMessage();
			}
		}
		return tasks;
	}

	@Override
	protected void onPostExecute(Task[] results){
	}

	public String getResult(Task[] tasks) {
		if (tasks != null) {
			for(Task task: tasks) {
				if(!task.result.equals(Task.SUCCESS)) {
					return task.result;
				}
			}
		}
		return Task.SUCCESS;
	}
}
