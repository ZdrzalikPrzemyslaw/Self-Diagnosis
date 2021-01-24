package tech.szymanskazdrzalik.self_diagnosis.api;

import androidx.annotation.Nullable;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;

public class MakeCovidRequest extends DiagnoseRequest {

    private final Response.Listener<JSONObject> succesListener;

    public MakeCovidRequest(ChatActivity chatActivity, @Nullable String userAnswer) {
        super(chatActivity, userAnswer);
        this.succesListener = response -> {
            System.out.println("Odpowiedz" + response);
            boolean shouldStop = false;
            try {
                try {
                    shouldStop = response.getBoolean("should_stop");
                    RequestUtil.getInstance().setConditionsArray(response.getJSONArray("conditions"));

                    if (shouldStop) {
                        new CovidTriageRequest(chatActivity);
                    }
                } catch (JSONException e) {
                    // TODO: 16.12.2020 To znaczy że nie znaleziono pola should_stop, zrobić coś mądrego z tym
                    e.printStackTrace();
                }
                if (!shouldStop) {
                    JSONObject jsonObjectQuestion = response.getJSONObject("question");
                    listener.onDoctorMessage(jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("name"));
                    listener.hideMessageBox();
                    listener.onDoctorQuestionReceived(jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("id"),
                            jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getJSONArray("choices"),
                            jsonObjectQuestion.getJSONArray("items").getJSONObject(0).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        String url = InfermedicaApiClass.getInstance(chatActivity).getUrl() + "/covid19" + "/diagnosis";
        try {
            this.addAgeToRequestBody(RequestUtil::addAgeToJsonObjectForCovid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, this.getHeaders(),
                this.getRequestBody(), this.getSuccessListener(), this.getErrorListener()));
    }

    public MakeCovidRequest(ChatActivity chatActivity) {
        this(chatActivity, null);
        System.out.println("Wszedles do MakeCovidRequest po super");
    }

    @Override
    protected Response.Listener<JSONObject> getSuccessListener() {
        return this.succesListener;
    }

    private static class CovidTriageRequest extends DiagnoseRequest {

        public CovidTriageRequest(ChatActivity chatActivity) {
            super(chatActivity);
            String url = InfermedicaApiClass.getInstance(chatActivity).getUrl() + "/covid19" + "/triage";
            try {
                this.addAgeToRequestBody(RequestUtil::addAgeToJsonObjectForCovid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, this.getHeaders(),
                    this.getRequestBody(), this.getSuccessListener(), this.getErrorListener()));
        }

        @Override
        protected Response.Listener<JSONObject> getSuccessListener() {
            return response -> {
                System.out.println("Odpowiedz" + response);
                RequestUtil.getInstance().setConditionsArray(new JSONArray().put(response));
                listener.finishCovidDiagnose();
            };
        }

    }

}

