var socket = new SockJS('/coordination');
var stompClient = Stomp.over(socket);
//向服务器发起websocket连接并发送CONNECT帧
stompClient.connect(
    '',
    function (frame) {
        // 连接成功时（服务器响应 CONNECTED 帧）的回调方法
        //单聊订阅地址,前面加个user服务器就能找到,简直神奇,为甚恶魔呢
        stompClient.subscribe('/user/userChat/chat', function (chat) {
            //显示消息
            showChat(JSON.parse(chat.body));
        });
        //通知消息订阅地址
        stompClient.subscribe('/user/invitation/chat', function (chat) {
            //显示消息
            showInvitation(JSON.parse(chat.body));
        });
        //群聊消息订阅地址
        stompClient.subscribe('/user/all/chat', function (chat) {
            //显示消息
            showGroupMessage(JSON.parse(chat.body));
        });
        //游戏消息订阅地址
        stompClient.subscribe('/user/game/chat', function (chat) {
            //显示消息
            showGameMessage(JSON.parse(chat.body));
        });
    },
    function errorCallBack (error) {
        // 连接失败时（服务器响应 ERROR 帧）的回调方法
        alert('连接失败');
    }
);

//发送消息
function sendMessage() {
    var input = document.getElementById("message").value;
    //获取我的头像路径
    var headPortrait = document.getElementById("headPortrait").src;
    var toNumber = document.getElementById("toAccountNumber").innerHTML;
    //长度小于8位是用户,否则是群
    if(toNumber.length<8) {
        stompClient.send("/app/userChat", {}, JSON.stringify({
            'headPortrait': headPortrait,
            'content': encodeURIComponent(input),
            'toNumber': toNumber
        }));
    }else {
        stompClient.send("/app/all", {}, JSON.stringify({
            'headPortrait': headPortrait,
            'content': encodeURIComponent(input),
            'toNumber': toNumber
        }));
    }
    //清空发送框
    document.getElementById("message").value = "";
    //显示自己发送的消息
    var newInput = showEmoji(input);
    document.getElementById("messageView").innerHTML +=
        "<div style=\"float:right\">" + decodeURIComponent(newInput) + " " + "<img src=\"" + headPortrait + "\" width=\"50px\" height=\"50px\">" + "</div>" + "<br/><br/><br/>";
    //滚动条默认显示在最下方
    $('#messageView')[0].scrollTop = $('#messageView')[0].scrollHeight;
    //显示最后一条消息到列表中
    var toAccountNumber = document.getElementById("toAccountNumber").innerHTML;
    document.getElementById(toAccountNumber).firstElementChild.lastElementChild.lastElementChild.innerText = decodeURIComponent(input);

}

//把表情包文本转化为图片
function showEmoji(message) {
    var result = decodeURIComponent(message),
        regrex = /\[EMOJI:\d+\]/g,
        match;
    while (match = regrex.exec(message)){
        var emojiIndex = match[0].slice(7, -1);
        var emojiUrl = "image/emoji/" + emojiIndex + ".gif";
        result = result.replace(match[0], '<img src="' + emojiUrl + '" width=\"50px\" height=\"50px\"/>');
    }
    return result;
}

