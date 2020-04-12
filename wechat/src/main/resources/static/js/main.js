var recorder;
var audio = document.querySelector('audio');

function startRecording() {
    document.getElementById('recording').innerHTML = "正在录音...  ";
    document.getElementById('recording').innerHTML+=
        "<input type=\"button\" value=\"发送\" onclick=\"send()\"/>"
   if(recorder){
       recorder.start();
       return;
   }

    HZRecorder.get(function (rec) {
        recorder = rec;
        recorder.start();
    },{error:showError});
}


function obtainRecord(){
     if(!recorder){
        showError("请先录音");
        return;
    }
   var record = recorder.getBlob();
   if(record.duration!==0){
   downloadRecord(record.blob);
   }else{
       showError("请先录音")
   }
};

function downloadRecord(record){
  var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a')
    save_link.href = URL.createObjectURL(record);
    var now=new Date;
    save_link.download = now.Format("yyyyMMddhhmmss");
    fake_click(save_link);
}


function fake_click(obj) {
var ev = document.createEvent('MouseEvents');
ev.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
obj.dispatchEvent(ev);
}

     function getStr(){
  var now=new Date;
  var str= now.toDateString();
}

function stopRecord(){
    recorder&&recorder.stop();
};
var msg={};
//发送音频片段
var msgId=1;
function send(){
    document.getElementById('recording').innerHTML = "";
    if(!recorder){
        showError("请先录音");
        return;
    }

   var data=recorder.getBlob();
   if(data.duration==0){
         showError("请先录音");
        return;
   }
    msg[msgId]=data;
    recorder.clear();
    //console.log(data);

    var blob1 = data.blob;
    var theTime = data.duration/10;
    //核心部分
    blobToDataURI(blob1,theTime);
    msgId++;
}

//回放
$(document).on("click",".voiceItem",function(){
    var id=$(this)[0].id;
    //根据id去查base64数据
    $.ajax({
        url:"findTheBaseById",
        data: {"id":id},
        async: false,
        success:function(data) {
            var theBase64 = data.theBase64;
            //base64转化成blob
            var theBlob = dataURItoBlob(theBase64);
            //播放
            audio.src = window.URL.createObjectURL(theBlob);
        }
    });

    // var data=msg[id];
    // playRecord(data.blob);
})

var ct;
function showError(msg){
    $(".error").html(msg);
    clearTimeout(ct);
    ct=setTimeout(function() {
        $(".error").html("")
    }, 3000);
}


function playRecord(blob){
    if(!recorder){
        showError("请先录音");
        return;
    }
     recorder.play(audio,blob);
};

/* 视频 */
function scamera() {
    var videoElement = document.getElementById('video1');
    var canvasObj = document.getElementById('canvas1');
    var context1 = canvasObj.getContext('2d');
    context1.fillStyle = "#ffffff";
    context1.fillRect(0, 0, 320, 240);
    context1.drawImage(videoElement, 0, 0, 320, 240);
};

function playVideo(){
    var videoElement1 = document.getElementById('video1');
    var videoElement2 = document.getElementById('video2');
    videoElement2.setAttribute("src", videoElement1);
};

function base64ToBlob(theBase) {
   var arr = theBase.split(','), mime = arr[0].match(/:(.*?);/)[1],
       bstr = atob(arr[1]),n = bstr.length,u8arr = new Uint8Array(n);
   while (n--){
       u8arr[n]=bstr.charCodeAt(n);
   }
   return new Blob([u8arr],{type:mime});
}

function dataURItoBlob(base64Data) {
  //console.log(base64Data);//data:image/png;base64,
  var byteString;
  if(base64Data.split(',')[0].indexOf('base64') >= 0)
      byteString = atob(base64Data.split(',')[1]);//base64 解码
  else{
      byteString = unescape(base64Data.split(',')[1]);
  }
  var mimeString = base64Data.split(',')[0].split(':')[1].split(';')[0];//mime类型 -- image/png

  // var arrayBuffer = new ArrayBuffer(byteString.length); //创建缓冲数组
  // var ia = new Uint8Array(arrayBuffer);//创建视图
  var ia = new Uint8Array(byteString.length);//创建视图
  for(var i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
  }
  var blob = new Blob([ia], {
      type: mimeString
  });
  return blob;
}

function blobToDataURI(blob,theTime) {
  var a = new FileReader();
  a.readAsDataURL(blob);
  a.onload = function () {
      //转成base64格式
      var theBase = a.result;
      //存入数据库
      //----------分割线-----------
      $.ajax({
          url: "message/audio",
          data: {"theBase":theBase,"theTime":theTime},
          async: false,
          success:function(data) {
              var uuidAudioName = data.uuidAudioName;
              document.getElementById("messageView").innerHTML+=
                  "<br/><br/><br/>"+
                  "<div style=\"float:right\">" +
                  "<div style=\"float:left\">"+
                       "<div class='warper'><div id="+uuidAudioName+" class='voiceItem'>"+theTime+"s ))></div></div>"+
                  "</div>"+
                  "  " +"<img src=\"" + data.headPortrait + "\" width=\"50px\" height=\"50px\">" +
                  "</div>" + "<br/><br/><br/>";
              //滚动条默认显示在最下方
              $('#messageView')[0].scrollTop =$('#messageView')[0].scrollHeight;
              //音频id
              sendAudio(uuidAudioName);
          },
          error:function () {
              alert("blobToDataURI-error");
          },
          dataType: "json",
          type: "POST"
      });
      //----------分割线-----------

  }
}

//发送音频
function sendAudio(uuidAudioName) {
  //获取我的头像路径
  var headPortrait = document.getElementById("headPortrait").src;
  var toNumber = document.getElementById("toAccountNumber").innerHTML;
  //长度小于8位是用户,否则是群
  //以Y开头F结尾表示音频
  if(toNumber.length<8) {
      stompClient.send("/app/userChat", {}, JSON.stringify({
          'headPortrait': headPortrait,
          'content': "Y" + uuidAudioName + "F",
          'toNumber': toNumber
      }));
  }else {
      stompClient.send("/app/all", {}, JSON.stringify({
          'headPortrait': headPortrait,
          'content': "Y" + uuidAudioName + "F",
          'toNumber': toNumber
      }));
  }
  //显示最后一条消息到列表中
  document.getElementById(toNumber).firstElementChild.lastElementChild.lastElementChild.innerText=decodeURIComponent("[语音]");
}