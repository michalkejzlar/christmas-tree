package com.easycore.stromecek.model;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import com.easycore.stromecek.R;

import java.util.Random;

public class ChristmasColor implements Parcelable {

    private final int treeColor;
    private final int materialColor;

    private ChristmasColor(int treeColor, int materialColor) {
        this.treeColor = treeColor;
        this.materialColor = materialColor;
    }

    protected ChristmasColor(Parcel in) {
        treeColor = in.readInt();
        materialColor = in.readInt();
    }

    public int getTreeColor() {
        return treeColor;
    }

    public int getMaterialColor() {
        return materialColor;
    }

    public static final Creator<ChristmasColor> CREATOR = new Creator<ChristmasColor>() {
        @Override
        public ChristmasColor createFromParcel(Parcel in) {
            return new ChristmasColor(in);
        }

        @Override
        public ChristmasColor[] newArray(int size) {
            return new ChristmasColor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(treeColor);
        parcel.writeInt(materialColor);
    }

    public static ChristmasColor random(Context context) {
        @ColorRes int[] matColorResId = new int[] {R.color.tree_material_red, R.color.tree_material_green,
                R.color.tree_material_blue, R.color.tree_material_yellow};
        @ColorRes int[] treeColorResId = new int[] {R.color.tree_red, R.color.tree_green,
                R.color.tree_blue, R.color.tree_yellow};

        if (matColorResId.length != treeColorResId.length) {
            throw new IllegalArgumentException("You must have same amount of material and tree colors.");
        }

        final int rnd = new Random().nextInt(matColorResId.length - 1);

        return new ChristmasColor(ContextCompat.getColor(context, treeColorResId[rnd]),
                ContextCompat.getColor(context, matColorResId[rnd]));
    }

    public static ChristmasColor red(Context context) {
        return new ChristmasColor(ContextCompat.getColor(context, R.color.tree_red),
                ContextCompat.getColor(context, R.color.tree_material_red));
    }

    public static ChristmasColor green(Context context) {
        return new ChristmasColor(ContextCompat.getColor(context, R.color.tree_green),
                ContextCompat.getColor(context, R.color.tree_material_green));
    }

    public static ChristmasColor blue(Context context) {
        return new ChristmasColor(ContextCompat.getColor(context, R.color.tree_blue),
                ContextCompat.getColor(context, R.color.tree_material_blue));
    }

    public static ChristmasColor yellow(Context context) {
        return new ChristmasColor(ContextCompat.getColor(context, R.color.tree_yellow),
                ContextCompat.getColor(context, R.color.tree_material_yellow));
    }
}
