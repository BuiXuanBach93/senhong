/*(function() {
    var Sock = function() {
        var socket;
        if (!window.WebSocket) {
            window.WebSocket = window.MozWebSocket;
        }

        if (window.WebSocket) {
            socket = new WebSocket("ws://localhost:8070");
            socket.onopen = onopen;
            socket.onmessage = onmessage;
            socket.onclose = onclose;
        } else {
            alert("Your browser does not support Web Socket.");
        }

        function onopen(event) {
            console.log("Web Socket opened!");
        }

        function onmessage(event) {
            debugger;
            console.log(event.data);
        }
        function onclose(event) {
            console.log("Web Socket closed");
        }

        function send(event) {
            event.preventDefault();
            if (window.WebSocket) {
                if (socket.readyState == WebSocket.OPEN) {
                    socket.send(event.target.message.value);
                } else {
                    alert("The socket is not open.");
                }
            }
        }
    }
    window.addEventListener('load', function() { new Sock(); }, false);
})();*/

getMyMessage = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/getmymessage',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {    
            var divMessage = document.getElementById('G_Message');
            var innerDiv = '';
            for(var i = 0; i < data.message.length; i++) {
                innerDiv += '<a href="#" class="list-group-item">' +
                                    '<p class="contacts-title pull-left">' + data.message[i].sender_name + '</p><p class="pull-right" style="color: #0571ce">' + data.message[i].send_date + '</p><br />' +
                                    '<img src="/viewimage?filename=' + data.message[i].sender_picture + '" class="pull-left" style="width:60px; height:60px;" alt="' + data.message[i].sender_name + '"/>' +
                                    '<p>' + data.message[i].content + '</p>' +
                                '</a>';
            }
            divMessage.innerHTML = innerDiv;
        }
    });    
}