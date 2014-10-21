package org.pottberg.ips.model;

import java.util.ArrayList;
import java.util.List;

public class History<T> {
    
    List<T> items;
    int previousItemIndex = -1;
    
    public History() {
	items = new ArrayList<>();
    }

    public void addToHistory(T item) {
	clearRedoList();
	items.add(item);
	previousItemIndex++;	
    }

    public void addToFuture(T item) {
        items.add(item);
    }

    private void clearRedoList() {
	for (int index = getLastIndex(); index > previousItemIndex; index--) {
	    items.remove(index);
	}
    }

    public boolean hasPrevious() {
        return previousItemIndex != -1;
    }

    public T getPrevious() {
	if(!hasPrevious()) {
	    return null;
	}
	return items.get(previousItemIndex);
    }

    public void back() {
	if(!hasPrevious()) {
	    return;
	}
	previousItemIndex--;
    }

    public boolean hasNext() {
        return previousItemIndex < getLastIndex();
    }

    public T getNext() {
	if(!hasNext()) {
	    return null;
	}
        return items.get(previousItemIndex + 1);
    }

    public void next() {
	if(!hasNext()) {
	    return;
	}
	previousItemIndex++;
    }

    public int size() {
	return items.size();
    }

    public int getPreviousIndex() {
	return previousItemIndex;
    }

    public int getLastIndex() {
	return size() - 1;
    }

}
