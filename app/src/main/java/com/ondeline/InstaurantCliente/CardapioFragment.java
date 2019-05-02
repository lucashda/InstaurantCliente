package com.ondeline.InstaurantCliente;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class CardapioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> nomes = new ArrayList<>();
    private ArrayList<String> descricoes = new ArrayList<>();
    private ArrayList<String> valores = new ArrayList<>();
    private ArrayList<String> imagens = new ArrayList<>();

    private Button btnLimpar;
    private Button btnFazerPedido;
    private TextView valorTotal;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private Context context;

    public CardapioFragment() { }

    public static CardapioFragment newInstance(ArrayList<String> nomes, ArrayList<String> descricoes, ArrayList<String> valores, ArrayList<String>imagens) {
        CardapioFragment fragment = new CardapioFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("nomes", nomes);
        args.putStringArrayList("imagens", imagens);
        args.putStringArrayList("valores", valores);
        args.putStringArrayList("descricoes", descricoes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.nomes = getArguments().getStringArrayList("nomes");
            this.imagens = getArguments().getStringArrayList("imagens");
            this.valores = getArguments().getStringArrayList("valores");
            this.descricoes = getArguments().getStringArrayList("descricoes");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cardapio_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewCardapio);
        adapter = new ItemAdapter(recyclerView.getId(), nomes, imagens, valores, context);
        adapter.setCategoria(getTag());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

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


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setBtnLimpar(Button btnLimpar) {
        this.btnLimpar = btnLimpar;
    }

    public void setBtnFazerPedido(Button btnFazerPedido) {
        this.btnFazerPedido = btnFazerPedido;
    }

    public void setValorTotal(TextView valorTotal) {
        this.valorTotal = valorTotal;
    }

    public interface OnFragmentInteractionListener {

    }
}
