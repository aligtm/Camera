package com.techkiii.Camera;

import static com.techkiii.Camera.CameraActivity.selected_aspect_ratio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.techkiii.R;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {
    public Integer[] buttonImage = new Integer[0];
    private Context context;
    private int selected = SensorControler.DELEY_DURATION;
    int height;

    class FilterHolder extends RecyclerView.ViewHolder {
        TextView filterName;
        FrameLayout filterRoot;
        ImageView imageViewDone;
        ImageView thumbImage;
        FrameLayout thumbSelected;
        View thumbSelected_bg;
        CapturePreview mCameraView;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public FilterAdapter(Context context, Integer[] buttonImage, int selected, int height) {
        this.context = context;
        this.buttonImage = buttonImage;
        this.selected = selected;
        this.height = height;
    }

    public void setIntPos(int pos) {
        this.selected = pos;
        notifyItemChanged(pos);
        notifyDataSetChanged();
    }

    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.filter_item_layout, parent, false);
        FilterHolder viewHolder = new FilterHolder(view);

        viewHolder.thumbImage = (ImageView) view.findViewById(R.id.filter_thumb_image);

        viewHolder.filterName = (TextView) view.findViewById(R.id.filter_thumb_name);

        viewHolder.filterRoot = (FrameLayout) view.findViewById(R.id.filter_root);

        viewHolder.thumbSelected = (FrameLayout) view.findViewById(R.id.filter_thumb_selected);

        viewHolder.imageViewDone = (ImageView) view.findViewById(R.id.filter_thumb_selected_icon);
        return viewHolder;
    }

    public void onBindViewHolder(FilterHolder holder, final int position) {
        try {
            holder.filterName.setText(filterName1(position));

            Glide.with(this.context).load(this.buttonImage[position]).into(holder.thumbImage);

            if (selected_aspect_ratio == 0) {
                holder.filterName.setTextColor(context.getResources().getColor(R.color.color_white));
            } else {
                holder.filterName.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark2));
            }

            if (position == this.selected) {
                holder.thumbSelected.setVisibility(View.VISIBLE);
                holder.imageViewDone.setImageResource(R.drawable.bg_border_20);
            } else {
                holder.imageViewDone.setImageResource(R.drawable.bg_white_border_10);
            }

            holder.filterRoot.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    FilterAdapter.this.notifyItemChanged(FilterAdapter.this.selected);
                    FilterAdapter.this.selected = position;
                    FilterAdapter.this.notifyItemChanged(FilterAdapter.this.selected);
                }
            });
        } catch (Exception e) {
        }
    }

    public int getItemCount() {
        return this.buttonImage == null ? 0 : this.buttonImage.length;
    }

    public int FilterType2Color(int pos) {
        switch (pos) {
        }
        return R.color.black_new;
    }

    public static int filterName1(int pos) {
        switch (pos) {
            case 1:
                return R.string.sala;
            case 2:
                return R.string.dawa;
            /*case 1:
                return R.string.pica;
            case 2:
                return R.string.rouge;
            case 3:
                return R.string.tuse;
            case 4:
                return R.string.versa;
            case 5:
                return R.string.slide;
            case 6:
                return R.string.bluzzer;
            case 7:
                return R.string.x7;
            case 8:
                return R.string.f2;
            case 9:
                return R.string.elset;
            case 10:
                return R.string.s1933;
            case 11:
                return R.string.koralle;
            case 14:
                return R.string.prism;
            case 15:
                return R.string.tri;
            case 16:
                return R.string.x1;
            case 17:
                return R.string.x2;
            case 18:
                return R.string.x3;
            case 19:
                return R.string.x4;
            case 20:
                return R.string.wind;
            case 21:
                return R.string.s1895;
            case 22:
                return R.string.mono;
            case 23:
                return R.string.leaf;
            case 24:
                return R.string.x5;
            case 25:
                return R.string.x6;
            case 26:
                return R.string.x7;
            case 27:
                return R.string.light;
            case 28:
                return R.string.dark;
            case 29:
                return R.string.wow;
            case 30:
                return R.string.lemo;
            case 31:
                return R.string.Newage;
            case 32:
                return R.string.Crescent;
            case 33:
                return R.string.Gloom;
            case 34:
                return R.string.Hippie;
            case 35:
                return R.string.Coffee;
            case 36:
                return R.string.Nostalgia;
            case 37:
                return R.string.filter_sunrise;
            case 38:
                return R.string.Matrix;
            case 39:
                return R.string.Memory;
            case 40:
                return R.string.Noir;
            case 41:
                return R.string.Eiffeln;
            case 42:
                return R.string.Eiffel;
            case 43:
                return R.string.Fresh1;
            case 44:
                return R.string.bolly;
            case 45:
                return R.string.color;
            case 46:
                return R.string.sunset;
            case 47:
                return R.string.music;
            case 48:
                return R.string.star;
            case 49:
                return R.string.rough;
            case 50:
                return R.string.sun;
            case 51:
                return R.string.rangoli;
            case 52:
                return R.string.flash;
            case 53:
                return R.string.bubble;
            case 54:
                return R.string.star12;
            case 55:
                return R.string.x11;*/
            default:
                return R.string.none;
        }
    }
}
