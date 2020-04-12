
function dianwo(){
    document.getElementById('zhezhao').style.display="block";

}
function hidder(){
    document.getElementById('zhezhao').style.display="none";
}

document.getElementById('editPage').style.display="none";
function editPageDisplay(){
    document.getElementById('editPage').style.display="block";

}
function editPageHider(){
    document.getElementById('editPage').style.display="none";
}


document.getElementById('editHead').style.display="none";
function editHead(){
    document.getElementById('editHead').style.display="block";

}
function editHeadHider(){
    document.getElementById('editHead').style.display="none";
}


function RecommendHead(){
    var sign = $('#listHead').children().length;
    if(sign!=0){

    }
    else {
        $.ajax({
            url: "touser/editRecommendHead",
            success: function (data) {
                var list = data.listHead;
                for (var i = 0; i < list.length; i++) {
                    var id = list[i].id;
                    var recommendHeadUrl = list[i].recommendHeadUrl;
                    $('#listHead').prepend(
                        "<a href=\"touser/editHead1?id=" + id + "\"><img src=\"" + recommendHeadUrl + "\" width=\"50px\" height=\"50px\"></a>"
                    );
                }
            },
            error: function () {
                alert("出错了???");
            },
            dataType: "json",
            type: "GET"
        });
    }
    document.getElementById('RecommendHead').style.display="block";
}
function RecommendHeadHider(){
    document.getElementById('RecommendHead').style.display="none";
}


function xiaoxi1(){
    document.getElementById('lianxiren').style.display="block";
    document.getElementById('lianxiren1').style.display="none";
    document.getElementById('xiaoxi').style.display="block";
    document.getElementById('xiaoxi1').style.display="none";
    document.getElementById('GroupChat').style.display="block";
    document.getElementById('GroupChat1').style.display="none";
    // 消息列表显示
    document.getElementById('MessageList').style.display="block";
    //  隐藏群聊列表
    document.getElementById('GroupChatList').style.display="none";
    // 联系人列表隐藏
    document.getElementById('UserList').style.display="none";
    //搜索结果列表隐藏
    document.getElementById('SearchResultsList').style.display="none";
    //  聊天框名字显示
    document.getElementById('chatName').style.display="block";
    //  聊天框显示
    document.getElementById('ChatBox').style.display="block";
    // 用户详情隐藏
    document.getElementById('UserDetail').style.display="none";
    document.getElementById('groupInformation').style.display="none";
    document.getElementById('theSpace').style.display="none";
    document.getElementById('friendsSpace').style.display="none";
    document.getElementById('space').style.display="block";
    document.getElementById('space1').style.display="none";
    document.getElementById('thePublish').style.display="none";
    document.getElementById('GameList').style.display="none";
    document.getElementById('game').style.display="block";
    document.getElementById('game1').style.display="none";
    document.getElementById('GoBang').style.display="none";
    document.getElementById('GoBangInviteList').style.display="none";

}



function lianxiren(){
    document.getElementById('lianxiren').style.display="none";
    document.getElementById('lianxiren1').style.display="block";
    document.getElementById('xiaoxi').style.display="none";
    document.getElementById('xiaoxi1').style.display="block";
    document.getElementById('GroupChat').style.display="block";
    document.getElementById('GroupChat1').style.display="none";
    //  显示联系人列表
    document.getElementById('UserList').style.display="block";
    //  聊天框隐藏
    document.getElementById('ChatBox').style.display="none";
    //  隐藏群聊列表
    document.getElementById('GroupChatList').style.display="none";
    //  消息列表隐藏
    document.getElementById('MessageList').style.display="none";
    //搜索结果列表隐藏
    document.getElementById('SearchResultsList').style.display="none";
    //  聊天框名字隐藏
    document.getElementById('chatName').style.display="none";
    //  省略号隐藏
    document.getElementById('ellipsis').style.display="none";
    document.getElementById('groupInformation').style.display="none";
    document.getElementById('space').style.display="block";
    document.getElementById('space1').style.display="none";
    document.getElementById('GameList').style.display="none";
    document.getElementById('game').style.display="block";
    document.getElementById('game1').style.display="none";
}


function GroupChat() {
    document.getElementById('lianxiren').style.display="block";
    document.getElementById('lianxiren1').style.display="none";
    document.getElementById('xiaoxi').style.display="none";
    document.getElementById('xiaoxi1').style.display="block";
    document.getElementById('GroupChat').style.display="none";
    document.getElementById('GroupChat1').style.display="block";

    //  显示群聊列表
    document.getElementById('GroupChatList').style.display="block";
    //  隐藏联系人列表
    document.getElementById('UserList').style.display="none";
    //  消息列表隐藏
    document.getElementById('MessageList').style.display="none";
    //  搜索结果列表隐藏
    document.getElementById('SearchResultsList').style.display="none";
    document.getElementById('groupInformation').style.display="none";
    document.getElementById('space').style.display="block";
    document.getElementById('space1').style.display="none";
    document.getElementById('GameList').style.display="none";
    document.getElementById('game').style.display="block";
    document.getElementById('game1').style.display="none";
}

