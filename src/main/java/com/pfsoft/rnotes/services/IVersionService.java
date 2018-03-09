package com.pfsoft.rnotes.services;

import java.util.List;

import com.pfsoft.rnotes.beans.Change;
import com.pfsoft.rnotes.beans.Version;

public interface IVersionService {
    List<Version> getVersions();
    List<Change> getChanges(Version start, Version until) ;
}
