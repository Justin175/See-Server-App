package com.treulieb.worktimetool.req;

import com.treulieb.worktimetool.data.Bill;
import com.treulieb.worktimetool.data.Posten;

import org.json.JSONArray;
import org.json.JSONObject;

public class SeeServerResponses {

    public static abstract class BaseResponse {
        private JSONObject data;
        private boolean successful;
        private String token;
        private String message;

        public BaseResponse(JSONObject data) {
            this.data = data;
            this.successful = data.optBoolean("successful", false);
            this.token = data.optString("token", null);
            this.message = data.optString("msg");

            if(successful)
                onSetup(data);
            else
                onSetupError(data);
        }

        public boolean isSuccessful() {
            return successful;
        }

        public JSONObject getData() {
            return data;
        }

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }

        protected abstract void onSetup(JSONObject data);
        protected void onSetupError(JSONObject data) {}
    }

    public static class EmptyResponse extends BaseResponse {

        public EmptyResponse(JSONObject data) {
            super(data);
        }

        @Override
        protected void onSetup(JSONObject data) {

        }

        @Override
        protected void onSetupError(JSONObject data) {

        }
    }

    public static class ResponseVersion extends BaseResponse {
        private String version;

        public ResponseVersion(JSONObject data) {
            super(data);

            this.version = data.optString("version");
        }

        public String getVersion() {
            return version;
        }

        @Override
        protected void onSetup(JSONObject data) {

        }

        @Override
        protected void onSetupError(JSONObject data) {

        }
    }

    public static class ResponseLogout extends BaseResponse {

        public ResponseLogout(JSONObject data) {
            super(data);
        }

        @Override
        protected void onSetup(JSONObject data) {

        }

        @Override
        protected void onSetupError(JSONObject data) {

        }
    }

    public static class ResponseGetBills extends BaseResponse {
        private String[] billNames;

        public ResponseGetBills(JSONObject data) {
            super(data);
        }

        @Override
        protected void onSetup(JSONObject data) {
            JSONArray bills = data.optJSONArray("bills");
            this.billNames = new String[bills.length()];
            for(int i = 0; i < bills.length(); i++)
                this.billNames[i] = bills.optString(i);
        }

        @Override
        protected void onSetupError(JSONObject data) {

        }

        public String[] getBillNames() {
            return billNames;
        }
    }

    public static class ResponseLogin extends EmptyResponse {

        public ResponseLogin(JSONObject data) {
            super(data);
        }
    }

    public static class ResponseCreateUser extends EmptyResponse {

        public ResponseCreateUser(JSONObject data) {
            super(data);
        }

    }

    public static class ResponseCreateBill extends EmptyResponse {

        public ResponseCreateBill(JSONObject data) {
            super(data);
        }
    }

    public static class ResponseDeleteBill extends EmptyResponse {

        public ResponseDeleteBill(JSONObject data) {
            super(data);
        }
    }

    public static class ResponseGetUser extends BaseResponse {
        private String[] userNames;

        public ResponseGetUser(JSONObject data) {
            super(data);
        }

        @Override
        protected void onSetup(JSONObject data) {
            JSONArray users = data.optJSONArray("users");
            this.userNames = new String[users.length()];
            for(int i = 0; i < users.length(); i++)
                this.userNames[i] = users.optString(i);
        }

        @Override
        protected void onSetupError(JSONObject data) {

        }

        public String[] getUserNames() {
            return userNames;
        }
    }

    public static class ResponseGetBill extends BaseResponse {
        private Bill bill;

        public ResponseGetBill(JSONObject data) {
            super(data);
        }

        @Override
        protected void onSetup(JSONObject data) {
            JSONObject billData = data.optJSONObject("bill");

            this.bill = new Bill(
                    billData.optString("name"),
                    new Bill.BillUser(billData.optString("creator"), Bill.BillPrivilege.values()),
                    billData.optString("created"),
                    Posten.fromJSON(billData.optJSONObject("posten")),
                    Bill.BillUser.fromJSON(billData.optJSONObject("users")),
                    (float) billData.optDouble("costs_sum")
            );
        }

        public Bill getBill() {
            return bill;
        }
    }

    public static class ResponseGetPosten extends BaseResponse {
        private Posten[] posten;

        public ResponseGetPosten(JSONObject data) {
            super(data);
        }

        @Override
        protected void onSetup(JSONObject data) {
            this.posten = Posten.fromJSON(data.optJSONObject("posten"));
        }

        public Posten[] getPosten() {
            return posten;
        }
    }

    public static class ResponseDeletePosten extends EmptyResponse {

        public ResponseDeletePosten(JSONObject data) {
            super(data);
        }
    }

    public static class ResponseAddPosten extends EmptyResponse {

        public ResponseAddPosten(JSONObject data) {
            super(data);
        }
    }

    public static class ResponseAddUserToBill extends EmptyResponse {

        public ResponseAddUserToBill(JSONObject data) {
            super(data);
        }
    }

    public static class ResponseRemoveUserFromBill extends EmptyResponse {

        public ResponseRemoveUserFromBill(JSONObject data) {
            super(data);
        }
    }

    @FunctionalInterface
    public static interface ResponseCallback<T extends BaseResponse> {
        public void onResponse(T response);
    }
}
