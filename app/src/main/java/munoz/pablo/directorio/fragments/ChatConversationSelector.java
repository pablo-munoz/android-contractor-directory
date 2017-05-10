package munoz.pablo.directorio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.activities.MainActivity;
import munoz.pablo.directorio.adapters.ConversationAdapter;
import munoz.pablo.directorio.models.Account;
import munoz.pablo.directorio.models.Conversation;
import munoz.pablo.directorio.models.Message;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.utils.AndroidContractorDirectoryApp;
import munoz.pablo.directorio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ChatConversationSelector#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatConversationSelector extends Fragment {
    private View view;

    private ListView listView;
    private ConversationAdapter conversationAdapter;

    public ChatConversationSelector() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatConversationSelector.
     */
    public static ChatConversationSelector newInstance() {
        ChatConversationSelector fragment = new ChatConversationSelector();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AndroidContractorDirectoryApp application = (AndroidContractorDirectoryApp) getActivity().getApplication();

        conversationAdapter = new ConversationAdapter(new ArrayList<Conversation>(), getActivity());

        APIRequest request = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                try {
                    JSONArray data = json.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject conversationData = data.getJSONObject(i);

                        String conversationId = conversationData.getString("id");
                        Date lastMessageDate = new Date();
                        JSONArray interlocutorIdJsonList = conversationData.getJSONArray("interlocutors");
                        JSONObject interlocutorData = conversationData.getJSONObject("interlocutor_data");

                        ArrayList<String> interlocutorIdList = new ArrayList<>();

                        for (int j = 0; j < interlocutorIdJsonList.length(); j++) {
                            interlocutorIdList.add(interlocutorIdJsonList.getString(j));
                        }

                        JSONArray messageJsonList = conversationData.getJSONArray("messages");
                        ArrayList<Message> messageList = new ArrayList<>();

                        for (int j = 0; j < messageJsonList.length(); j++) {
                            JSONObject messageData = messageJsonList.getJSONObject(j);
                            Message message = (new Message.Builder(1))
                                    .username(messageData.getString("from"))
                                    .message(messageData.getString("message"))
                                    .build();
                            messageList.add(message);
                        }

                        Conversation conversation = new Conversation(
                                conversationId,
                                lastMessageDate,
                                interlocutorIdList,
                                messageList,
                                interlocutorData
                        );

                        application.setConversation(conversationId, conversation);
                        Log.d("Added it", ""+application.getConversationList().size());
                        updateView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage, int code) {
                Log.e("ChatConverSelector", "Error retrieving conversation data");
                Log.e("ChatConverSelector", errorMessage);
            }
        });

        Account userAccount = application.getUserAccount();

        String endpoint = Constants.API_URL + "/" + Constants.API_VERSION + "/conversation";
        String headers = String.format("{ \"Authorization\": \"Bearer %s\" }", userAccount.getToken());
        request.execute(APIRequest.HTTP_GET, endpoint, headers.toString(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final AndroidContractorDirectoryApp application = (AndroidContractorDirectoryApp) getActivity().getApplication();

        view = inflater.inflate(R.layout.fragment_chat_conversation_selector, container, false);

        listView = (ListView) view.findViewById(R.id.chat_conversation_selector_list_view);
        updateView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Conversation> conversationList = application.getConversationList();
                Conversation conversation = conversationList.get(position);

                String recipientId = null;
                String conversationId = null;

                for (int i = 0; i < 2; i++) {
                    if (!conversation.interlocutorIdList.get(i).equals(application.getUserAccount().getId())) {
                        recipientId = conversation.interlocutorIdList.get(i);
                        conversationId = conversation.getId();
                    }
                }

                ChatConversation chatConversation = ChatConversation.newInstance(recipientId, conversationId);

                ((MainActivity) getActivity()).changeContentFragment(chatConversation);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateView() {
        final AndroidContractorDirectoryApp application = (AndroidContractorDirectoryApp) getActivity().getApplication();

        conversationAdapter = new ConversationAdapter(application.getConversationList(), getActivity());
        Log.d("Size of", ""+application.getConversationList().size());
        listView.setAdapter(conversationAdapter);
    }
}
