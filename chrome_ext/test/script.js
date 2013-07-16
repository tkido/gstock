$(function(){
$("a.l").each(function(){　//検索結果ごとに処理しますよ。
var domain = $(this).attr('href');　//URLを取得しますよ。
var domain2 = domain.match(/^[httpsfile]+:\/{2,3}([0-9a-zA-Z\.\-:]+?):?[0-9]*?\//i);　//取得したURLのドメイン部分だけ取り出しますよ。
var favget = "//www.google.com/s2/favicons?domain="+domain2[1];　//Googleさんのファビコン取得APIのURLを入れますよ。
var favgethtml = "<img src='"+favget+"' class='favi'/>"; //上記のURLを画像ファイルタグにしますよ。
$(this).parent().prepend(favgethtml); //タイトルの前に上記のタグを挿入しますよ。
});
});
