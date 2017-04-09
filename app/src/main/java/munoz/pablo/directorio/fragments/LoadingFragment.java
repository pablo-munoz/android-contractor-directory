package munoz.pablo.directorio.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import munoz.pablo.directorio.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MESSAGE = "message";

    private ImageView imageIv;
    private TextView messageTv;

    // TODO: Rename and change types of parameters
    private String message;


    public LoadingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message The message to put in the fragment.
     * @return A new instance of fragment LoadingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoadingFragment newInstance(String message) {
        LoadingFragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        imageIv = (ImageView) view.findViewById(R.id.loading_fragment_image);
        messageTv = (TextView) view.findViewById(R.id.loading_fragment_message);

        messageTv.setText(this.message);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageIv);
        Glide.with(this).load(R.raw.spherewave).into(imageViewTarget);


        return view;
    }

    public void addToManager(FragmentManager manager, int containerId) {
        if (!isAdded()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(containerId, this, "loading");
            transaction.commit();
        }
    }

    public void removeFromManager(FragmentManager manager) {
        if (isAdded()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(this);
            transaction.commit();
        }
    }

}
