package com.parse.starter.setting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by izzi on 7/5/15.
 */

public class SettingArray<T> extends ASetting implements List<T> {
    public SettingArray(JSONObject parent, String name) throws JSONException, SettingException {
        super(parent, name, settingType);

        JSONArray jsonArray = parent.getJSONArray(name);

        this.value = new ArrayList<T>(jsonArray.length());

        for (int i = 0; size() > i; ++i) this.value.set(i, (T) jsonArray.get(i));
    }

    @Override
    public JSONObject jsonify(JSONObject parent) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (T v : value) jsonArray.put(v);

        return jsonify(parent, jsonArray);
    }

    private final static ESettingType settingType = ESettingType.settingArray;

    private ArrayList<T> value;

    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if (size() <= index) throw new IndexOutOfBoundsException();
    }

    @Override
    public void add(int location, T object) {
        value.add(object);
    }

    @Override
    public boolean add(T object) {
        return value.add(object);
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        return value.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return value.addAll(collection);
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public boolean contains(Object object) {
        return value.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return value.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return value.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return value.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return value.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return value.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return value.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int location) {
        return value.listIterator(location);
    }

    @Override
    public T remove(int location) {
        return value.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return value.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return value.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return value.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        return value.set(location, object);
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public List<T> subList(int start, int end) {
        return value.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return value.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        return value.toArray(array);
    }
}
