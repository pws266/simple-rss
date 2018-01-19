/**
 * Created by newbie on 21.12.17.
 */

$(window).load(function(){
    var $trRssList = $("#rssList").find("tr"),
        $trRssFeed = $("#rssFeed").find("tr");

    $("input[name='sorting']").change(function(){
        var $sortingState = $(this).val();

        $("#SortRBForAdd").val($sortingState);
        $("#SortRBForDelete").val($sortingState);

        $("#showFeedsForm").submit();
/*
        $.ajax({
            url: "../show-feeds",
            method: "POST",
            data: {displayChannelId: $("#ShowChannel").val(),
                   pageNumber: $("#PageForShow").val(),
                   channelRow: $("#ChannelRow").val(),
                   sorting: $sortingState},

            error: function(message) {
                console.log(message);
            },
            success: function(data) {
                console.log(data);
            }
        });
*/
    });

    /*
        $("#rssList").find("tr").click(function(){
            $(this).addClass('selected').siblings().removeClass('selected');
            // var value=$(this).find('td:first').html();
            // alert(value);

            alert("Row: " + $(this).index());

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
    */

    function showChannel(elm, rowIndex) {
        // var myRow = $('tr', elm.parent("table")).eq(rowIndex);
        var myRow = $($(elm).parents("table")[0]).find("tr")[rowIndex];

        $(myRow).addClass('selected').siblings().removeClass('selected');

        var $linkContent=$(myRow).find('td:nth-child(2)').html();
        var $descContent=$(myRow).find('td:nth-child(3)').html();
        var $channelId=$(myRow).find('td:nth-child(4)').html();

        var $link = $("<a>").text($linkContent).attr("href", $linkContent);
        $($("#channel-link-row").find("td")[0]).html("").append('<b>Link:</b> ').append($link);

        $($("#channel-description-row").find("td")[0]).html("").append('<b>Description:</b> ').append($descContent);

        //$("#rssList tr.selected td:first").html();
        $(elm).parent("table").find("tr.selected").find("td:first").html();
        $("#DelChannel").val($channelId);
        $("#ShowChannel").val($channelId);

        $("#ChannelRow").val(rowIndex);
    };

    $trRssList.click(function(){
        var rowIndex = $(this).index();
        if (rowIndex < 1) {
            return;
        }

        $('#PageForShow').val(1);

        //$("input[name='sorting']").val("asc");
        showChannel(this, rowIndex);

        $("#showFeedsForm").submit();
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
        // alert("Feed number: " + this.rowIndex);
    });


    // $($("#rssList tr")[1]).click();
    showChannel($trRssList, currentChannelRow);

//    $($("#rssFeed tr")[2]).click();


    $("#feed-pagination").pagination({
        items: feedsNumber,
        itemsOnPage: feedsPerPage,
        currentPage: $('#PageForShow').val(), //currentPage,
        displayedPages: 3,
        cssStyle: 'light-theme',

        onPageClick: function(pageNumber) {
            $("#PageForShow").val(pageNumber);
            $("#showFeedsForm").submit();
        }
    });

    // >> start of "checkFragment"

    // function checkFragment() {
    //     // If there's no hash, treat it like page 1.
    //     var hash = window.location.hash || "#page-1";
    //
    //     // We'll use a regular expression to check the hash string.
    //     hash = hash.match(/^#page-(\d+)$/);
    //
    //     if(hash) {
    //         // The `selectPage` function is described in the documentation.
    //         // We've captured the page number in a regex group: `(\d+)`.
    //         // !incorrect version
    //         //$(".pagination-page").pagination("selectPage", parseInt(hash[1]));
    //         // correct version but we initialize current page: "checkFragment" usage isn't necessary
    //         $("#feed-pagination").pagination("selectPage", parseInt(hash[1]));
    //     }
    // };

    // We'll call this function whenever back/forward is pressed...
    // $(window).bind("popstate", checkFragment);
    //
    // checkFragment();

// >> end of "checkFragment"

    /*
     $('.ok').on('click', function(e){
     alert($("#rssList tr.selected td:first").html());
     });
     */
});//]]>
