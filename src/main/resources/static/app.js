var stompClient = null;
var files_paths = [];

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#filestree").show();
    }
    else {
        $("#filestree").hide();
    }
    $("#files").html("");
}

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);

        stompClient.subscribe('/topic/file', function (message) {
            var event = JSON.parse(message.body);
            addOrRemove(event);
            showGreeting();
        });
    });
}

function addOrRemove(event) {
    if (event["eventType"] == "CREATE" && $.inArray(event["path"], files_paths) == -1) {
        files_paths.push(event["path"]);
    }
    else if (event["eventType"] == "DELETE") {
        files_paths = jQuery.grep(files_paths, function (value) {
            return value != event["path"];
        });
    }
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendPath() {
    files_paths = [];
    $("#files").empty();

    stompClient.send("/app/path", {}, JSON.stringify({
        'path': $("#path").val()
    }));

    stompClient.subscribe('/app/tree/' + $("#path").val().replace(/\\/g, '\\\\'), function (message) {
        var events = JSON.parse(message.body);
        for (var i = 0; i < events.length; i++) {
            addOrRemove(events[i]);
        }
        showGreeting();
    });
}

function showGreeting() {
    $("#files").empty();
    $.each(files_paths, function (index, value) {
        $("#files").append("<tr><td>" + value + "</td></tr>");
    });
}

$(function () {
    connect();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendPath();
    });
});