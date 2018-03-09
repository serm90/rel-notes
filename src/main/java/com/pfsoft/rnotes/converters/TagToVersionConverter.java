package com.pfsoft.rnotes.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.pfsoft.rnotes.beans.Version;

@Component
public class TagToVersionConverter  implements Converter<String, Version> {

    @Override public Version convert(String tagId) {

        final int beginIndex = tagId.indexOf('3');
        if(beginIndex>-1) {
            String name = tagId.substring(beginIndex);
            return new Version(name, tagId);
        }else {
            return null;
        }
    }
}
