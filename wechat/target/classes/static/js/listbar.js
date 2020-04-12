
//  搜索结果列表隐藏
document.getElementById('SearchResultsList').style.display="none";
//  新建群聊框隐藏
document.getElementById('newGroupChat').style.display="none";
//  隐藏群聊列表
document.getElementById('GroupChatList').style.display="none";

//显示出消息列表数据
$.ajax({
    url: "message/showMessageList",
    async: false,
    success:function(data) {
        var MessageList = data.MessageList;
        var lastMessageList = data.lastMessageList;
        for (var i = 0; i < MessageList.length; i++) {
            var nickName = MessageList[i].nickName;
            var headPortrait = MessageList[i].headPortrait;
            var accountNumber = MessageList[i].accountNumber;
            var lastContent = decodeURIComponent(lastMessageList[i]);
            $('#MessageList').prepend(
                "<div id=\""+accountNumber+"\" style=\"width: 350px;position: relative;height: 60px\" onmouseenter=\"myMouseEnter(this)\" onmouseleave=\"myMouseLeave(this)\">"+
                    "<div style=\"float:left;width: 100%;height: 60px\" onclick=\"openChat('"+nickName+"',"+accountNumber+",'"+headPortrait+"')\">"+
                        "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\"  style=\"float: left\" >"+
                        "<div style='float: left'>"+
                            "<div style=\"margin-left: 10px;width: 200px;font-size:20px;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">"+nickName+"</div>"+
                            "<div style=\"margin-left: 10px;width: 200px;margin-top: 5px;font-size:15px;color:#D8D6D6;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">"+lastContent+"</div>"+
                        "</div>"+
                    "</div>"+
                    "<div id=\"closeButton\" onclick=\"removeMessage("+accountNumber+",this)\" style=\"right: 10px;top: 12px;position: absolute\">"+
                        "<img style=\"border-radius: 50%\" src=\"https://i.postimg.cc/fb7R06Vf/qaa.jpg\" width=\"20px\" height=\"20px\">"+
                    "</div>"+
                "</div>"
            );
            //关闭按钮隐藏
            document.getElementById('closeButton').style.display="none";
        }
    },
    error:function () {
        alert("showMessageList-error");
    },
    dataType: "json",
    type: "GET"
});

function myMouseEnter(data) {
    data.lastElementChild.style.display="block";

}
function myMouseLeave(data) {
    data.lastElementChild.style.display="none";
}

function removeMessage(accountNumber,data) {
    data.parentNode.remove();
    $.ajax({
        url: "message/removeMessageList",
        data: {"accountNumber":accountNumber},
        success:function (data) {

        },
        error:function () {
            alert("removeMessage-error");
        },
        dataType: "json",
        type: "GET"
    })
}

//查询群聊列表数据
$.ajax({
    url: "unclassified/findMyGroup",
    success:function (data) {
        var GroupList = data.GroupList;
        var lastMessageList = data.lastMessageList;
        for (var i=0;i<GroupList.length;i++){
            var groupNumber = GroupList[i].groupNumber;
            var groupName = GroupList[i].groupName;
            var groupCount = GroupList[i].groupCount;
            var lastContent = decodeURIComponent(lastMessageList[i]);
            $('#GroupChatList').prepend(
                "<div id=\""+groupNumber+"\" style=\"width: 350px;position: relative;height: 60px\">"+
                "<div style=\"float:left;width: 100%;height: 60px\" onclick=\"openGroupChat('"+groupName+"',"+groupNumber+",'"+groupCount+"')\">"+
                "<img src=\"https://i.postimg.cc/VNHkbZch/u-153836249-1450345530-fm-26-gp-0.jpg\" width=\"50px\" height=\"50px\"  style=\"float: left\" >"+
                "<div style='float: left'>"+
                "<div style=\"margin-left: 10px;width: 200px;font-size:20px;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">"+groupName+"</div>"+
                "<div style=\"margin-left: 10px;width: 200px;margin-top: 5px;font-size:15px;color:#D8D6D6;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">"+lastContent+"</div>"+
                "</div>"+
                "</div>"+
                "</div>"
            );

        }
    },
    error:function () {
        alert("findMyGroup-error");
    },
    dataType: "json",
    type: "GET"
});

