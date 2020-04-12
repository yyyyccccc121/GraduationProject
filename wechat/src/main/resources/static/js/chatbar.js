

//显示和隐藏表情包
$(document).bind("click",function (e) {
    var target = $(e.target);
    if(target.closest("#showCate").length == 0){
        document.getElementById('emojiWrapper').style.display="none";
    }
    else
    {
        document.getElementById('emojiWrapper').style.display="block";
    }
})

//预加载表情包图片
$(function(){
    for (var i = 30; i > 0; i--){
        $('#emojiWrapper').prepend(
            "<button onclick='sendEmoji(this)'>"+
                 "<img src=\"image/emoji/" + i + ".gif\" title=\""+i+"\" width=\"50px\" height=\"50px\" style=\"margin-top: 2px;margin-left: 2px\">"+
            "</button>"
        );
    }
});

//把表情包添加到发送框
function sendEmoji(data) {
    var emojiId = data.lastElementChild.title;
    document.getElementById("message").value+="[EMOJI:"+emojiId+"]";
}

function toSendMessage(nickName,accountNumber,headPortrait) {

    document.getElementById('lianxiren').style.display="block";
    document.getElementById('lianxiren1').style.display="none";
    document.getElementById('xiaoxi').style.display="block";
    document.getElementById('xiaoxi1').style.display="none";

    // 联系人列表隐藏
    document.getElementById('UserList').style.display="none";
    //搜索结果列表隐藏
    document.getElementById('SearchResultsList').style.display="none";
    // 消息列表显示
    document.getElementById('MessageList').style.display="block";
    // 用户详情隐藏
    document.getElementById('UserDetail').style.display="none";
    // 聊天框显示
    document.getElementById('ChatBox').style.display="block";
    // 清空聊天框名字
    document.getElementById('chatName').innerHTML = "";
    // 聊天框名字显示
    document.getElementById('chatName').style.display="block";
    document.getElementById('GroupChatList').style.display="none";
    document.getElementById('GroupChat').style.display="block";
    document.getElementById('GroupChat1').style.display="none";

    $('#chatName').prepend(
        "<span>"+nickName+"</span>"
    );

    //清空当前打开的人的账号
    document.getElementById('accountNumber').innerHTML = "";

    $('#accountNumber').prepend(
        "<p id='toAccountNumber'>"+accountNumber+"</p>"
    );

    openChat(nickName,accountNumber,headPortrait)

    //添加当前用户到聊天列表
    addMessageList(accountNumber,nickName,headPortrait,"");
}

