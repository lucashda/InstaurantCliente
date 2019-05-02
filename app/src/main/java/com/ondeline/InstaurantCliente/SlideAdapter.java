package com.ondeline.InstaurantCliente;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;

    String [] title = {"Título 1", "Titulo 2"};
    String [] description = {"descrição, descricção, desc.", "dyfthaqbwvicuqywehcbqweuyocihqbwdukcyg"};
    int imageView [] = {R.drawable._slide1, R.drawable._slide1};

    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.slide, container, false);
            ImageView imagemSlide = view.findViewById(R.id.imgSlide);
            TextView txtSlide = view.findViewById(R.id.txtTitleSlide);
            TextView descricao = view.findViewById(R.id.txtDescriptionSlide);
            imagemSlide.setImageResource(imageView[position]);
            txtSlide.setText(title[position]);
            descricao.setText(description[position]);
            container.addView(view);
            return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