//查询出发给我的未读的消息,并把消息状态设置为已读
$.ajax({
    url: "message/findUnreadMessage",
    success:function (data) {
        var unreadMessageList = data.unreadMessageList;
        for(var i=0;i<unreadMessageList.length;i++){
            var fromNumber = unreadMessageList[i].fromNumber;
            //用户已存在于列表
            if(document.getElementById(fromNumber)){
                //显示最后一条消息到列表中
                document.getElementById(fromNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent(unreadMessageList[i].content);
            }else{
                $.ajax({
                    url: "touser/findUserByName",
                    data:{"accountNumber":fromNumber},
                    async: false,
                    success:function (data) {
                        var user = data.sysUser;
                        addMessageList(user.accountNumber,user.nickName,user.headPortrait,unreadMessageList[i].content);
                    }
                });
            }
        }
    },
    error:function () {
        alert("findUnreadMessage-error");
    },
    dataType: "json",
    type: "GET"
});


//查询出发给我的未读的邀请消息,并把消息状态设置为已读
$.ajax({
    url:"unclassified/findUnreadInvitation",
    success:function(data) {
        var NotifyMessageList = data.NotifyMessageList;
        if (NotifyMessageList!=null&&NotifyMessageList!=""){
        for(var i=0;i<NotifyMessageList.length;i++){
            var myName = NotifyMessageList[i].myName;
            var groupName = NotifyMessageList[i].groupName;
            var groupNumber = NotifyMessageList[i].groupNumber;
            var toNumber = NotifyMessageList[i].toNumber;
            var flag = theChoice(myName,groupName);
            if (flag==true){
                var cc = "";
                $.ajax({
                    url:"unclassified/judgeMe",
                    async: false,
                    data:{'groupNumber': groupNumber},
                    success:function (data) {
                        cc = data.cc;
                    },
                    error:function () {
                        alert("updateGroupCount-error");
                    },
                    dataType:"json",
                });

                if (cc=="1"){
                    alert("已加入群聊");
                }else{
                    //群人数加1
                    $.ajax({
                        url:"unclassified/updateGroupCount",
                        data:{'groupNumber': groupNumber},
                        success:function (data) {

                        },
                        error:function () {
                            alert("updateGroupCount-error");
                        },
                        dataType:"json",
                    });
                    //添加进群成员
                    $.ajax({
                        url:"unclassified/addGroupMember",
                        data:{'groupNumber': groupNumber,"accountNumber":toNumber},
                        success:function (data) {

                        },
                        error:function () {
                            alert("addGroupMember-error");
                        },
                        dataType:"json",
                    });
                    //添加到群聊列表里面
                    GroupChat();
                    $.ajax({
                        url:"unclassified/findGroupChatByNumber",
                        data:{"groupNumber":groupNumber},
                        success:function (data) {
                            var count = data.count;
                            $('#GroupChatList').prepend(
                                "<div id=\""+groupNumber+"\" style=\"width: 350px;position: relative;height: 60px\">"+
                                "<div style=\"float:left;width: 100%;height: 60px\" onclick=\"openGroupChat('"+groupName+"',"+groupNumber+",'"+count+"')\">"+
                                "<img src=\"https://i.postimg.cc/VNHkbZch/u-153836249-1450345530-fm-26-gp-0.jpg\" width=\"50px\" height=\"50px\"  style=\"float: left\" >"+
                                "<div style='float: left'>"+
                                "<div style=\"margin-left: 10px;width: 200px;font-size:20px;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">"+groupName+"</div>"+
                                "<div style=\"margin-left: 10px;width: 200px;margin-top: 5px;font-size:15px;color:#D8D6D6;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\"></div>"+
                                "</div>"+
                                "</div>"+
                                "</div>"
                            );
                        },
                        error:function () {
                            alert("addGroupMember-error");
                        },
                        dataType:"json"
                    });
                }

            }
        }
        }
    },
    error:function () {
        alert("findUnreadInvitation-error");
    },
    dataType:"json",
    type: "GET"
});

//搜索用户
function search() {
    var word = $("#search").val();
    if(word.trim()=="")
        return;
    $.ajax({
        url: "touser/findUserByWord",
        data: {"word":word},
        success:function(data) {
            var SearchListUser = data.SearchListUser;
            //联系人列表隐藏
            document.getElementById('UserList').style.display="none";
            //消息列表隐藏
            document.getElementById('MessageList').style.display="none";
            //搜索结果列表
            document.getElementById('SearchResultsList').innerHTML = "";
            //搜索结果列表显示
            document.getElementById('SearchResultsList').style.display="block";
            //添加进搜索结果列表
            for(var i=0;i<SearchListUser.length;i++){
                var headPortrait = SearchListUser[i].headPortrait;
                var nickName = SearchListUser[i].nickName;
                var accountNumber = SearchListUser[i].accountNumber;
                $('#SearchResultsList').append(
                    "<div onclick=\"details("+accountNumber+")\" style=\"margin-top: 20px;margin-left: 15px\">" +
                    "<img style=\"float: left\" src=\"" + headPortrait + "\" width=\"40px\" height=\"40px\">" +
                    "<span style=\"margin-left: 18px;float: left\">"+nickName+"" +
                    "</span>" +
                    "<br/>"+
                    "<span style=\"margin-left: 18px;float: left\">账号: "+accountNumber+"" +
                    "</span>"+
                    "</div>"+
                    "<br/><br/>"
                );
            }
        },
        error:function () {
            alert("search-error");
        },
        dataType:"json",
        type: "GET"
    });

}

function details(accountNumber) {

    // 用户详情显示
    document.getElementById('UserDetail').style.display="block";
    // 聊天框隐藏
    document.getElementById('ChatBox').style.display="none";
    // 聊天框名字隐藏
    document.getElementById('chatName').style.display="none";
    //  省略号隐藏
    document.getElementById('ellipsis').style.display="none";
    document.getElementById('groupInformation').style.display="none";
    document.getElementById('theSpace').style.display="none";
    document.getElementById('friendsSpace').style.display="none";
    document.getElementById('thePublish').style.display="none";
    document.getElementById('GoBang').style.display="none";
    document.getElementById('GoBangInviteList').style.display="none";
    document.getElementById('Checkerboard').style.display="none";
    $.ajax({
        url: "touser/findUserByName",
        data: {"accountNumber":accountNumber},
        success:function(data) {
            var user = data.sysUser;
            //清空div
            document.getElementById('UserDetail').innerHTML = "";
            $('#UserDetail').prepend(
                "    <span style=\"margin-top: 60px;margin-left: 60px\">"+user.nickName+"</span> <img src=\"" + user.headPortrait + "\" width=\"60px\" height=\"60px\" style=\"margin-top: 60px;margin-right: 60px\"><br/>\n" +
                "    <hr/>\n" +
                "    <div style=\"margin-top: 30px;margin-bottom: 30px;margin-left: 60px\">\n" +
                "         <span>性别: "+user.sex+" </span><br/>\n" +
                "         <span>地区: "+user.region+" </span><br/>\n" +
                "         <span>签名: "+user.signature+" </span><br/>\n" +
                "    </div>\n" +
                "    <hr/>\n" +
                "    <div style=\"margin-top: 30px;margin-left: 120px\">\n" +
                "         <button onclick=\"toSendMessage('"+user.nickName+"',"+user.accountNumber+",'"+user.headPortrait+"')\">发消息</button>\n" +
                "    </div>"
            );
        },
        error:function () {
            alert("details-error");
        },
        dataType: "json",
        type: "GET"
    });
}

//用来存储要创建群聊的人的账号
var rightDivList = [];
//储存弹框类型,1表示是新建群聊，2表示是邀请好友
var plusType;
function addGroup() {
    // 新建群聊框显示
    document.getElementById('newGroupChat').style.display="block";
    plusType = "1";
    $.ajax({
        url:"touser/findAllUser",
        success:function (data) {
            document.getElementById('leftDiv').innerHTML = "";
            document.getElementById('leftDiv').innerHTML = "<input id=\"groupChatName\" placeholder=\"群聊名称\" type=\"text\"/>";
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
            alert("addGroup-error");
        },
        dataType: "json",
        type: "GET"
    });
}

function toRightDiv(accountNumber,headPortrait,nickName) {
    var flag = 0;
    for (var i = 0; i < rightDivList.length; i++) {
        if (rightDivList[i] == accountNumber) {
            flag = 1;
        }
    }
    if (flag==0){
        rightDivList.push(accountNumber);
        $('#rightDiv').append(
            "<div style=\"margin-top: 20px;margin-left: 10px\">" +
            "<img style=\"float: left\" src=\"" + headPortrait + "\" width=\"40px\" height=\"40px\">" +
            "<span style=\"margin-left: 10px;margin-top: 10px;float: left\">"+nickName+"" +
            "</span>" +
            "<span onclick=\"removeUser("+accountNumber+",this)\" style=\"margin-left: 10px;margin-bottom: 5px\">"+
            "<img style=\"border-radius: 50%\" src=\"https://i.postimg.cc/fb7R06Vf/qaa.jpg\" width=\"20px\" height=\"20px\">"+
            "</span>"+
            "</div>"
        );
    }
}

function removeUser(accountNumber,data) {
    for (var i = 0; i < rightDivList.length; i++) {
        if (rightDivList[i] == accountNumber) {
            rightDivList.splice(i, 1);
        }
    }
    data.parentNode.remove();
}

//创建按钮
function toEstablish() {
    //表示新建群
    if (plusType=="1") {
        var groupName = document.getElementById('groupChatName').value;
        if (groupName == null || groupName == "") {
            alert("群名不能为空");
        } else {
            // 新建群聊框关闭
            document.getElementById('newGroupChat').style.display = "none";
            //创建群,返回群号
            $.ajax({
                url: "unclassified/newGroupChat",
                data: {"groupName": groupName},
                async: false,
                success: function (data) {
                    var groupNumber = data.groupNumber;
                    //给每个人发邀请消息
                    for (var i = 0; i < rightDivList.length; i++) {
                        stompClient.send("/app/invitation", {}, JSON.stringify({
                            'groupName': groupName,
                            'toNumber': rightDivList[i],
                            'groupNumber': groupNumber,
                        }));
                    }

                    var count = 1;
                    //添加到群聊列表里面
                    GroupChat();
                    $('#GroupChatList').prepend(
                        "<div id=\"" + groupNumber + "\" style=\"width: 350px;position: relative;height: 60px\">" +
                        "<div style=\"float:left;width: 100%;height: 60px\" onclick=\"openGroupChat('" + groupName + "'," + groupNumber + ",'" + count + "')\">" +
                        "<img src=\"https://i.postimg.cc/VNHkbZch/u-153836249-1450345530-fm-26-gp-0.jpg\" width=\"50px\" height=\"50px\"  style=\"float: left\" >" +
                        "<div style='float: left'>" +
                        "<div style=\"margin-left: 10px;width: 200px;font-size:20px;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\">" + groupName + "</div>" +
                        "<div style=\"margin-left: 10px;width: 200px;margin-top: 5px;font-size:15px;color:#D8D6D6;white-space:nowrap;overflow:hidden;text-overflow: ellipsis\"></div>" +
                        "</div>" +
                        "</div>" +
                        "</div>"
                    );

                },
                error: function () {
                    alert("toEstablish-error");
                },
                dataType: "json",
                type: "GET"
            });
        }
    }else{
        //表示要邀请好友
        //给每个人发邀请消息
        $.ajax({
            url:"unclassified/findGroup",
            data:{"groupNumber":document.getElementById("toAccountNumber").innerHTML},
            async: false,
            success:function (data) {
                var groupChat = data.groupChat;

                for (var i = 0; i < rightDivList.length; i++) {
                    stompClient.send("/app/invitation", {}, JSON.stringify({
                        'groupName': groupChat.groupName,
                        'toNumber': rightDivList[i],
                        'groupNumber': document.getElementById("toAccountNumber").innerHTML,
                    }));
                }
            },
            error:function () {
                alert("findGroup-error");
            },
            dataType: "json",
        });
        document.getElementById('newGroupChat').style.display="none";

    }

}

//打开群聊窗口
function openGroupChat(groupName,groupNumber,count) {
    //清空聊天框名字
    document.getElementById('chatName').innerHTML = "";

    $('#chatName').prepend(
        "<span>"+groupName+" ("+count+")</span>"
    );
    //清空当前打开的人的账号
    document.getElementById('accountNumber').innerHTML = "";

    $('#accountNumber').prepend(
        "<p id='toAccountNumber'>"+groupNumber+"</p>"
    );
    //  聊天框名字显示
    document.getElementById('chatName').style.display="block";
    //  聊天框显示
    document.getElementById('ChatBox').style.display="block";
    //  省略号显示
    document.getElementById('ellipsis').style.display="block";
    //  清空聊天框
    document.getElementById('messageView').innerHTML = "";
    document.getElementById('UserDetail').style.display="none";
    document.getElementById('groupInformation').style.display="none";
    document.getElementById('theSpace').style.display="none";
    document.getElementById('friendsSpace').style.display="none";
    document.getElementById('thePublish').style.display="none";
    document.getElementById('GoBang').style.display="none";
    document.getElementById('GoBangInviteList').style.display="none";
    document.getElementById('Checkerboard').style.display="none";
    //查询历史消息
    $.ajax({
        url:"queryGroupMessage",
        data:{"groupNumber":groupNumber},
        success:function (data) {
            var GroupMessageList = data.GroupMessageList;
            var myNumber = data.myNumber;
            for (var i=0;i<GroupMessageList.length;i++){
                var fromNumber = GroupMessageList[i].fromNumber;
                var content = GroupMessageList[i].content;
                var state = GroupMessageList[i].state;
                var headPortrait = data[fromNumber];
                //消息发送者是我
                if (fromNumber==myNumber){
                    //是普通文本消息
                    if(state=="0") {
                        //如果有表情就转化成表情
                        var newContent = showEmoji(decodeURIComponent(content));
                        document.getElementById("messageView").innerHTML +=
                            "<div style=\"float:right\">" + decodeURIComponent(newContent) + " " + "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">" + "</div>" + "<br/><br/><br/>";
                    //是文件
                    }else if(state=="1"){
                        //根据唯一文件名查真实文件名
                        $.ajax({
                            url:"/findRealFileName",
                            data:{"uuidFileName":content},
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
                                    "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\" href=\"/util/downfile?uuidFileName="+content+"\">下载</a>"+
                                    "</div>"+
                                    " " +"<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">"+
                                    "</div>" + "<br/><br/><br/>";
                            },
                            error:function () {
                                alert("queryHistoryMessage-error");
                            },dataType: "json",
                            type: "GET"

                        });
                        //图片视频
                    }else if(state=="3"){
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
                                "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">"+
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
                                "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">"+
                                "</div>" + "<br/><br/><br/><br/><br/><br/>";
                        }

                    //语音
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
                                    "  " +"<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">" +
                                    "</div>" + "<br/><br/><br/>";
                            },
                            error:function () {
                                alert("queryHistoryMessage-error");
                            },
                            dataType:"json",
                            type:"GET"
                        });
                    }
                }else{
                    //发送者是别人
                    //是普通文本消息
                    if(state=="0") {
                        //如果有表情就转化成表情
                        var newContent = showEmoji(decodeURIComponent(content));
                        document.getElementById("messageView").innerHTML +=
                            "<div style=\"float:left\">" + "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">" + " " + decodeURIComponent(newContent) + "</div>" + "<br/><br/><br/>";

                    }else if(state=="1"){
                        //是文件
                        //根据唯一文件名查真实文件名
                        $.ajax({
                            url:"/findRealFileName",
                            data:{"uuidFileName":content},
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
                                    "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\" href=\"/util/downfile?uuidFileName="+content+"\">下载</a>"+
                                    "</div>"+
                                    "</div>" + "<br/><br/><br/>";
                            },
                            error:function () {
                                alert("queryHistoryMessage-error");
                            },dataType: "json",
                            type: "GET"

                        });
                    }else if(state=="3"){
                        //图片视频
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


                    }else{
                        //是语音
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
            }
        },error:function () {
          alert("queryGroupMessage-error");
        },
        dataType: "json",
        type: "GET"
    });
}

