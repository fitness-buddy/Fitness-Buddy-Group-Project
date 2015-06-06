package com.strengthcoach.strengthcoach.views;

import android.content.Context;

import com.dexafree.materialList.cards.SimpleCard;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.Trainer;

public class CustomCard extends SimpleCard {

    public Trainer trainer;
    public CustomCard(Context context, Trainer trainer) {
        super(context);
        this.trainer = trainer;
    }

    @Override
    public int getLayout(){
        return R.layout.card_layout;
    }
}
