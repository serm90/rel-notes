package com.pfsoft.rnotes.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = {"changeId"} )
@ToString
public class Change {
    private static final String GERRIT_URL = "http://192.168.0.2:9091/r/#/q/";
    private static final String TP_URL = "http://tp.pfsoft.net/entity/";
    private static final Pattern p = Pattern.compile("#\\d+");
    private String message;
    @Getter private String fullMessage;
   @NonNull private String changeId;

   public Link getGerritLink(){
        int i = changeId.indexOf("\\s");
        return new Link(GERRIT_URL + (i>0?"message:"+changeId.substring(0,i):changeId), "in gerrit" );
   }

   public void setFullMessage(String fullMessage){
//       this.fullMessage =fullMessage;
       Matcher m = p.matcher(fullMessage);
       StringBuffer sb = new StringBuffer();
       while (m.find()) { // find next match
           String match = m.group();
           m.appendReplacement(sb, buildHref(TP_URL+match.substring(1), match));
       }
       m.appendTail(sb);
       this.fullMessage = sb.toString();
//               .replaceAll("[\r\n]+","<br>");
       if(fullMessage.indexOf("Change-Id:")>-1) {
           this.fullMessage =  this.fullMessage.replaceAll(changeId, buildHref(getGerritLink().getHref(),changeId));
       }
   }
   private String buildHref(Link link){
      return buildHref(link.getHref(),link.getLable());
   }
   private String buildHref(String link, String text){
      return  "<a href=\""+link+"\" >"+text+"</a>";
   }

    public List<Link> getTpLinks(){
        ArrayList<Link> list = new ArrayList<>();
        Matcher m = p.matcher(fullMessage);
        while (m.find()) { // find next match
            String match = m.group();
            list.add(new Link(TP_URL+match.substring(1), match));
        }
        return list;
    }
}