//取消
function toClose() {
    // 新建群聊框关闭
    document.getElementById('newGroupChat').style.display="none";
}

//查询我自己,加载到朋友圈页面
//添加自己发表按钮
$.ajax({
    url: "space/findMe",
    success: function (data) {
        var user = data.sysUser;
        var myName = user.nickName;
        var myHeadPortrait = user.headPortrait;
        $('#theSpace').prepend(
            "<img src=\"https://i.postimg.cc/8cVtSzTQ/3516318-EB41-BC907-E80-B5881-B0-E3-B9-DF7197-C88-B-size693-w500-h270.gif\" width=\"1060px\" height=\"300px\" style=\"position:absolute;left: 456px;top: 1px\">\n" +
            "<span style=\"position:absolute;right: 200px;top: 280px;\">"+myName+"</span>\n" +
            "<img onclick='publish()' src=\""+myHeadPortrait+"\" style=\"position:absolute;left: 1350px;top: 270px;\" width=\"60px\" height=\"60px\">"
        );
    },
    error: function () {
        alert("findUserByName-error");
    },
    dataType: "json",
});

$(function () {
    $("#toPublish").click(function () {
        var formData = new FormData($('#publishFrom')[0]);
        var thePublishMessage = document.getElementById('thePublishMessage').value;
        formData.append("thePublishMessage",thePublishMessage);
        $.ajax({
            type: 'post',
            url: "space/publish",
            data: formData,
            async: false,
            cache: false,
            processData: false,
            contentType: false,
            success:function (data) {
                var headPortrait = data.headPortrait;
                var nickname = data.nickname;
                var thePublishMessage = data.thePublishMessage;
                var fileList = data.fileList;
                var fileTypeList = data.fileTypeList;
                var theDate = data.theDate;
                var id = data.id;

                $('#friendsSpace').prepend(
                    "<br/>"+
                    "<div id='"+id+"'>" +
                    "</div>"+
                    "<br/>"+
                    "<hr/>"
                );

                $('#'+id).append(
                    "                <div style='float: left'>" +
                    "                    <img src=\""+headPortrait+"\" width=\"40px\" height=\"40px\"> " +
                    "                </div>" +
                    "                <div style='float: left'>" +
                    "                    <div>"+nickname+"</div>" +
                    "                    <div>"+thePublishMessage+"</div>" +
                    "                </div>"
                );
                for (var i=0;i<fileList.length;i++){
                    var route = fileList[i];
                    var theType = fileTypeList[i];

                    if (theType=="0") {
                        $('#'+id).append(
                            "<img src=\"flage/" + id + "/" + route + "\" height=\"80px\" width=\"80px\">"
                        );

                    }else {
                        $('#'+id).append(
                            "<video src=\"flage/" + id + "/" + route + "\" controls=\"controls\" height=\"100px\" width=\"100px\">" +
                                "您的浏览器不支持 video 标签。\n" +
                            "</video>"
                        );
                    }

                }
                $('#'+id).append(
                    "                <div>"+theDate+"</div>" +
                    "                <div onclick=\"agree('"+nickname+"',this)\">" +
                    "                    <img src=\"https://i.postimg.cc/kgn14c95/u-55912192-2104110857-fm-15-gp-0.jpg\" height=\"20px\" width=\"20px\" style=\"border-radius: 50%\">" +
                    "                    <span>"+
                    "                    </span>"+
                    "                </div>" +
                    "                <span onclick=\"Comment('"+nickname+"',this)\">" +
                    "                    <img src=\"https://i.postimg.cc/nrN6QQFL/1.jpg\" height=\"20px\" width=\"20px\" style=\"border-radius: 50%\">" +
                                         "<div>"+
                                         "</div>"+
                    "                </span>"
                );

            },
            error:function () {
                alert("上传失败");
            }
        });
    });
    document.getElementById('thePublish').style.display="none";
});

