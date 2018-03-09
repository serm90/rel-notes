package com.pfsoft.rnotes.viewbeans;

import java.util.List;

import com.pfsoft.rnotes.beans.Change;
import com.pfsoft.rnotes.beans.Version;

import lombok.Data;


@Data
public class ChangesFormBean {
    private Version from;
    private Version to;
    private List<Change> changes;
}
