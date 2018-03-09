package com.pfsoft.rnotes.beans;

import lombok.Data;
import lombok.NonNull;

@Data
public class Link {
    @NonNull private String href;
    @NonNull private String lable;
}
