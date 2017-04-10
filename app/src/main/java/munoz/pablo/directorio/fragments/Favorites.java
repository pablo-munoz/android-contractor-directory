package munoz.pablo.directorio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.activities.MainActivity;
import munoz.pablo.directorio.adapters.FavoritesAdapter;
import munoz.pablo.directorio.models.Account;
import munoz.pablo.directorio.models.Contractor;
import munoz.pablo.directorio.models.ModelBuilder;
import munoz.pablo.directorio.services.APIRequest;
import munoz.pablo.directorio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favorites extends Fragment {
    private View view;
    private MainActivity mainActivity;

    private ArrayList<Contractor> favoriteContractors;
    private FavoritesAdapter favoritesAdapter;
    private ListView listView;

    public Favorites() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Favorites.
     */
    // TODO: Rename and change types and number of parameters
    public static Favorites newInstance() {
        Favorites fragment = new Favorites();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        favoriteContractors = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        listView = (ListView) view.findViewById(R.id.favorites_list_view);

        APIRequest requestToGetFavorites = new APIRequest(new APIRequest.APIRequestCallback() {
            @Override
            public void onSuccess(JSONObject json, int code) {
                ModelBuilder<Contractor> modelBuilder = new ModelBuilder<>();

                try {
                    favoriteContractors = modelBuilder.resourceListFromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                favoritesAdapter = new FavoritesAdapter(favoriteContractors, getActivity());
                listView.setAdapter(favoritesAdapter);
            }

            @Override
            public void onError(String errorMessage, int code) {

            }
        });

        Account userAccount = mainActivity.getUserAccount();

        String endpoint = String.format("%s/%s/account/%s/favorites",
                Constants.API_URL, Constants.API_VERSION, userAccount.getId());

        requestToGetFavorites.execute(APIRequest.HTTP_GET, endpoint, null, null);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contractor_id;
                contractor_id = favoriteContractors.get(position).getId();
                ContractorDetail newFragment = ContractorDetail.newInstance(contractor_id);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.changeContentFragment(newFragment);
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

}
