/*
 * Copyright (C) 2017 Christian Rivera
 *
 */

package com.christian.domaindemo;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.christian.domaindemo.PropListingsFragment.OnListingsSelectedListener;

/**
 * {@link RecyclerView.Adapter} that can display a {card and makes a call to the
 * specified {@link OnListingsSelectedListener}.
 */
public class PropRecyclerViewAdapter
        extends RecyclerView.Adapter<PropRecyclerViewAdapter.ViewHolder> {
    private final OnListingsSelectedListener mListener;
    private final Context mContextHolder;
    private final ImageLoader mImageLoader;
    private int mThumbnailWidth;
    private int mThumbnailHeight;
    private int mSelectedPosition = -1;

    public static final int LISTING_ELITE = 1;
    public static final int LISTING_NOT_ELITE = 0;

    public PropRecyclerViewAdapter(OnListingsSelectedListener listener) {
        mListener = listener;
        mContextHolder = DomainDemoSingleton.getContext();
        mImageLoader = DomainDemoSingleton.getInstance().getImageLoader();
    }

    public int getSelectedPosition() { return mSelectedPosition; }

    public void setSelectedPosition(final int value) { mSelectedPosition = value;  }

    public void setReferenceWidth(int width) {
       int cardWidth = width -
                ((int)mContextHolder.getResources().getDimension(R.dimen.card_bar_width) +
                2*(int)mContextHolder.getResources().getDimension(R.dimen.card_padding) );
        mThumbnailWidth = cardWidth /2 - 2*(int)mContextHolder.getResources().
                getDimension(R.dimen.card_image_spacing);

        TypedValue tmp = new TypedValue();
        mContextHolder.getResources().getValue(R.dimen.image_aspect_ratio, tmp, true);
        mThumbnailHeight = (int)(mThumbnailWidth / tmp.getFloat());
    }

    public int getItemViewType(int position) {
        int x = PropertyData.getUint(position,
                mContextHolder.getString(R.string.key_is_elite));
        if (x < -1) return LISTING_NOT_ELITE;
        else return x;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        if (viewType == LISTING_ELITE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.elite_property_card, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.property_card, parent, false);
        }
        return new ViewHolder(view);
    }

    private String roomExpander(String room, String text, String altTextIfZero, String separator) {
        String tmp = (separator.isEmpty())? "": separator + " ";
        if (room.equals("0")) return altTextIfZero;
        else return tmp + room + " " + text;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //String displayableAddress;

        int agencyBarColor;
        int textWidth = mThumbnailWidth; // enforce text wrapping on card text

        boolean isElite = (PropertyData.getUint(position,
                mContextHolder.getString(R.string.key_is_elite)) == LISTING_ELITE);
        String textNotFound = mContextHolder.getString(R.string.not_found_text);

        String displayPrice = PropertyData.getString(position,
                mContextHolder.getString(R.string.key_display_price));
        if (displayPrice == null) displayPrice = textNotFound;
        else if (displayPrice.isEmpty()) displayPrice = mContextHolder.getString(R.string.label_zero_price);

        String separator = mContextHolder.getString(R.string.label_separator);

        String tmpBed = PropertyData.getString(position, mContextHolder.getString(R.string.key_bedrooms));
        if (tmpBed == null) tmpBed = textNotFound;
        else tmpBed = roomExpander(tmpBed,
                mContextHolder.getString(R.string.label_bed),
                mContextHolder.getString(R.string.label_zero_bed), "");

        String tmpBath = PropertyData.getString(position,
                mContextHolder.getString(R.string.key_bathrooms));
        if (tmpBath == null) tmpBath = textNotFound;
        else tmpBath = roomExpander(tmpBath,
                   mContextHolder.getString(R.string.label_bath),
                   mContextHolder.getString(R.string.label_zero_bath), separator);

        String tmpCar = PropertyData.getString(position,
                    mContextHolder.getString(R.string.key_carspaces));
        if (tmpCar == null) tmpCar = textNotFound;
        else tmpCar = roomExpander(tmpCar,
                    mContextHolder.getString(R.string.label_car),
                    mContextHolder.getString(R.string.label_zero_car), separator);
        String propertySpecs = tmpBed + tmpBath + tmpCar;

        String displayableAddress = PropertyData.getString(position,
                    mContextHolder.getString(R.string.key_displayable_address));
        if (displayableAddress == null) displayableAddress = textNotFound;

        String tmpUrl = PropertyData.getString(position,
                mContextHolder.getString(R.string.key_retina_display_thumb_url));
        if (tmpUrl == null) holder.mPrimeThumbnail.setDefaultImageResId(R.drawable.default_blank);
        else {
            holder.mPrimeThumbnail.setImageUrl(tmpUrl,  mImageLoader);
            holder.mPrimeProgressBar.setVisibility(View.GONE);
        }

        holder.mPrimeThumbnail.getLayoutParams().width = mThumbnailWidth;
        holder.mPrimeThumbnail.getLayoutParams().height = mThumbnailHeight;
        holder.mPrimeThumbnail.setScaleType(ImageView.ScaleType.FIT_XY);

        if (isElite) {
            tmpUrl = PropertyData.getString(position, mContextHolder
                    .getString(R.string.key_second_retina_display_thumb_url));

            if (tmpUrl == null) holder.mSecondThumbnail.setDefaultImageResId(R.drawable.default_blank);
            else {
                holder.mSecondThumbnail.setImageUrl(tmpUrl, mImageLoader);
                holder.mSecondProgressBar.setVisibility(View.GONE);
            }
            holder.mSecondThumbnail.getLayoutParams().width = mThumbnailWidth;
            holder.mSecondThumbnail.getLayoutParams().height = mThumbnailHeight;
            holder.mSecondThumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
            textWidth = 2 * mThumbnailWidth;
        }

        tmpUrl = PropertyData.getString(position, mContextHolder.getString(R.string.key_agency_logo_url));
        if (tmpUrl != null) holder.mAgencyLogo.setImageUrl(tmpUrl, mImageLoader);

        String tmpColor = PropertyData.getString(position, mContextHolder.getString(R.string.key_agency_color));
        agencyBarColor = Integer.decode(tmpColor) | 0xFF000000; // set alpha channel

        holder.mFavorite.getLayoutParams().height = mThumbnailHeight/4;
        holder.mFavorite.getLayoutParams().width = mThumbnailHeight/4;
        holder.mFavorite.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.mFavorite.setActivated(PropertyData.isInFavorites(position));

        ((CardView)holder.itemView).setCardBackgroundColor(agencyBarColor);

        holder.mDisplayPrice.setText(displayPrice);
        holder.mPropertySpecs.setText(propertySpecs);
        holder.mDisplayableAddress.setText(displayableAddress);

        // force text wrapping; text won't wrap properly in GridLayout
        holder.mDisplayPrice.getLayoutParams().width = textWidth;
        holder.mPropertySpecs.getLayoutParams().width = textWidth;
        holder.mDisplayableAddress.getLayoutParams().width = textWidth;

        holder.itemView.setSelected(mSelectedPosition == position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListSelectedInteraction(position);
                notifyItemChanged(mSelectedPosition);
                mSelectedPosition = position;
                notifyItemChanged(mSelectedPosition);
            }
        });

        holder.mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isActive = !holder.mFavorite.isActivated();
                holder.mFavorite.setActivated(isActive);
                if (isActive)  PropertyData.addToFavorites(position);
                else      PropertyData.removeFromFavorites(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return PropertyData.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final NetworkImageView mPrimeThumbnail;
        public final NetworkImageView mSecondThumbnail;
        public final ProgressBar mPrimeProgressBar;
        public final ProgressBar mSecondProgressBar;
        public final TextView mDisplayPrice;
        public final TextView mPropertySpecs;
        public final TextView mDisplayableAddress;
        public final NetworkImageView mAgencyLogo;
        public final ImageButton mFavorite;

        public ViewHolder(View view) {
            super(view);
            mPrimeThumbnail = (NetworkImageView) view.findViewById(R.id.prime_thumbnail);
            mSecondThumbnail = (NetworkImageView) view.findViewById(R.id.second_thumbnail);
            mPrimeProgressBar = (ProgressBar) view.findViewById(R.id.prime_progress_bar);
            mSecondProgressBar = (ProgressBar) view.findViewById(R.id.secondary_progress_bar);
            mDisplayPrice = (TextView) view.findViewById(R.id.display_price);
            mPropertySpecs = (TextView) view.findViewById(R.id.property_specs);
            mDisplayableAddress = (TextView) view.findViewById(R.id.displayable_address);
            mAgencyLogo = (NetworkImageView) view.findViewById(R.id.agency_logo);
            mFavorite = (ImageButton) view.findViewById(R.id.favorite);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + itemView.toString() + "'";
        }
    }
}
