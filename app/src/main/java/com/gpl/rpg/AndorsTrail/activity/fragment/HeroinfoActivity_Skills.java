package com.gpl.rpg.AndorsTrail.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.AndorsTrailApplication;
import com.gpl.rpg.AndorsTrail.Dialogs;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.context.ControllerContext;
import com.gpl.rpg.AndorsTrail.context.WorldContext;
import com.gpl.rpg.AndorsTrail.model.ability.SkillCollection;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.view.SkillListAdapter;

import java.util.ArrayList;
public final class HeroinfoActivity_Skills extends Fragment {

	private static final int INTENTREQUEST_SKILLINFO = 12;

	private WorldContext world;
	private ControllerContext controllers;
	private Player player;

	ListView skillList;
	private ArrayList<SkillListAdapter> skillListCategoryViewsAdapters = new ArrayList<SkillListAdapter>();
	private Spinner skillList_categories;
	private Spinner skillList_sort;
	private TextView listskills_number_of_increases;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndorsTrailApplication app = AndorsTrailApplication.getApplicationFromActivity(getActivity());
		if (!app.isInitialized()) return;
		this.world = app.getWorld();
		this.controllers = app.getControllerContext();
		this.player = world.model.player;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.heroinfo_skill_list, container, false);

		final Activity ctx = getActivity();

		//Initiating drop-down list for category filters
		skillList_categories = (Spinner) v.findViewById(R.id.skillList_category_filters);
		ArrayAdapter<CharSequence> skillCategoryFilterAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.skill_category_filters, android.R.layout.simple_spinner_item);
		skillCategoryFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		skillList_categories.setAdapter(skillCategoryFilterAdapter);
		skillList_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				world.model.uiSelections.selectedSkillCategory = skillList_categories.getSelectedItemPosition();
				reloadShownCategory();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				world.model.uiSelections.selectedSkillCategory = 0;
			}
		});
		skillList_categories.setSelection(world.model.uiSelections.selectedSkillCategory);

		for(int i = 0; i< SkillCollection.SkillCategory.values().length; i++){
			// Creates a list of adapters for each category.
			// The adapter at position 0 has all items.
			// length + 1 in order to create an extra position for "all"
			skillListCategoryViewsAdapters.add(
					new SkillListAdapter(ctx, world.skills.getAllSkills(), player, i));
		}


		//Initiating drop-down list for sort filters
		skillList_sort = (Spinner) v.findViewById(R.id.skillList_sort_filters);
		ArrayAdapter<CharSequence> skillSortFilterAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.skill_sort_filters, android.R.layout.simple_spinner_item);
		skillSortFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		skillList_sort.setAdapter(skillSortFilterAdapter);
		skillList_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				world.model.uiSelections.selectedSkillSort = skillList_sort.getSelectedItemPosition();
				reloadShownSort(); //Yet to be implemented
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				world.model.uiSelections.selectedSkillSort = 0;
			}
		});
		skillList_sort.setSelection(world.model.uiSelections.selectedSkillSort);


		skillList = (ListView) v.findViewById(R.id.heroinfo_listskills_list);
		skillList.setAdapter(getCurrentCategoryAdapter());
		skillList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent = Dialogs.getIntentForSkillInfo(ctx,
						getCurrentCategoryAdapter().getItem(position).id);
				startActivityForResult(intent, INTENTREQUEST_SKILLINFO);
			}
		});
		listskills_number_of_increases = (TextView) v.findViewById(R.id.heroinfo_listskills_number_of_increases);
		return v;
	}

	private void reloadShownSort() {
		int v = world.model.uiSelections.selectedSkillSort;
		if(v ==0);
		if(v==1) getCurrentCategoryAdapter().sortByName();
		if(v==2) getCurrentCategoryAdapter().sortByPoints();
		if(v==3) getCurrentCategoryAdapter().sortByUnlocked();

		updateSkillList();
	}

	private void reloadShownCategory() {
		skillList.setAdapter(getCurrentCategoryAdapter());
		updateSkillList();
	}
	private SkillListAdapter getCurrentCategoryAdapter(){
		return skillListCategoryViewsAdapters.get(
				world.model.uiSelections.selectedSkillCategory);
	}

	@Override
	public void onStart() {
		super.onStart();
		update();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case INTENTREQUEST_SKILLINFO:
			if (resultCode != Activity.RESULT_OK) break;

			SkillCollection.SkillID skillID = SkillCollection.SkillID.valueOf(data.getExtras().getString("skillID"));
			controllers.skillController.levelUpSkillManually(player, world.skills.getSkill(skillID));
			break;
		}
		update();
	}

	private void update() {
		updateSkillList();
	}

	private void updateSkillList() {
		int numberOfSkillIncreases = player.getAvailableSkillIncreases();
		if (numberOfSkillIncreases > 0) {
			if (numberOfSkillIncreases == 1) {
				listskills_number_of_increases.setText(R.string.skill_number_of_increases_one);
			} else {
				listskills_number_of_increases.setText(getResources().getString(R.string.skill_number_of_increases_several, numberOfSkillIncreases));
			}
			listskills_number_of_increases.setVisibility(View.VISIBLE);
		} else {
			listskills_number_of_increases.setVisibility(View.GONE);
		}
		getCurrentCategoryAdapter().notifyDataSetInvalidated();
	}
}