//显示别人发的消息
function showChat(map) {
    //如果别人发给我的消息的人是我当前打开这个窗口的名字就把消息显示出来
    var toAccountNumber = document.getElementById("toAccountNumber").innerHTML;
    if(map.message.fromNumber==toAccountNumber) {
        //是普通文本
        if(map.type=="1") {
            var newInput = showEmoji(decodeURIComponent(map.message.content));
            document.getElementById("messageView").innerHTML +=
                "<div style=\"float:left\">" + "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">" + " " + decodeURIComponent(newInput) + "</div>" + "<br/><br/><br/>";
        //是文件
        }else if (map.type=="3"){
            var uuidFileName = map.uuidFileName;
            var realFileName = map.realFileName;
            if(realFileName.length>15){
                realFileName = realFileName.substring(0,6)+"..."+realFileName.substring(realFileName.indexOf('.')-6);
            }
            document.getElementById("messageView").innerHTML+=
                 "<div style=\"float:left\">" +
                     "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+ "  "+
                     "<div style=\"float:right\">"+
                          realFileName+"<br/>"+
                          "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\" href=\"/util/downfile?uuidFileName="+uuidFileName+"\">下载</a>"+
                     "</div>"+
                 "</div>" + "<br/><br/><br/>";
        //图片视频
        }else if (map.type=="7"){
            //获取后缀
            var houZui = getType(map.uuidFileName);
            //是视频
            if (houZui=="mp4"||houZui=="webm"||houZui=="ogg"){
                document.getElementById("messageView").innerHTML +=
                    "<br/><br/><br/>"+
                    "<div style=\"float:left;\">" +
                    "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+"  " +
                    "<div style=\"float:right;margin-right: 5px;\">" +
                    "<video src=\"flage/"+map.uuidFileName+"\" controls=\"controls\" style='width: 200px;height: 150px;'>" +
                    "</video>"+
                    "<br/>" +
                    "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + map.uuidFileName + "\">下载</a>" +
                    "</div>" +
                    "</div>" + "<br/><br/><br/><br/><br/><br/>";
            }else {
                document.getElementById("messageView").innerHTML +=
                    "<br/><br/><br/>"+
                    "<div style=\"float:left;\">" +
                    "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+"  " +
                    "<div style=\"float:right;margin-right: 5px;\">" +
                    "<img src=\"flage/"+map.uuidFileName+"\" style='width: 80px;height: 80px;'>" +
                    "</img>"+
                    "<br/>" +
                    "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + map.uuidFileName + "\">下载</a>" +
                    "</div>" +
                    "</div>" + "<br/><br/><br/><br/><br/><br/>";
            }

            //是语音
        }else{
            var uuidAudioName = map.uuidAudioName;
            var theTime = map.theTime;
            document.getElementById("messageView").innerHTML+=
                "<br/><br/><br/>"+
                "<div style=\"float:left\">" +
                "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+ "  "+
                "<div style=\"float:right\">"+
                    "<div class='warper'><div id="+uuidAudioName+" class='voiceItem'><)) "+theTime+"s</div></div>"+
                "</div>"+
                "</div>" + "<br/><br/><br/>";
        }
        //滚动条默认显示在最下方
        $('#messageView')[0].scrollTop =$('#messageView')[0].scrollHeight;
    }

    //判断用户是否存在于消息列表里面
    if(document.getElementById(map.message.fromNumber)){
        //是普通文本
        if(map.type=="1"){
            //显示最后一条消息到列表中
            document.getElementById(map.message.fromNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent(map.message.content);
        //是文件
        }else if (map.type=="3"){
            //显示最后一条消息到列表中
            document.getElementById(map.message.fromNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent(map.realFileName);
        //图片视频
        }else if (map.type=="7"){
            //显示最后一条消息到列表中
            document.getElementById(map.message.fromNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent("[图片]");
            //是语音
        }else {
            //显示最后一条消息到列表中
            document.getElementById(map.message.fromNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent("[语音]");
        }
    }else{
        $.ajax({
            url: "touser/findUserByName",
            data:{"accountNumber":map.message.fromNumber},
            success:function (data) {
                var user = data.sysUser;
                //是普通文本
                if (map.type=="1") {
                    addMessageList(user.accountNumber, user.nickName, user.headPortrait, map.message.content);
                //是文件
                }else if (map.type=="3"){
                    addMessageList(user.accountNumber, user.nickName, user.headPortrait, map.realFileName);
                //图片视频
                }else if (map.type=="7"){
                    addMessageList(user.accountNumber, user.nickName, user.headPortrait, "[图片]");
                //是语音
                }else {
                    addMessageList(user.accountNumber, user.nickName, user.headPortrait, "[语音]");
                }
            }
        });
    }

    //执行到这代表用户在线,把接收到的消息和文件设为已读
    $.ajax({
        url:"message/changeMessageStatus",
        data:{'toNumber': map.message.toNumber},
        success:function (data) {

        },
        error:function () {
            alert("showChat-error");
        },
        dataType:"json",
    });

}

//显示通知消息
function showInvitation(map) {
    var myName = map.myName;
    var groupNumber = map.groupNumber;
    var groupName = map.groupName;
    var toNumber = map.toNumber;
    var flag = theChoice(myName,groupName);
    //同意了
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
                    alert("findGroupChatByNumber-error");
                },
                dataType:"json"
            });
        }
    }
    //执行到这代表用户在线,把接收到的消息设为已读
    $.ajax({
        url:"message/changeNotifyMessageStatus",
        data:{'toNumber': toNumber},
        success:function (data) {
            //console.log("消息已设置为已读");
        },
        error:function () {
            alert("changeNotifyMessageStatus-error");
        },
        dataType:"json",
    });
}

