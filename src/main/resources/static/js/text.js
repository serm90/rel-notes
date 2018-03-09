var  TP_URL = 'http://tp.pfsoft.net/entity/';

function scanUrls(msg){
    var p = new RegExp('#\d+');
    var matches = msg.match(p);
     for (var i=0;i<matches.length;i++){
         msg.replace(matches[i],'<a href="'+TP_URL+matches[i].substr(1)+'" >'+matches[i]+'</a>');
     }
     return msg;
}