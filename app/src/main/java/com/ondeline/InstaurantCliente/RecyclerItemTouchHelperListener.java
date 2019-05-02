package com.ondeline.InstaurantCliente;

import android.support.v7.widget.RecyclerView;

interface RecyclerItemTouchHelperListener {
    void onSwiped(RecyclerView.ViewHolder itemViewHolder, int direction, int position);
}
