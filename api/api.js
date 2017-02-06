const http = require('http');
const express = require('express');
const app = express();


app.route('/')
    .get(function(request, response) {
        response.send("Api starts at /api/v1");
    });


http.createServer(app).listen(8080, function() {
    console.log('App running on 192.168.33.10 or localhost:8080');
});
