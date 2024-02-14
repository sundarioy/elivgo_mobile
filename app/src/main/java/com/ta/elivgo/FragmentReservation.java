package com.ta.elivgo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ta.elivgo.adapter.ReservationAdapter;
import com.ta.elivgo.adapter.SpkluAdapter;
import com.ta.elivgo.model.ListReservation;
import com.ta.elivgo.response.DataResponse;
import com.ta.elivgo.rest.ApiConnection;
import com.ta.elivgo.rest.InterfaceConnection;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentReservation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReservation extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ReservationAdapter resvAdapter;
    public List<ListReservation> list = new ArrayList<>();

    InterfaceConnection interfaceConnection;

    public FragmentReservation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentReservation.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentReservation newInstance(String param1, String param2) {
        FragmentReservation fragment = new FragmentReservation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_reservation, container, false);
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);
        recyclerView = view.findViewById(R.id.rv_rsvd_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        interfaceConnection = ApiConnection.getClient().create(InterfaceConnection.class);
        loadRecyclerViewItem();

        return view;
    }

    private void loadRecyclerViewItem() {
        resvAdapter = new ReservationAdapter(getContext());
        Call<DataResponse> getRespResv = interfaceConnection.getUserReservations("1");
        getRespResv.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()) {
                    list = response.body().getUserReservations();
                    resvAdapter.updateReservation(list);

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        Log.i("error", jObjError.getString("message"));
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i("error", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.d("Error Jaringan", "disini");
                t.printStackTrace();
                Log.d("here", "here", t);
                Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });

//        ListReservation listReservation = new ListReservation("SPKLU PLN UID Jakarta Raya", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","20 Juni 2023","14:00 - 14:30");
//        list.add(listReservation);
//        listReservation = new ListReservation("SPKLU PLN UID Jakarta Raya", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","20 Juni 2023","14:00 - 14:30");
//        list.add(listReservation);
//        listReservation = new ListReservation("SPKLU PLN UID Jakarta Raya", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","20 Juni 2023","14:00 - 14:30");
//        list.add(listReservation);
//        listReservation = new ListReservation("SPKLU PLN UID Jakarta Raya", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","20 Juni 2023","14:00 - 14:30");
//        list.add(listReservation);
//        listReservation[0] = new ListReservation("SPKLU PLN UID Jakarta Raya", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","20 Juni 2023","14:00 - 14:30");
//        list.add(listReservation[0]);
//        resvAdapter = new ReservationAdapter(this.list, this);
//        recyclerView.setAdapter();
        recyclerView.setAdapter(resvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


}