//查询朋友动态
$.ajax({
    url:"space/findSpace",
    success:function (data) {
        var dynamicList = data.dynamicList;
        var myName = data.myName;
        var dateList = data.dateList;

        for (var i=0;i<dynamicList.length;i++){
            var id = dynamicList[i].id;
            var sysUser = data["SysUser"+id];
            var headPortrait = sysUser.headPortrait;
            var nickname = sysUser.nickName;
            var thePublishMessage = dynamicList[i].theTitle;
            var fileList = data["Image"+id];
            var fileTypeList = data["ImageType"+id];
            var theDate = dateList[i];
            var dynamicCommentList = data["Comment"+id];
            var praisePeople = dynamicList[i].praisePeople;

            $('#friendsSpace').append(
                "<hr/>"+
                "<br/>"+
                "<div id='"+id+"'>" +
                "</div>"+
                "<br/>"+
                "<hr/>"
            );

            $('#'+id).append(
                "                <div style='float: left'>" +
                "                    <img src=\""+headPortrait+"\" width=\"40px\" height=\"40px\"> " +
                "                </div>" +
                "                <div style='float: left'>" +
                "                    <div>"+nickname+"</div>" +
                "                    <div>"+thePublishMessage+"</div>" +
                "                </div>"
            );

            $('#'+id).append(
                "<br/>"+
                "<br/>"+
                "<br/>"+
                "<div id=\"tu"+id+"\" style=\"float: left;margin-left: 2px\">" +
                "</div>"
            );

            for (var k=0;k<fileList.length;k++){
                var route = fileList[k];
                var theType = fileTypeList[k];
                if (theType=="0") {
                    $('#tu'+id).append(
                        "<img src=\"flage/" + id + "/" + route + "\" height=\"80px\" width=\"80px\">"
                    );

                }else {
                    $('#tu'+id).append(
                        "<video src=\"flage/" + id + "/" + route + "\" controls=\"controls\" height=\"100px\" width=\"100px\">" +
                        "您的浏览器不支持 video \n" +
                        "</video>"
                    );
                }

            }

            $('#'+id).append(
                "<br/>"+
                "<br/>"+
                "<br/>"+
                "<br/>"+
                "<br/>"+
                //"<br/>"+
                "                <div style=\" margin-left: 10px;font-size:15px;color:#D8D6D6\">"+theDate+"</div>" +
                "                <div style=\" margin-left: 10px;\" onclick=\"agree('"+myName+"',this)\">" +
                "                    <img src=\"https://i.postimg.cc/kgn14c95/u-55912192-2104110857-fm-15-gp-0.jpg\" height=\"20px\" width=\"20px\" style=\"border-radius: 50%\">" +
                "                    <span>"+
                "                    </span>"+
                "                </div>" +
                "                <span style=\" margin-left: 10px;\" onclick=\"Comment('"+myName+"',this)\">" +
                "                    <img src=\"https://i.postimg.cc/nrN6QQFL/1.jpg\" height=\"20px\" width=\"20px\" style=\"border-radius: 50%\">" +
                "                    <div>"+
                "                    </div>"+
                "                </span>"
            );

            //添加点赞和评论
            for(var j=0;j<dynamicCommentList.length;j++) {
                var commentPeople = dynamicCommentList[j].commentPeople;
                var commentContent = dynamicCommentList[j].commentContent;
                document.getElementById(id).lastElementChild.lastElementChild.innerHTML += commentPeople + ": " + commentContent+"<br>";
            }
            if(praisePeople==null){
                document.getElementById(id).lastElementChild.previousElementSibling.lastElementChild.innerHTML = "";
            }
            else {
                document.getElementById(id).lastElementChild.previousElementSibling.lastElementChild.innerHTML = praisePeople;
            }
        }

    },
    error:function () {

    },
    dataType: "json",
});