function openChat(nickName,accountNumber,headPortrait) {
    //清空聊天框名字
    document.getElementById('chatName').innerHTML = "";

    $('#chatName').prepend(
        "<span>"+nickName+"</span>"
    );

    //清空当前打开的人的账号
    document.getElementById('accountNumber').innerHTML = "";

    $('#accountNumber').prepend(
        "<p id='toAccountNumber'>"+accountNumber+"</p>"
    );
    //  聊天框名字显示
    document.getElementById('chatName').style.display="block";
    //  聊天框显示
    document.getElementById('ChatBox').style.display="block";
    //  清空聊天框
    document.getElementById('messageView').innerHTML = "";
    //查询历史消息
    document.getElementById('theSpace').style.display="none";
    document.getElementById('friendsSpace').style.display="none";
    document.getElementById('thePublish').style.display="none";
    document.getElementById('GoBang').style.display="none";
    document.getElementById('GoBangInviteList').style.display="none";
    document.getElementById('Checkerboard').style.display="none";
    $.ajax({
        url: "message/queryHistoryMessage",
        data: {"toAccountNumber":accountNumber},
        success:function(data) {
            var list = data.list;
            for (var i = 0; i < list.length; i++) {
                var fromNumber = list[i].fromNumber;
                var content = list[i].content;
                //如果有表情就转化成表情
                var newContent = showEmoji(decodeURIComponent(content));
                var state = list[i].state;
                //消息发送者是他
                if (fromNumber==accountNumber){
                    //是普通文本消息
                    if(state=="0"||state=="1") {
                        document.getElementById("messageView").innerHTML +=
                            "<div style=\"float:left\">" + "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">" + " " + decodeURIComponent(newContent) + "</div>" + "<br/><br/><br/>";
                    //是文件
                    }else if(state=="2"||state=="3"){
                        //根据唯一文件名查真实文件名
                        $.ajax({
                            url:"/findRealFileName",
                            data:{"uuidFileName":newContent},
                            async:false,
                            success:function (data) {
                                var realFileName = data.realFileName;
                                if(realFileName.length>15){
                                    realFileName = realFileName.substring(0,6)+"..."+realFileName.substring(realFileName.indexOf('.')-6);
                                }
                                document.getElementById("messageView").innerHTML+=
                                    "<div style=\"float:left\">" +
                                         "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">"+ " "+
                                         "<div style=\"float:right\">"+
                                              realFileName+"<br/>"+
                                              "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\" href=\"/util/downfile?uuidFileName="+newContent+"\">下载</a>"+
                                         "</div>"+
                                    "</div>" + "<br/><br/><br/>";
                            },
                            error:function () {
                                alert("queryHistoryMessage-error");
                            },dataType: "json",
                            type: "GET"

                        });
                    //图片视频
                    }else if(state=="6"||state=="7"){
                        //获取后缀
                        var houZui = getType(content);
                        //是视频
                        if (houZui=="mp4"||houZui=="webm"||houZui=="ogg"){
                            document.getElementById("messageView").innerHTML +=
                                "<br/><br/><br/>"+
                                "<div style=\"float:left;\">" +
                                "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">"+"  " +
                                "<div style=\"float:right;margin-right: 5px;\">" +
                                "<video src=\"flage/"+content+"\" controls=\"controls\" style='width: 200px;height: 150px;'>" +
                                "</video>"+
                                "<br/>" +
                                "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + content + "\">下载</a>" +
                                "</div>" +
                                "</div>" + "<br/><br/><br/><br/><br/><br/>";
                        }else {
                            document.getElementById("messageView").innerHTML +=
                                "<br/><br/><br/>"+
                                "<div style=\"float:left;\">" +
                                "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">"+"  " +
                                "<div style=\"float:right;margin-right: 5px;\">" +
                                "<img src=\"flage/"+content+"\" style='width: 80px;height: 80px;'>" +
                                "</img>"+
                                "<br/>" +
                                "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + content + "\">下载</a>" +
                                "</div>" +
                                "</div>" + "<br/><br/><br/><br/><br/><br/>";
                        }

                    //是语音
                    }else{
                        //根据id查时间
                        $.ajax({
                            url:"/findTheTimeById",
                            data:{"uuidAudioName":content},
                            async:false,
                            success:function (data) {
                                var theTime = data.theTime;
                                document.getElementById("messageView").innerHTML+=
                                    "<br/><br/><br/>"+
                                    "<div style=\"float:left\">" +
                                    "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">"+ "  "+
                                    "<div style=\"float:right\">"+
                                    "<div class='warper'><div id="+content+" class='voiceItem'><)) "+theTime+"s</div></div>"+
                                    "</div>"+
                                    "</div>" + "<br/><br/><br/>";
                            },
                            error:function () {
                                alert("queryHistoryMessage-error");
                            },
                            dataType:"json",
                            type:"GET"
                        });
                    }
                }
                //消息发送者是我
                else{
                    //获取我的头像路径
                    var myHeadPortrait = document.getElementById("headPortrait").src;
                    //是普通文本消息
                    if(state=="0"||state=="1") {
                        document.getElementById("messageView").innerHTML +=
                            "<div style=\"float:right\">" + decodeURIComponent(newContent) + " " + "<img src=\"" + myHeadPortrait + "\" width=\"50px\" height=\"50px\">" + "</div>" + "<br/><br/><br/>";
                    //是文件
                    }else if(state=="2"||state=="3"){
                        //根据唯一文件名查真实文件名
                        $.ajax({
                            url:"/findRealFileName",
                            data:{"uuidFileName":newContent},
                            async:false,
                            success:function (data) {
                                var realFileName = data.realFileName;
                                if(realFileName.length>15){
                                    realFileName = realFileName.substring(0,6)+"..."+realFileName.substring(realFileName.indexOf('.')-6);
                                }
                                document.getElementById("messageView").innerHTML+=
                                    "<div style=\"float:right\">" +
                                         "<div style=\"float:left\">"+
                                              realFileName+"<br/>"+
                                              "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\" href=\"/util/downfile?uuidFileName="+newContent+"\">下载</a>"+
                                         "</div>"+
                                         " " +"<img src=\"" + myHeadPortrait + "\" width=\"50px\" height=\"50px\">"+
                                    "</div>" + "<br/><br/><br/>";
                            },
                            error:function () {
                                alert("queryHistoryMessage-error");
                            },dataType: "json",
                            type: "GET"

                        });
                    //图片或视频
                    }else if(state=="6"||state=="7"){
                        //获取后缀
                        var houZui = getType(content);
                        //是视频
                        if (houZui=="mp4"||houZui=="webm"||houZui=="ogg"){
                            document.getElementById("messageView").innerHTML +=
                                "<br/><br/><br/>"+
                                "<div style=\"float:right;\">" +
                                "<div style=\"float:left;margin-right: 5px;\">" +
                                "<video src=\"flage/"+content+"\" controls=\"controls\" style='width: 200px;height: 150px;'>" +
                                "</video>"+
                                "<br/>" +
                                "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + content + "\">下载</a>" +
                                "</div>" +
                                " "+
                                "<img src=\"" + myHeadPortrait + "\" width=\"50px\" height=\"50px\">"+
                                "</div>" + "<br/><br/><br/><br/><br/><br/>";
                        }else {
                            document.getElementById("messageView").innerHTML +=
                                "<br/><br/><br/>"+
                                "<div style=\"float:right;\">" +
                                "<div style=\"float:left;margin-right: 5px;\">" +
                                "<img src=\"flage/"+content+"\" style='width: 80px;height: 80px;'>" +
                                "</img>"+
                                "<br/>" +
                                "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + content + "\">下载</a>" +
                                "</div>" +
                                " "+
                                "<img src=\"" + myHeadPortrait + "\" width=\"50px\" height=\"50px\">"+
                                "</div>" + "<br/><br/><br/><br/><br/><br/>";
                        }

                    //是语音
                    }else {
                        //根据id查时间
                        $.ajax({
                            url:"/findTheTimeById",
                            data:{"uuidAudioName":content},
                            async:false,
                            success:function (data) {
                                var theTime = data.theTime;
                                document.getElementById("messageView").innerHTML+=
                                    "<br/><br/><br/>"+
                                    "<div style=\"float:right\">" +
                                    "<div style=\"float:left\">"+
                                    "<div class='warper'><div id="+content+" class='voiceItem'>"+theTime+"s ))></div></div>"+
                                    "</div>"+
                                    "  " +"<img src=\"" + myHeadPortrait + "\" width=\"50px\" height=\"50px\">" +
                                    "</div>" + "<br/><br/><br/>";
                            },
                            error:function () {
                                alert("queryHistoryMessage-error");
                            },
                            dataType:"json",
                            type:"GET"
                        });
                    }
                }

            }
            //滚动条默认显示在最下方
            $('#messageView')[0].scrollTop =$('#messageView')[0].scrollHeight;
        },
        error:function () {
            alert("openChat-error");
        },
        dataType: "json",
        type: "GET"
    });

}

