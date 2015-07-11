package com.sjgilbert.unanimus.setting;

import java.util.LinkedList;

/**
 * Created by izzi on 7/5/15.
 */

public class SettingPath<T extends ASetting> {
    public final static String delimiter = "/";

    public SettingPath(String fullPath) {
        pathString = fullPath;
        pathList = new LinkedList<String>();
        for (String loc : fullPath.split(delimiter)) if (!loc.isEmpty()) pathList.addLast(loc);
    }

    public final String pathString;

    public T get() throws SettingException {
        SettingEdge current = SettingTree.settings.root;
        ASetting member = current;

        LinkedList<String> path = (LinkedList<String>) pathList.clone();

        while (!path.isEmpty()) {
            member = current.getMember(path.pop());

            if (member instanceof SettingEdge) current = (SettingEdge) member;

            if (!path.isEmpty()) throw new SettingException();
        }

        return (T) member;
    }

    private LinkedList<String> pathList;
}
