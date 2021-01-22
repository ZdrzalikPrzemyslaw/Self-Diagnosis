package tech.szymanskazdrzalik.self_diagnosis.api;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import tech.szymanskazdrzalik.self_diagnosis.ChatActivity;

public class MakeCovidRequest extends DiagnoseRequest {
    public MakeCovidRequest(ChatActivity chatActivity, @Nullable String userAnswer) {
        super(chatActivity, userAnswer);

        String url = InfermedicaApiClass.getInstance(chatActivity).getUrl() + "/covid19" + "/diagnosis";
        try {
            this.addAgeToRequestBody(RequestUtil::addAgeToJsonObjectForCovid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequestQueue.getInstance(chatActivity).addToRequestQueue(new JSONObjectRequestWithHeaders(1, url, this.getHeaders(), this.getRequestBody(), this.getSuccessListener(), this.getErrorListener()));
    }

    public MakeCovidRequest(ChatActivity chatActivity) {
        this(chatActivity, null);
        System.out.println("Wszedles do MakeCovidRequest po super");
    }
}