//选择框
function theChoice(myName,groupName){
    if(confirm(myName+" 邀请你加入群聊 "+groupName)){
        return true;
    }else{
        return false;
    }
}

function showGroupMessage(map) {
    //如果别人发给我的消息的人是我当前打开这个窗口的名字就把消息显示出来
    var toAccountNumber = document.getElementById("toAccountNumber").innerHTML;
    if(map.toNumber==toAccountNumber){
        //是普通文本
        if(map.type=="0") {
            var newInput = showEmoji(decodeURIComponent(map.content));
            document.getElementById("messageView").innerHTML +=
                "<div style=\"float:left\">" + "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">" + " " + decodeURIComponent(newInput) + "</div>" + "<br/><br/><br/>";
        //是文件
        }else if(map.type=="1") {
            var uuidFileName = map.uuidFileName;
            var realFileName = map.realFileName;
            if(realFileName.length>15){
                realFileName = realFileName.substring(0,6)+"..."+realFileName.substring(realFileName.indexOf('.')-6);
            }
            document.getElementById("messageView").innerHTML+=
                "<div style=\"float:left\">" +
                "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+ "  "+
                "<div style=\"float:right\">"+
                realFileName+"<br/>"+
                "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\" href=\"/util/downfile?uuidFileName="+uuidFileName+"\">下载</a>"+
                "</div>"+
                "</div>" + "<br/><br/><br/>";
        //图片视频
        }else if(map.type=="3") {
            //获取后缀
            var houZui = getType(map.uuidFileName);
            //是视频
            if (houZui=="mp4"||houZui=="webm"||houZui=="ogg"){
                document.getElementById("messageView").innerHTML +=
                    "<br/><br/><br/>"+
                    "<div style=\"float:left;\">" +
                    "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+"  " +
                    "<div style=\"float:right;margin-right: 5px;\">" +
                    "<video src=\"flage/"+map.uuidFileName+"\" controls=\"controls\" style='width: 200px;height: 150px;'>" +
                    "</video>"+
                    "<br/>" +
                    "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + map.uuidFileName + "\">下载</a>" +
                    "</div>" +
                    "</div>" + "<br/><br/><br/><br/><br/><br/>";
            }else {
                document.getElementById("messageView").innerHTML +=
                    "<br/><br/><br/>"+
                    "<div style=\"float:left;\">" +
                    "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+"  " +
                    "<div style=\"float:right;margin-right: 5px;\">" +
                    "<img src=\"flage/"+map.uuidFileName+"\" style='width: 80px;height: 80px;'>" +
                    "</img>"+
                    "<br/>" +
                    "<a class=\"btn btn-info\" style=\"text-decoration:none;color:grey\"  href=\"/util/downfile?uuidFileName=" + map.uuidFileName + "\">下载</a>" +
                    "</div>" +
                    "</div>" + "<br/><br/><br/><br/><br/><br/>";
            }

        }else {
            var uuidAudioName = map.uuidAudioName;
            var theTime = map.theTime;
            document.getElementById("messageView").innerHTML+=
                "<div style=\"float:left\">" +
                "<img src=\"" + map.headPortrait + "\" width=\"50px\" height=\"50px\">"+ "  "+
                "<div style=\"float:right\">"+
                "<div class='warper'><div id="+uuidAudioName+" class='voiceItem'>"+theTime+"s</div></div>"+
                "</div>"+
                "</div>" + "<br/><br/><br/>";
        }
        //滚动条默认显示在最下方
        $('#messageView')[0].scrollTop =$('#messageView')[0].scrollHeight;
    }

    if (map.type=="0"){
        //是普通文本
        //显示最后一条消息到列表中
        document.getElementById(map.toNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent(map.content);
    }else if (map.type=="1"){
        //是文件
        //显示最后一条消息到列表中
        document.getElementById(map.toNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent(map.realFileName);
    }else if (map.type=="3"){
        //图片视频
        //显示最后一条消息到列表中
        document.getElementById(map.toNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent("[图片]");
    }else {
        //是语音
        //显示最后一条消息到列表中
        document.getElementById(map.toNumber).firstElementChild.lastElementChild.lastElementChild.innerText =decodeURIComponent("[语音]");
    }
}

//棋子种类,1表示黑棋,2表示白棋
var pieceType = 0;
//初始化数组,0表示没有棋子，1表示黑棋，2表示白棋
var arr = new Array();
for(var i=0;i<15;i++){
    arr[i] = new Array();
    for(var j=0;j<15;j++){
        arr[i][j]=0;
    }
}
//对手账号
var theAccountNumber;
//标记,1表示该我下,2表示该对手下
var theSign;

function showGameMessage(map) {
    var theName = map.myName;
    var theType = map.theType;
    var accountNumber = map.accountNumber;
    if (theType=="GoBangInvite"){
        var flag = GoBangChoice(theName);
        if (flag==true){
            //同意
            stompClient.send("/app/game", {}, JSON.stringify({
                'toNumber':accountNumber,
                'type':"GoBangTrue"
            }));

            //开始对战
            //移除棋子
            document.getElementById('ggggg').innerHTML="";
            document.getElementById('Checkerboard').style.display="block";
            document.getElementById('ChatBox').style.display="none";
            document.getElementById('UserDetail').style.display="none";
            document.getElementById('chatName').style.display="none";
            document.getElementById('ellipsis').style.display="none";
            document.getElementById('groupInformation').style.display="none";
            document.getElementById('theSpace').style.display="none";
            document.getElementById('thePublish').style.display="none";
            document.getElementById('friendsSpace').style.display="none";

            pieceType = 1;
            theAccountNumber = accountNumber;
            //数组全部置0
            for(var i=0;i<15;i++){
                for(var j=0;j<15;j++){
                    arr[i][j]=0;
                }
            }
            theSign = 2;

        }else {
            //拒绝
            stompClient.send("/app/game", {}, JSON.stringify({
                'toNumber': accountNumber,
                'type':"GoBangFalse"
            }));
        }
    }else if (theType=="GoBangTrue"){
        alert(theName+" 接受了你的挑衅");
        document.getElementById('GoBang').innerHTML="";
        document.getElementById('vvvv').innerHTML="";
        //移除棋子
        document.getElementById('ggggg').innerHTML="";
        document.getElementById('Checkerboard').style.display="block";
        pieceType = 2;
        theAccountNumber = accountNumber;
        //数组全部置0
        for(var i=0;i<15;i++){
            for(var j=0;j<15;j++){
                arr[i][j]=0;
            }
        }
        theSign = 1;

    }else if (theType=="GoBangFalse"){
        alert(theName+" 拒绝了你的挑衅");
    }else if (theType=="GoBangPK"){
        var fx = map.fx;
        var fy = map.fy;
        var theValue = map.theValue;
        //设置数组值
        arr[fx][fy] = theValue;
        //显示对手下的棋子
        var x = fx*50.7+6;
        var y = fy*45.4+10;
        var x1 = x + 460 - 20;
        var y1 = y + 0 - 20;
        if (theValue==2){
            //显示白棋
            $('#ggggg').prepend(
                "<img class='BPiece' src=\"image/timg21.jpg\" style='z-index:10; border-radius: 50%;position: absolute;left: " + x1 + "px;top: " + y1 + "px'>"
            );
        }else {
            //显示黑棋
            $('#ggggg').prepend(
                "<img class='WPiece' src=\"image/timg22.jpg\" style='z-index:10; border-radius: 50%;position: absolute;left: " + x1 + "px;top: " + y1 + "px'>"
            );
        }
        //该我下了
        theSign = 1;
        //我的回合
        document.getElementById('vvvv').innerHTML="我的回合";
        //判断是否有5颗
        if (checkVictory(fx,fy,theValue)==true){
            if (theValue==2){
                alert("白棋获胜");
            }else {
                alert("黑棋获胜");
            }

            document.getElementById('kkkk').style.display="block";
        }
    }
}

//选择框
function GoBangChoice(theName){
    if(confirm(theName+" 邀请你来一盘紧张而刺激的五子棋")){
        return true;
    }else{
        return false;
    }
}

//双击棋盘内
document.getElementById('theBoard').addEventListener("dblclick",function (ev) {
    //鼠标点击坐标
    var x = ev.offsetX;
    var y = ev.offsetY;
    //数组下标
    var fx;
    var fy;
    //坐标吸附到交叉点上
    if ((x-6)%50.7<=25.35){
        fx = Math.floor((x-6)/50.7);
        x = Math.floor((x-6)/50.7)*50.7+6;
    }else {
        fx = Math.ceil((x-6)/50.7);
        x = Math.ceil((x-6)/50.7)*50.7+6;
    }

    if ((y-10)%45.4<=22.7){
        fy = Math.floor((y-10)/45.4);
        y = Math.floor((y-10)/45.4)*45.4+10;
    }else {
        fy = Math.ceil((y-10)/45.4);
        y = Math.ceil((y-10)/45.4)*45.4+10;
    }

    //console.log("typeof(fx)="+typeof(fx));
    //console.log("typeof(fy)="+typeof(fy));

    //该我下
    if (theSign==1) {
        //这个坐标点没有棋子
        if (arr[fx][fy] == 0) {

            var x1 = x + 460 - 20;
            var y1 = y + 0 - 20;

            if (pieceType == 1) {
                //显示下的黑棋
                $('#ggggg').prepend(
                    "<img class='BPiece' src=\"image/timg22.jpg\" style='z-index:10; border-radius: 50%;position: absolute;left: " + x1 + "px;top: " + y1 + "px'>"
                );
                //改变数组的值
                arr[fx][fy] = 1;
                //向对方发送棋子坐标和值
                stompClient.send("/app/game", {}, JSON.stringify({
                    'toNumber': theAccountNumber,
                    'fx': fx,
                    'fy': fy,
                    'theValue': 1,
                    'type': "GoBangPK"
                }));

            } else {
                //显示下的白棋
                $('#ggggg').prepend(
                    "<img class='WPiece' src=\"image/timg21.jpg\" style='z-index:10; border-radius: 50%;position: absolute;left: " + x1 + "px;top: " + y1 + "px'>"
                );
                //改变数组的值
                arr[fx][fy] = 2;
                //向对方发送棋子坐标和值
                stompClient.send("/app/game", {}, JSON.stringify({
                    'toNumber': theAccountNumber,
                    'fx': fx,
                    'fy': fy,
                    'theValue': 2,
                    'type': "GoBangPK"
                }));

            }

            //此时我不能下
            theSign = 2;
            document.getElementById('vvvv').innerHTML="对手回合";
            //判断是否有5颗
            if (checkVictory(fx,fy,pieceType)==true){

                if (pieceType==1){
                    alert("黑棋获胜");
                }else {
                    alert("白棋获胜");
                }

                document.getElementById('kkkk').style.display="block";

            }
        }
    }

});

function kkkk() {
    //移除棋子
    document.getElementById('ggggg').innerHTML="";
    document.getElementById('Checkerboard').style.display="none";
}

function test111(){
    for(var i=0;i<15;i++){
        for(var j=0;j<15;j++){
            //console.log(arr[i][j]);
        }
    }
}

function checkVictory(fx,fy,theValue) {
    var size = 0;
    //横向判断
    for (var i = fx-4;i<fx+5;i++){
        if (i<0||i>14){
            continue;
        }else {
            if (arr[i][fy]==theValue){
                size++;
                if (size==5){
                    return true;
                }
            }else {
                size = 0;
                continue;
            }
        }
    }

    size = 0;
    //纵向判断
    for (var j = fy-4;j<fy+5;j++){
        if (j<0||j>14){
            continue;
        }else {
            if (arr[fx][j]==theValue){
                size++;
                if (size==5){
                    return true;
                }
            }else {
                size = 0;
                continue;
            }
        }
    }

    size = 0;
    //左上到右下
    for (i = fx-4,j = fy-4;i<fx+5;i++,j++){
        if (i<0||j<0||i>14||j>14){
            continue;
        }else {
            if (arr[i][j]==theValue){
                size++;
                if (size==5){
                    return true;
                }
            }else {
                size = 0;
                continue;
            }
        }
    }

    size = 0;
    //右上到左下
    for (i = fx+4,j = fy-4;i>fx-5;i--,j++){
        if (i<0||j<0||i>14||j>14){
            continue;
        }else {
            if (arr[i][j]==theValue){
                size++;
                if (size==5){
                    return true;
                }
            }else {
                size = 0;
                continue;
            }
        }
    }

    return false;

}

function sleep(d) {
    for(var t = Date.now();Date.now() - t <= d;);
}

function removeTags(tagName,tagClass){
    var tagElements = document.getElementsByTagName( tagName );
    for( var m = 0 ; m < tagElements.length ; m++ ){
        if( tagElements[m].className == tagClass ){
            tagElements[m].parentNode.removeChild( tagElements[m] );
        }
    }
}