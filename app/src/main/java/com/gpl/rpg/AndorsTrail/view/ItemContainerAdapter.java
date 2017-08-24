package com.gpl.rpg.AndorsTrail.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.gpl.rpg.AndorsTrail.R;
import com.gpl.rpg.AndorsTrail.controller.ItemController;
import com.gpl.rpg.AndorsTrail.model.actor.Player;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer;
import com.gpl.rpg.AndorsTrail.model.item.ItemContainer.ItemEntry;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileCollection;
import com.gpl.rpg.AndorsTrail.resource.tiles.TileManager;

public class ItemContainerAdapter extends ArrayAdapter<ItemEntry> {
	private final TileManager tileManager;
	private final TileCollection tileCollection;
	private final Player player;

	public ItemContainerAdapter(Context context, TileManager tileManager, ItemContainer items, Player player) {
		this(context, tileManager, items, player, tileManager.loadTilesFor(items, context.getResources()));
	}
	public ItemContainerAdapter(Context context, TileManager tileManager, ItemContainer items, Player player, TileCollection tileCollection) {
		super(context, 0, items.items);
		this.tileManager = tileManager;
		this.tileCollection = tileCollection;
		this.player = player;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ItemEntry item = getItem(position);

		View result = convertView;
		if (result == null) {
			result = View.inflate(getContext(), R.layout.inventoryitemview, null);
		}
		TextView tv = (TextView) result;

		tileManager.setImageViewTile(getContext().getResources(), tv, item.itemType, tileCollection);
		tv.setText(ItemController.describeItemForListView(item, player));
		return result;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).itemType.id.hashCode();
	}

	/*public void reloadShownCategory(int category){ // Overwrites player.inventory instead of cloning it
		this.clear();
		ArrayList<
		for(ItemEntry i: (ItemEntry) this.player.inventory.items.clone()){
			if(category == 0)
				this.add(i);
			else if(i.itemType.isWeapon())
				if(category ==1)
					this.add(i);
			else if(i.itemType.isEquippable() && ! i.itemType.isWeapon())
				if(category==2)
					this.add(i);
			else if(i.itemType.isUsable())
					if(category ==3)
						this.add(i);
			else if(i.itemType.isQuestItem())
						if(category ==4)
							this.add(i);
			else if(category == 5) //other items
							this.add(i);
		}
	}*/

	public static void reloadShownSort(int selected, int oldSortSelection, ItemContainer container, Player p) {
		//Not sure which is worse, hardcoding the names or the position.

		if(selected == oldSortSelection);
			//inv.sortByReverse();
		else if (selected ==1)
			container.sortByName(p);
		else if (selected == 2)
			container.sortByPrice(p);
		else if (selected == 3)
			container.sortByQuantity(p);
		else if (selected == 4)
			container.sortByRarity(p);
		else if (selected == 5)
			container.sortByType(p);
		else if (selected == 0); //Unsorted
	}

	public static void reloadShownSort(int selected, ItemContainer container, Player p){
		reloadShownSort(selected, -1, container, p);
	}
}