//添加当前用户到聊天列表
function addMessageList(accountNumber,nickName,headPortrait,lastContent){
    $.ajax({
        url: "message/addMessageList",
        data: {"accountNumber":accountNumber},
        success:function(data) {
            var str = data.message;
            if(str=="alreadyExist"){
                //已经存在于列表里面
            }else {
                $('#MessageList').prepend(
                    "<div id=\""+accountNumber+"\" style=\"width: 350px;position: relative;height: 60px\" onmouseenter=\"myMouseEnter(this)\" onmouseleave=\"myMouseLeave(this)\">"+
                    "<div style=\"float:left;width: 100%;height: 60px\" onclick=\"openChat('"+nickName+"',"+accountNumber+",'"+headPortrait+"')\">"+
                    "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\"  style=\"float: left\" >"+
                    "<div style='float: left'>"+
                    "<div style=\"margin-left: 10px;width: 200px;font-size:20px;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">"+nickName+"</div>"+
                    "<div style=\"margin-left: 10px;width: 200px;margin-top: 5px;font-size:15px;color:#D8D6D6;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">"+lastContent+"</div>"+
                    "</div>"+
                    "</div>"+
                    "<div id=\"closeButton1\" onclick=\"removeMessage("+accountNumber+",this)\" style=\"right: 10px;top: 12px;position: absolute\">"+
                    "<img style=\"border-radius: 50%\" src=\"https://i.postimg.cc/fb7R06Vf/qaa.jpg\" width=\"20px\" height=\"20px\">"+
                    "</div>"+
                    "</div>"
                );
                //关闭按钮隐藏
                document.getElementById('closeButton1').style.display = "none";
            }
        },
        error:function () {
            alert("addMessageList-error");
        },
        dataType: "json",
        type: "GET"
    });
}

