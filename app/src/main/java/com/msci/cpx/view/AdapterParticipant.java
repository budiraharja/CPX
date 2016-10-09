package com.msci.cpx.view;

import com.msci.cpx.R;
import com.msci.cpx.model.Participant;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterParticipant extends BaseAdapter {
	private Activity activity;
	private ArrayList<Participant> list;
	private int selectedIndex = -1;
	private boolean showScore = false;
	private int resourceId = 0;
	private int textColorId = android.R.color.white;
	
	public AdapterParticipant(Activity activity, ArrayList<Participant> list) {
		this(activity, list, R.layout.custom_list, android.R.color.white);
	}
	
	public AdapterParticipant(Activity activity, ArrayList<Participant> list, int resource, int textColorId) {
		this.activity = activity;
		this.list = list;
		this.showScore = false;
		this.resourceId = resource;
		this.textColorId = textColorId;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getId();
	}
	
	public void updateList(ArrayList<Participant> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public Participant getParticipantById(int id) {
		for (Participant p:list) {
			if (p.getId() == id) {
				return p;
			}
		}
		
		return null;
	}
	
	public int getIndexById(int id) {
		int index = 0;
		for (Participant p:list) {
			if (p.getId() == id) {
				return index;
			}
			index++;
		}
		
		return -1;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		notifyDataSetChanged();
	}

	public ArrayList<Participant> getList() {
		return list;
	}
	
	public void updateScore(int id, int score) {
		for (Participant p:list) {
			if (p.getId() == id) {
				p.setScore(score);
				break;
			}
		}
		
		notifyDataSetChanged();
	}
	
	public void removeParticipant(int id) {
		for (Participant p:list) {
			if (p.getId() == id) {
				list.remove(p);
				break;
			}
		}
		
		notifyDataSetChanged();
	}
	
	public Participant getMissingParticipant(ArrayList<Participant> list) {
		Participant missingParticipant = null;
		for (Participant p:getList()) {
			missingParticipant = p;
			for (Participant np:list) {
				if (p.getId() == np.getId()) {
					missingParticipant = null;
					break;
				}
			}
			
			if (missingParticipant != null) {
				break;
			}
		}
		return missingParticipant;
	}

	public boolean isShowScore() {
		return showScore;
	}

	public void setShowScore(boolean showScore) {
		this.showScore = showScore;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = convertView;
		
		if (view == null) {
			view = inflater.inflate(resourceId, null);
		}
		
		if (position == selectedIndex) {
			view.setBackgroundResource(R.color.button_color_pressed);
			view.setSelected(true);
		} else {
			view.setBackgroundResource(0);
			view.setSelected(false);
		}
		
		Participant player = list.get(position);
		String text = player.getNameId();
		
		if (isShowScore()) {
			text = String.format(activity.getString(R.string.player_score), player.getNameId(), player.getScore());
		}
		
		TextView textView = (TextView) view.findViewById(R.id.text1);
		textView.setText(text);
		textView.setTextColor(activity.getResources().getColor(textColorId));
		
		return view;
	}

}
