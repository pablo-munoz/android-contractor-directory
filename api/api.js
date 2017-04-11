const http = require('http');
const bodyParser = require('body-parser');
const _ = require('lodash');

const express = require('express');
const app = express();

const dbconfig = require('./dbconfig.js');
const db = require('./db');

const constants = require('./constants');


app.route('/')
    .get(function(request, response) {
        response.send("Api starts at /api/v1");
    });


const router = express.Router();


app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());


app.use(constants.api_version, router);
app.use(
    constants.api_version + '/contractor_category',
    require('./routes/contractor_category'));
app.use(
    constants.api_version + '/contractor_category',
    require('./routes/contractor_category_detail'));
app.use(constants.api_version + '/contractor', require('./routes/contractor'));
app.use(constants.api_version + '/contractor', require('./routes/contractor_detail'));
app.use(constants.api_version + '/auth', require('./routes/auth'));
app.use(constants.api_version + '/account', require('./routes/account'));


const server = http.createServer(app).listen(+process.argv[2] || 8080, function() {
    console.log('App running on 192.168.33.10:3000');
});

const io = require('socket.io')(server);



const interlocutors = {};


io.on('connection', function(socket) {
    console.log('A user connected');

    socket.on('disconnect', function() {
        console.log('A user has disconnected');
    });

    socket.on('identify', function(payload) {
        payload = JSON.parse(payload);
        interlocutors[payload.id] = socket;
        socket.m_id = payload.id;
        socket.join(payload.id);
    });

    socket.on('send message', function(payload) {
        payload = JSON.parse(payload);
        console.log(payload);
        const recipient = payload.recipient;
        const message = payload.message;
        io.to(recipient).emit('new message', payload);
    });
});
