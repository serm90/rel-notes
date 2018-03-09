package com.pfsoft.rnotes.services.impl;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import com.pfsoft.rnotes.beans.Change;
import com.pfsoft.rnotes.beans.Version;
import com.pfsoft.rnotes.csv.IRepository;
import com.pfsoft.rnotes.services.IVersionService;
@Service
public class VersionService implements IVersionService {
    private  final IRepository repository;
    private  final Converter<String,Version> versionConverter;
    private final ConcurrentHashMap<String, SoftReference<List<Change>>> cashe = new ConcurrentHashMap<>();

    public  VersionService(@Autowired IRepository repository, @Autowired Converter<String,Version> versionConverter){
        this.repository = repository;
        this.versionConverter = versionConverter;
    }

    @Override public List<Version> getVersions() {
       return  repository.getTags().stream()
                .map( versionConverter::convert)
                .filter(Objects::nonNull)
                .sorted( Version.comparator)
               .collect(Collectors.toList());
    }

    @Override public List<Change> getChanges(Version start, Version until) {
        String key = start.getTag()+".."+until.getTag();
        List<Change> list = cashe.computeIfAbsent(key, k-> new SoftReference<>(getChangesFromRepo(start,until))).get();
        if(list==null){
            list = getChangesFromRepo(start,until);
            cashe.put(key,new SoftReference(list ));
        }
        return list;
    }

    private List<Change> getChangesFromRepo(Version start, Version until) {
        Collection<Change>  changeList=  repository.getChanges(start.getTag(),until.getTag());
        Collection<Change> exclusions =   repository.getChanges(until.getTag(), start.getTag());
        return changeList.stream()
                .filter(c->!c.getMessage().startsWith("[maven-release-plugin]"))
                .filter(c->!exclusions.contains(c))
                .collect(Collectors.toList());
    }


}
