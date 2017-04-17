package munoz.pablo.directorio.utils;


import android.app.Application;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import munoz.pablo.directorio.models.Account;
import munoz.pablo.directorio.models.Conversation;

/**
 * Created by pablo on 4/11/2017.
 */
public class AndroidContractorDirectoryApp extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    private Account userAccount = Account.getAnonymous();

    public Account getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(Account account) {
        userAccount = account;
    }

    private Map<String, Conversation> conversations = new HashMap<>();

    public void setConversation(String conversationId, Conversation conversation) {
        conversations.put(conversationId, conversation);
    }

    public ArrayList<Conversation> getConversationList() {
        return new ArrayList<Conversation>(conversations.values());
    }

    public void injectAuthorizationHeader(JSONObject json) {
        try {
            json.put("Authorization", "Bearer " + userAccount.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
