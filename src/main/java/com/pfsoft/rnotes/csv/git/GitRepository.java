package com.pfsoft.rnotes.csv.git;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Repository;

import com.pfsoft.rnotes.converters.TagToVersionConverter;
import com.pfsoft.rnotes.csv.IRepository;
import com.pfsoft.rnotes.beans.Change;
import com.pfsoft.rnotes.beans.Version;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class GitRepository implements IRepository{


    private final String repoUri;
    private final String localPath;
    private final Git git;
    private static final long FETCH_INTERVAL = 5 *60 * 1000;
    private long lastFetch =0;

    public GitRepository(@Value("${repo.uri}") String repoUri,
                         @Value("${repo.local.path}") String localPath ) throws GitAPIException {
        this.repoUri=repoUri;
        this.localPath = localPath;
        this.git = initGit();
        ensureFetch();
    }

    private Git initGit() throws GitAPIException {
        File path = new File(localPath);
        if(path.exists()){
            return Git.init().setDirectory(path).call();
        }else {
            path.mkdirs();
             return  Git.cloneRepository().setURI(repoUri).setDirectory(path).call();
        }
    }

    private void ensureFetch(){
        final long time = System.currentTimeMillis();
        if(time-lastFetch > FETCH_INTERVAL){
            try {
                git.fetch().call();
                lastFetch = time;
            } catch (GitAPIException e) {
                log.error("Cant't fetch", e);
            }
        }
    }

    public List<String> getTags(){
        ensureFetch();
        try {
            return git.tagList().call()
                    .stream()
                    .map(Ref::getName)
                    .collect(Collectors.toList());
        } catch (GitAPIException e) {
            e.printStackTrace();
            log.error("can't get tags", e);
            return Collections.emptyList();
        }
    }

    @Override public Collection<Change> getChanges(String begin, String end)   {
        final org.eclipse.jgit.lib.Repository repository = git.getRepository();
        try {
//            ObjectId head = repository.resolve("HEAD");
            Ref start = repository.findRef(begin );
            Ref until = repository.findRef(end );
            return  asSet(git.log().addRange(getActualRefObjectId(start), getActualRefObjectId(until)).call());
        }catch (IOException |  GitAPIException  e){
            log.error("Error when try to get changes from "+begin+" to "+end  , e);
        }




        return Collections.emptyList();
    }

    private Predicate<RevCommit> filter = c->{
       for(String path: getFilesInCommit(c) ){
           if( path.contains("NewOrderMessage")
                   ||path.contains( "PFIXCancelOrderMessage")
                   || path.contains( "MessageListener") ){
               return true;
           }
       }
       return false;
    };




    public  Set<String> getFilesInCommit(RevCommit commit) {
        org.eclipse.jgit.lib.Repository repository = git.getRepository();
         RevCommit parent =  commit.getParent(0);
        try {


            FileTreeIterator fileTreeIterator = new FileTreeIterator(repository);

            IndexDiff indexDiff = new IndexDiff(repository, commit.getId(), fileTreeIterator);
            indexDiff.diff();
//           return  indexDiff.getChanged();
            return git.diff().setOldTree( getTreeIterator(parent.toObjectId()))
                    .setNewTree(  getTreeIterator(commit.getId() ) )
                    .setShowNameAndStatusOnly(true).call().
                    stream().map(df -> df.toString()).collect(Collectors.toSet());
        }catch (Exception e){
            return Collections.emptySet();
        }

    }

    private AbstractTreeIterator getTreeIterator(final @NonNull ObjectId id)
            throws IOException {
        final CanonicalTreeParser p = new CanonicalTreeParser();
        try (ObjectReader or = git.getRepository().newObjectReader();
                RevWalk rw = new RevWalk(git.getRepository())) {
            p.reset(or, rw.parseTree(id));
            return p;
        }
    }

    private Set<Change> asSet(Iterable<RevCommit> commits) {
        LinkedHashSet<Change> result = new LinkedHashSet<>();
        for(RevCommit commit : commits){
//            if(filter.test(commit)) {
                result.add(convert(commit));
//            }
        }
        return result;
    }

    private Change convert(RevCommit commit) {
        final String changeId;
        final List<String> footerLines = commit.getFooterLines("Change-Id");
        if( footerLines!=null && footerLines.size()>0){
            changeId = footerLines.get(0);
        }else {
            changeId = commit.getShortMessage();
        }
        Change change = new Change(changeId);
        change.setMessage(commit.getShortMessage());
        change.setFullMessage(commit.getFullMessage());
        return change;
    }

    public Iterable<RevCommit> getChanges(String startTag )   {
        final org.eclipse.jgit.lib.Repository repository = git.getRepository();
        try {
            return   git.log()
                    .add(getActualRefObjectId(repository.findRef(startTag)))
                    .call();
        }catch (IOException |  GitAPIException  e){
            log.error("Error when try to get changes", e);
        }
        return  Collections.emptyList();
    }



    private ObjectId getActualRefObjectId(Ref ref) {
        final Ref repoPeeled = git.getRepository().peel(ref);
        if(repoPeeled.getPeeledObjectId() != null) {
            return repoPeeled.getPeeledObjectId();
        }
        return ref.getObjectId();
    }
}
