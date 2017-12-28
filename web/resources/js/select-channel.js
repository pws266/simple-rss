/**
 * Created by newbie on 21.12.17.
 */
$(window).load(function(){
    $("#rssList tr").click(function(){
        $(this).addClass('selected').siblings().removeClass('selected');
        // var value=$(this).find('td:first').html();
        // alert(value);

        //$("channel-link-row").find("td").eq(0).html('Link: ');
        var $linkContent=$(this).find('td:nth-child(2)').html();//$("#rssList tr td:nth-child(2)").html();
        var $descContent=$(this).find('td:nth-child(3)').html();
        var $channelId=$(this).find('td:nth-child(4)').html();

        //var $link = $("<a>").text("http://ya.ru").attr("href", "http://ya.ru");
        var $link = $("<a>").text($linkContent).attr("href", $linkContent);
        $($("#channel-link-row td")[0]).html("").append('<b>Link:</b> ').append($link);

        $($("#channel-description-row td")[0]).html("").append('<b>Description:</b> ').append($descContent);

        // $("#channelInfo").row("#channel-link-row").invalidate().draw();

        $("#rssList tr.selected td:first").html();
        $("#DelChannel").val($channelId);
        $("#ShowChannel").val($channelId);
    });

    $("#rssFeed tr").click(function(){
        $(this).addClass('selected').siblings().removeClass('selected');

        // var $linkContent=$(this).find('td:nth-child(2)').html();//$("#rssList tr td:nth-child(2)").html();
        // var $descContent=$(this).find('td:nth-child(3)').html();
        // var $channelId=$(this).find('td:nth-child(4)').html();
        //
        // var $link = $("<a>").text($linkContent).attr("href", $linkContent);
        // $($("#channel-link-row td")[0]).html("").append('<b>Link:</b> ').append($link);
        //
        // $($("#channel-description-row td")[0]).html("").append('<b>Description:</b> ').append($descContent);

        $("#rssFeed tr.selected td:first").html();
        // $("#DelChannel").val($channelId);
        // $("#ShowChannel").val($channelId);
        alert(this.rowIndex);
    });


    $($("#rssList tr")[1]).click();
    $($("#rssFeed tr")[2]).click();


    $("#feed-pagination").pagination({
        items: 100,
        itemsOnPage: 10,
        displayedPages: 3,
        cssStyle: 'light-theme'
    });
    /*
     $('.ok').on('click', function(e){
     alert($("#rssList tr.selected td:first").html());
     });
     */
});//]]>