//文件上传和下载
// $(function () {
    $("#asd").bind("change", function () {
        var formData = new FormData($('#uploadForm')[0]);
        $.ajax({
            type: 'post',
            url: "manage/excelImport.do",
            data: formData,
            async: false,
            cache: false,
            processData: false,
            contentType: false,
            success:function (data) {
                var uuidFileName = data.uuidFileName;
                var realFileName = data.realFileName;
                if(realFileName.length>15){
                    realFileName = realFileName.substring(0,6)+"..."+realFileName.substring(realFileName.indexOf('.')-6);
                }
                //自己也能下载
                document.getElementById("messageView").innerHTML+=
                    "<div style=\"float:right\">" +
                       "<div style=\"float:left\">"+
                           realFileName+"<br/>"+
                           "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName="+uuidFileName+"\">下载</a>"+
                       "</div>"+
                       "  " +"<img src=\"" + data.headPortrait + "\" width=\"50px\" height=\"50px\">" +
                    "</div>" + "<br/><br/><br/>";
                //滚动条默认显示在最下方
                $('#messageView')[0].scrollTop =$('#messageView')[0].scrollHeight;
                //发送唯一文件名到对方,让对方也能下载
                sendFile(uuidFileName,realFileName);
            },
            error:function () {
                alert("上传失败");
            }
        });
    });
// });
$("#upload").click(function () {
    $("#asd").trigger("click");
});

//发送文件
function sendFile(uuidFileName,realFileName) {
    //获取我的头像路径
    var headPortrait = document.getElementById("headPortrait").src;
    var toNumber = document.getElementById("toAccountNumber").innerHTML;
    //长度小于8位是用户,否则是群
    //以Y开头C结尾表示文件
    if(toNumber.length<8) {
        stompClient.send("/app/userChat", {}, JSON.stringify({
            'headPortrait': headPortrait,
            'content': "Y" + uuidFileName + "C",
            'toNumber': toNumber
        }));
    }else {
        stompClient.send("/app/all", {}, JSON.stringify({
            'headPortrait': headPortrait,
            'content': "Y" + uuidFileName + "C",
            'toNumber': toNumber
        }));
    }
    //显示最后一条消息到列表中
    document.getElementById(toNumber).firstElementChild.lastElementChild.lastElementChild.innerText=decodeURIComponent(realFileName);
}

function openGroupInformation() {
    //群号
    var groupNumber = document.getElementById("toAccountNumber").innerHTML;
    if(groupNumber.length<8){
        alert("发现彩蛋,还不是时候");
    }else {
        $.ajax({
            url:"unclassified/findUsersByGroupNumber",
            data:{"groupNumber":groupNumber},
            success:function (data) {
                document.getElementById('groupInformation').innerHTML = "";
                document.getElementById('groupInformation').style.display="block";
                //添加好友按钮
                $('#groupInformation').prepend(
                    "<div onclick=\"addGroupFriends('"+groupNumber+"')\" style=\"float: left; margin-top: 5px;margin-left: 10px\">\n" +
                    "    <img src=\"https://i.postimg.cc/sgdL3ZWv/u-3834252561-3672809101-fm-26-gp-0.jpg\" width=\"40px\" height=\"40px\"><br/>\n" +
                    "    <span>添加</span>\n" +
                    "</div>"
                );
                var sysUserList = data.sysUserList;
                for (var i=0;i<sysUserList.length;i++){
                    var headPortrait = sysUserList[i].headPortrait;
                    var nickName = sysUserList[i].nickName;
                    var accountNumber = sysUserList[i].accountNumber;
                    $('#groupInformation').append(
                        "<div onclick=\"details('"+accountNumber+"')\" style=\"float: left; margin-top: 5px;margin-left: 5px\">\n" +
                        "    <img src=\""+headPortrait+"\" width=\"40px\" height=\"40px\"><br/>\n" +
                        "    <span>"+nickName+"</span>\n" +
                        "</div>"
                    );
                }
            },
            error:function () {

            },
            dataType: "json",
        })
    }
}

