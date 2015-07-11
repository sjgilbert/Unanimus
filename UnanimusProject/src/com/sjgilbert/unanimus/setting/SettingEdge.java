package com.sjgilbert.unanimus.setting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by izzi on 7/5/15.
 */

/**
 * Bullshit I can't extend ASetting and ArrayList<ASetting>, then I could just use 'this' and not
 * reimplement the whole Collection interface directly to members.
 */
public class SettingEdge extends ASetting implements Collection<ASetting> {
    public SettingEdge(JSONObject parent, String name) throws JSONException, SettingException {
        super(parent, name, settingType);

        JSONObject jsonObject = parent.getJSONObject(name);

        this.members = new ArrayList<ASetting>(jsonObject.length());

        Iterator<String> keys = jsonObject.keys();

        for (String k = keys.next(); true; k = keys.next()) {
            if (ESettingType.settingsEdge.hasPrefix(k)) add(new SettingEdge(jsonObject, k));
            else if (ESettingType.settingsNode.hasPrefix(k)) add(new SettingNode(jsonObject, k));
            else if (ESettingType.settingArray.hasPrefix(k)) add(new SettingArray(jsonObject, k));
            else throw new SettingException("Got string: " + k);

            // it seems like iterators would make this easier than it is
            if (!keys.hasNext()) break;
        }
    }

    public ASetting getMember(String name) throws SettingException {
        for (ASetting setting : members) if (name.equals(setting.getName())) return setting;
        throw new SettingException();
    }

    @Override
    public JSONObject jsonify(JSONObject parent) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (ASetting setting : members) setting.jsonify(jsonObject);
        return super.jsonify(parent, jsonObject);
    }

    @Override
    public boolean add(ASetting object) {
        return members.add(object);
    }

    @Override
    public boolean addAll(Collection<? extends ASetting> collection) {
        return members.addAll(collection);
    }

    @Override
    public void clear() {
        members.clear();
    }

    @Override
    public boolean contains(Object object) {
        return members.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return members.contains(collection);
    }

    @Override
    public boolean isEmpty() {
        return members.isEmpty();
    }

    @Override
    public Iterator<ASetting> iterator() {
        return members.iterator();
    }

    @Override
    public boolean remove(Object object) {
        return members.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return members.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return members.retainAll(collection);
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public Object[] toArray() {
        return members.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return members.toArray(array);
    }

    private final static ESettingType settingType = ESettingType.settingsEdge;

    private final ArrayList<ASetting> members;
}
