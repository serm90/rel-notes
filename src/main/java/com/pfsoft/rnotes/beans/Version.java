package com.pfsoft.rnotes.beans;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor public class Version {
    public static final Comparator<Version> comparator = (v1, v2) -> {
        String[] ver1 = v1.getName().split("\\.");
        String[] ver2 = v2.getName().split("\\.");
        int res = 0;
        for (int i = 0; i < 3; i++) {
            res = Integer.parseInt(ver2[i]) - Integer.parseInt(ver1[i]);
            if (res != 0) {
                return res;
            }
        }
        return res;
    };

    private String name;
    private String tag;
}
