package tech.szymanskazdrzalik.self_diagnosis.api;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tech.szymanskazdrzalik.self_diagnosis.db.User;
import tech.szymanskazdrzalik.self_diagnosis.helpers.GlobalVariables;

public class MakeFirstRequest {

    private ApiClass apiClass;
    private ApiRequestQueue apiRequestQueue;
    private String url;

    public MakeFirstRequest(Context context, JSONArray jsonArray) {
        this.apiClass = ApiClass.getInstance(context);
        this.apiRequestQueue = ApiRequestQueue.getInstance(context);
        this.url = this.apiClass.getUrl() + "/diagnosis";

        GlobalVariables globalVariables = GlobalVariables.getInstance();
        User user = globalVariables.getCurrentUser();

        Map<String, String> headers = new HashMap<>();
        headers.put("App-Id", this.apiClass.getId());
        headers.put("App-Key", this.apiClass.getKey());
        headers.put("Content-Type", "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sex", user.getFullGenderName());
            JSONObject ageJson = new JSONObject();
            ageJson.put("value", user.getAge());
            jsonObject.put("age", ageJson);
            jsonObject.put("evidence", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);

        Response.Listener<JSONObject> listener = System.out::println;
        Response.ErrorListener errorListener = System.out::println;

        this.apiRequestQueue.addToRequestQueue(new JSONObjectRequestWithHeaders(1, this.url, headers, jsonObject, listener, errorListener));

    }
}