function publish() {
    document.getElementById('thePublish').style.display="block";

}

function openSpace() {
    document.getElementById('theSpace').style.display="block";
    document.getElementById('friendsSpace').style.display="block";
    document.getElementById('space').style.display="none";
    document.getElementById('space1').style.display="block";
    document.getElementById('lianxiren').style.display="block";
    document.getElementById('lianxiren1').style.display="none";
    document.getElementById('xiaoxi').style.display="none";
    document.getElementById('xiaoxi1').style.display="block";
    document.getElementById('GroupChat').style.display="block";
    document.getElementById('GroupChat1').style.display="none";
    //  显示联系人列表
    document.getElementById('UserList').style.display="none";
    //  聊天框隐藏
    document.getElementById('ChatBox').style.display="none";

    //  聊天框名字隐藏
    document.getElementById('chatName').style.display="none";
    //  省略号隐藏
    document.getElementById('ellipsis').style.display="none";
    document.getElementById('groupInformation').style.display="none";
    // 用户详情隐藏
    document.getElementById('UserDetail').style.display="none";
    document.getElementById('game').style.display="block";
    document.getElementById('game1').style.display="none";
    document.getElementById('GoBang').style.display="none";
    document.getElementById('GoBangInviteList').style.display="none";
    document.getElementById('Checkerboard').style.display="none";

}

function agree(nickname,data) {

    if(data.lastElementChild.innerHTML.indexOf(nickname) != -1){

    }else {
        var theId = data.parentNode.id;
        data.lastElementChild.innerHTML += "," + nickname;
        var names = data.lastElementChild.innerHTML.trim();
        //点赞人添加到数据库
        $.ajax({
            url: "space/addAgree",
            data: {"theId": theId, "names": names},
            success: function (data) {

            },
            error: function () {
                alert("addAgree-error");
            },
            dataType: "json"
        });
    }
}

function Comment(nickname,data) {
    var theId  = data.parentNode.id;
    var str=prompt("评论","说点什么吧");
    if(str) {
        data.lastElementChild.innerHTML+=nickname+": "+str+"<br>";
        //评论添加到数据库
        $.ajax({
            url:"space/addComment",
            data:{"str":str,"nickname":nickname,"theId":theId},
            success:function (data) {

            },
            error:function () {
                alert("addComment-error");
            },
            dataType:"json"
        });
    }
}