function addGroupFriends() {
    // 新建群聊框显示
    document.getElementById('newGroupChat').style.display="block";
    document.getElementById('leftDiv').innerHTML = "";
    plusType = "2"; //表示要邀请好友
    $.ajax({
        url:"touser/findAllUser",
        success:function (data) {
            var listChar = data.listChar;
            for (var i=0;i<listChar.length;i++){
                var fistChar = listChar[i].toLocaleUpperCase();
                //大写首字母
                $('#leftDiv').append(
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
                    $('#leftDiv').append(
                        "<br/>"+
                        "<div onclick=\"toRightDiv("+accountNumber+",'"+headPortrait+"','"+nickName+"')\" style=\"margin-top: 20px;margin-left: 15px\">" +
                        "<img style=\"float: left\" src=\"" + headPortrait + "\" width=\"40px\" height=\"40px\">" +
                        "<span style=\"margin-left: 18px;margin-top: 10px;float: left\">"+nickName+"" +
                        "</span>" +
                        "</div>"+
                        "<br/>"
                    );
                }
            }

        },
        error:function () {
            alert("addGroupFriends-error");
        },
        dataType: "json",
        type: "GET"
    });

}

$("#sendImage").bind("change", function () {
    var formData = new FormData($('#sendImageForm')[0]);
    if (this.files.length != 0){
        $.ajax({
            url: "file/sendImageOrVideo",
            type: 'POST',
            cache: false,
            data: formData,
            processData: false,
            contentType: false,
            success:function (data) {
                var uuidFileName = data.uuidFileName;
                var headPortrait = data.headPortrait;
                var type = data.type;
                //视频
                if (type==1) {
                    document.getElementById("messageView").innerHTML +=
                        "<br/><br/><br/>"+
                        "<div style=\"float:right;\">" +
                        "<div style=\"float:left;margin-right: 5px;\">" +
                        "<video src=\"flage/"+uuidFileName+"\" controls=\"controls\" style='width: 200px;height: 150px;'>" +
                        "</video>"+
                        "<br/>" +
                        "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + uuidFileName + "\">下载</a>" +
                        "</div>" +
                        "  " + "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">" +
                        "</div>" + "<br/><br/><br/><br/><br/><br/>";
                    //发送给对方
                    sendImageOrVideo(uuidFileName);
                }
                else {
                    document.getElementById("messageView").innerHTML +=
                        "<br/><br/><br/>"+
                        "<div style=\"float:right\">" +
                        "<div style=\"float:left\">" +
                        "<img src=\"flage/"+uuidFileName+"\" width='80px' height='80px'>" +
                        "</img>"+
                        "<br/>" +
                        "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + uuidFileName + "\">下载</a>" +
                        "</div>" +
                        "  " + "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">" +
                        "</div>" + "<br/><br/><br/><br/><br/><br/>";
                    //发送给对方
                    sendImageOrVideo(uuidFileName);
                }
                //滚动条默认显示在最下方
                $('#messageView')[0].scrollTop = $('#messageView')[0].scrollHeight;
            },
            error:function () {
                alert("sendImageOrVideo-error");
            }
        });
    }
});

$("#sendImageBtn").click(function () {
    $("#sendImage").trigger("click");
});

function sendImageOrVideo(uuidFileName) {
    //获取我的头像路径
    var headPortrait = document.getElementById("headPortrait").src;
    var toNumber = document.getElementById("toAccountNumber").innerHTML;
    //长度小于8位是用户,否则是群
    //以Y开头Z结尾表示视频或图片
    if(toNumber.length<8) {
        stompClient.send("/app/userChat", {}, JSON.stringify({
            'headPortrait': headPortrait,
            'content': "Y" + uuidFileName + "Z",
            'toNumber': toNumber
        }));
    }else {
        stompClient.send("/app/all", {}, JSON.stringify({
            'headPortrait': headPortrait,
            'content': "Y" + uuidFileName + "Z",
            'toNumber': toNumber
        }));
    }
    //显示最后一条消息到列表中
    document.getElementById(toNumber).firstElementChild.lastElementChild.lastElementChild.innerText=decodeURIComponent("[图片]");
}

function getType(file){
    var filename=file;
    var index1=filename.lastIndexOf(".");
    var index2=filename.length;
    var type=filename.substring(index1+1,index2);
    return type;
}