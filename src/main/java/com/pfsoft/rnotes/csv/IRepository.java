package com.pfsoft.rnotes.csv;

import java.util.Collection;

import com.pfsoft.rnotes.beans.Change;

public interface IRepository {

    Collection<String> getTags();
    Collection<Change> getChanges(String startTag, String untilTag) ;
}
