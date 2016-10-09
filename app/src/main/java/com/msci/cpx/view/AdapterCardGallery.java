package com.msci.cpx.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.msci.cpx.R;
import com.msci.cpx.engine.StandardCard;
import com.msci.cpx.engine.StandardCard.Rank;
import com.msci.cpx.utility.Helper;

public class AdapterCardGallery extends BaseAdapter {
	private Activity activity;
	private StandardCard[] cards;
	private static StandardCard selectedCard = null;
	private static int selectedIndex = -1;
	
	public AdapterCardGallery(Activity activity, StandardCard[] cards) {
		this.activity = activity;
		this.cards = cards;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public StandardCard[] getCards() {
		return cards;
	}

	public void setCards(StandardCard[] cards) {
		this.cards = cards;
		notifyDataSetChanged();
	}

	public StandardCard getSelectedCard() {
		return selectedCard;
	}

	public void setSelectedCard(StandardCard selectedCard) {
		this.selectedCard = selectedCard;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		this.selectedCard = cards[selectedIndex];
		notifyDataSetChanged();
	}

	public void replaceCard(StandardCard card) {
		cards[selectedIndex] = card;
		notifyDataSetChanged();
	}
	
	public void replaceCards(StandardCard[] cards) {
		this.cards = cards;
		notifyDataSetChanged();
	}
	
	public boolean isCardEmpty() {
		boolean isEmpty = true;
		
		for (StandardCard card:cards) {
			if (card != null) {
				isEmpty = false;
				break;
			}
		}
		
		return isEmpty;
	}
	
	public void setSelectedSpecialCard() {
		int index = 0;
		for (StandardCard card:cards) {
			if (card.getRank().equals(Rank.Four)) {
				setSelectedIndex(index);
				setSelectedCard(card);
				break;
			}
			index++;
		}
	}

	@Override
	public int getCount() {
		return cards.length;
	}

	@Override
	public Object getItem(int position) {
		return cards[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = convertView;
		
		if (view == null) {
			view = inflater.inflate(R.layout.custom_card, parent, false);
		}
		
		StandardCard card = this.cards[position];
		
		TextView textview = (TextView)view.findViewById(R.id.text1);
		ImageView imageView = (ImageView)view.findViewById(R.id.imageview1);
		
		textview.setVisibility(View.GONE);
		
		if (card != null) {
			String cardDisplay = card.getRank().name() + " " + card.getSuit().name();
			textview.setText(cardDisplay);
			imageView.setImageBitmap(Helper.getInstance().getBitmap(getActivity(), card));
		} else {
			textview.setText("");
			imageView.setImageBitmap(null);
		}
		
		view.setId(position);
		
		if (position == selectedIndex) {
			view.setSelected(true);
			view.setBackgroundResource(R.drawable.rect_selected_border);
			selectedCard = cards[selectedIndex];
		} else {
			view.setSelected(false);
			view.setBackgroundResource(R.drawable.selector_card);
		}
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View lastSelectedView = (View)parent.findViewById(selectedIndex);
				
				if (lastSelectedView != null) {
					lastSelectedView.setSelected(false);
					lastSelectedView.setBackgroundResource(R.drawable.selector_card);
				}
				
				v.setSelected(true);
				v.setBackgroundResource(R.drawable.rect_selected_border);
				selectedIndex = v.getId();
				selectedCard = cards[v.getId()];
			}
		});
		
		return view;
	}

}
