package com.ondeline.InstaurantCliente;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CardapioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private String idFragment;
    private ArrayList<ItemCardapio> itens;

    public CardapioFragment() { }

    public static CardapioFragment newInstance(String id, ArrayList<ItemCardapio> itens) {
        CardapioFragment fragment = new CardapioFragment();
        Bundle args = new Bundle();
        args.putString("categoria", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.idFragment = getArguments().getString("categoria");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cardapio_fragment, container, false);
        TextView textView = view.findViewById(R.id.nomeFragmento);
        textView.setText(this.idFragment);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}