//加载联系人列表数据
$.ajax({
    url: "touser/findAllUser",
    success:function(data) {
        // 清空联系人列表
        document.getElementById('UserList').innerHTML = "";
        var listChar = data.listChar;
        for (var i=0;i<listChar.length;i++){
            var fistChar = listChar[i].toLocaleUpperCase();
            //大写首字母

            $('#UserList').append(


                "<div style=\"margin-top: 20px;margin-left: 15px;font-size:15px;color:grey;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">" +
                fistChar+
                "</div>"
            );
            //每个字母下的联系人
            var listUser = data[fistChar.toLowerCase()];
            for (var j=0;j<listUser.length;j++){
                var headPortrait = listUser[j].headPortrait;
                var nickName = listUser[j].nickName;
                var accountNumber = listUser[j].accountNumber;
                $('#UserList').append(
                    "<br/>"+
                    "<div onclick=\"details("+accountNumber+")\" style=\"margin-top: 20px;margin-left: 15px\">" +
                    "<img style=\"float: left\" src=\"" + headPortrait + "\" width=\"40px\" height=\"40px\">" +
                    "<span style=\"margin-left: 18px;margin-top: 10px;float: left\">"+nickName+"" +
                    "</span>" +
                    "</div>"+"<br/>"
                );
            }
            $('#UserList').append(
                "<div style=\"margin-top: 20px\">"+
                "<br/>"+ "<hr/>"+
                "</div>"
            );

        }
    },
    error:function () {
        alert("出错了????");
    },
    dataType: "json",
    type: "GET"
});

//加载游戏列表
$.ajax({
    url:"unclassified/findAllGame",
    success:function (data) {
        $('#GameList').append(
            "<div style=\"margin-left: 10px;width: 200px;position: relative;height: 60px\" onclick=\"openGoBang()\">" +
            "            <div style=\"float:left;width: 60px;height: 60px\">" +
            "                <img src=\"https://i.postimg.cc/nLFGQQ23/u-707541989-3531887347-fm-26-gp-0.jpg\" width=\"50px\" height=\"50px\"  style=\"float: left\" >" +
            "            </div>" +
            "            <div style=\"float:right;margin-right: 10px;margin-top: 10px\">" +
            "                连珠五子棋" +
            "            </div>" +
            "</div>"
        );

    },
    error:function () {
        alert("findAllGame-error");
    },
    dataType: "json",
    type: "GET"
});

function openGame() {
    document.getElementById('game').style.display="none";
    document.getElementById('xiaoxi').style.display="none";
    document.getElementById('game1').style.display="block";
    document.getElementById('xiaoxi1').style.display="block";
    //  消息列表隐藏
    document.getElementById('MessageList').style.display="none";
    document.getElementById('GameList').style.display="block";
    document.getElementById('GroupChat').style.display="block";
    document.getElementById('GroupChat1').style.display="none";
    //  显示群聊列表
    document.getElementById('GroupChatList').style.display="none";
    //  隐藏联系人列表
    document.getElementById('UserList').style.display="none";
    //  搜索结果列表隐藏
    document.getElementById('SearchResultsList').style.display="none";
    document.getElementById('space').style.display="block";
    document.getElementById('space1').style.display="none";
}

function openGoBang() {
    document.getElementById('GoBang').innerHTML="";
    document.getElementById('theSpace').style.display="none";
    document.getElementById('UserDetail').style.display="none";
    document.getElementById('chatName').style.display="none";
    document.getElementById('accountNumber').style.display="none";
    document.getElementById('ellipsis').style.display="none";
    document.getElementById('ChatBox').style.display="none";
    document.getElementById('groupInformation').style.display="none";
    document.getElementById('thePublish').style.display="none";
    document.getElementById('friendsSpace').style.display="none";
    document.getElementById('thePublish').style.display="none";
    $('#GoBang').append(
        "<img src=\"image/goBangBackground.jpg\" style='width: 600px;height: 650px'>"+
        "<button onclick=\"InviteFriends()\" style='position: absolute;top: 100px;left: 600px'>邀請好友</button>" +
        "<div style='position: absolute;top: 150px;left: 600px;color: yellow'> 發起挑戰者為白方，先手，落子無悔 </div>"
    );
    document.getElementById('GoBang').style.display="block";

}

function InviteFriends() {
    $.ajax({
        url: "touser/findAllUser",
        success: function(data) {
            // 清空列表
            document.getElementById('GoBangInviteList').innerHTML = "";
            document.getElementById('GoBangInviteList').style.display="block";
            var listChar = data.listChar;
            for (var i=0;i<listChar.length;i++){
                var fistChar = listChar[i].toLocaleUpperCase();
                //大写首字母
                $('#GoBangInviteList').append(

                    "<div style=\"margin-top: 20px;margin-left: 15px;font-size:15px;color:grey;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">" +
                    fistChar+
                    "</div>"
                );
                //每个字母下的联系人
                var listUser = data[fistChar.toLowerCase()];
                for (var j=0;j<listUser.length;j++){
                    var headPortrait = listUser[j].headPortrait;
                    var nickName = listUser[j].nickName;
                    var accountNumber = listUser[j].accountNumber;
                    $('#GoBangInviteList').append(
                        "<br/>"+
                        "<div style=\"margin-top: 20px;margin-left: 15px\">" +
                        "<img style=\"float: left\" src=\"" + headPortrait + "\" width=\"40px\" height=\"40px\">" +
                        "<span style=\"margin-left: 18px;margin-top: 10px;float: left\">"+nickName+"" +
                        "</span>" +
                        "<button onclick='InviteToGoBang("+accountNumber+")' style=\"margin-left: 18px;margin-top: 10px;float: left\">邀请 "+
                        "</button>" +
                        "</div>"+"<br/>"
                    );
                }
                $('#GoBangInviteList').append(
                    "<div style=\"margin-top: 20px\">"+
                    "<br/>"+ "<hr/>"+
                    "</div>"
                );

            }
        },
        error:function() {
            alert("??");
        },
        dataType: "json",
        type: "GET"
    });
}

function InviteToGoBang(accountNumber) {
    // 清空列表
    document.getElementById('GoBangInviteList').innerHTML = "";
    document.getElementById('vvvv').innerHTML="";
    alert("稍等........");
    stompClient.send("/app/game", {}, JSON.stringify({
        'toNumber': accountNumber,
        'type':"GoBangInvite"
    }));

}