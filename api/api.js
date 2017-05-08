const http = require('http');
const bodyParser = require('body-parser');
const _ = require('lodash');

const express = require('express');
const app = express();
const jwt = require('jsonwebtoken');

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


const conversation_db = {};


function get_conversation_id(account_id_1, account_id_2) {
    if (account_id_1 <= account_id_2) return account_id_1 + account_id_2;
    else return account_id_2 + account_id_1;
}


app.route(constants.api_version + '/conversation')
    .get((request, response) => {
        function handler(error, decoded) {
            if (error) {
                response.status(400).end();
                return;
            }

            const conversations = _.filter(
                _.values(conversation_db),
                convo => _.includes(convo.interlocutors, decoded.account_id));

            response.json({
                data: conversations
            });
        }

        jwt.verify(request.header('Authorization').split(' ')[1],
                   constants.AUTH_SECRET,
                   handler);
    });


app.route(constants.api_version + '/conversation/:conversation_id')
    .get((request, response) => {
        function handler(error, decoded) {
            if (error) {
                response.status(400).end();
                return;
            }

            response.json({
                data: conversation_db[request.params.conversation_id]
            });
        }

        jwt.verify(request.header('Authorization').split(' ')[1],
                   constants.AUTH_SECRET,
                   handler);
    });



io.on('connection', function(socket) {
    console.log('A user connected');

    socket.on('disconnect', function() {
        console.log('user disconnected');
        // interlocutors[socket.account_id].socket = null;
    });

    socket.on('identify', function(payload) {
        payload = JSON.parse(payload);
        const account_id = payload.id;

        interlocutors[account_id] = {
            socket: socket,
            conversations: {}
        };

        // Each account joins a room with its id.
        socket.join(account_id);
        socket.account_id = account_id;
    });

    // On connection, send the conversation history
    io.to(socket.account_id).emit(
        'conversation history',
        _.filter(_.values(conversation_db), (conversation) => _.includes(conversation.interlocutors, socket.account_id)));

    socket.on('send message', function(payload) {

        const author_id = payload.from;
        const recipient_id = payload.to;
        const message = payload.message;

        const conversation_id = get_conversation_id(author_id, recipient_id);
        let conversation = conversation_db[conversation_id];

        if (_.isUndefined(conversation)) {
            conversation_db[conversation_id] = {
                id: conversation_id,
                interlocutors: [author_id, recipient_id],
                messages: []
            };

            db.select('account.id', 'account.email', 'contractor.first_name',
                      'contractor.middle_name', 'contractor.last_names')
                .from('account')
                .rightJoin('contractor', 'account.id', 'contractor.account_id')
                .where('account.id', recipient_id)
                .orWhere('account.id', author_id)
                .then((result) => {
                    conversation_db[conversation_id].interlocutor_data = {
                        [result[0].id]: result[0].first_name + ' ' + (result[0].middle_name || '') + ' ' + result[0].last_names,
                        [result[1].id]: result[1].first_name + ' ' + (result[1].middle_name || '') + ' ' + result[1].last_names,

                    };

                    conversation.last_message_date = new Date();
                    conversation.messages.push(payload);

                    io.to(recipient_id).emit('new message', payload);
                })
                .catch((error) => {
                    console.error(error);
                });

            conversation = conversation_db[conversation_id];
        } else {

            conversation.last_message_date = new Date();
            conversation.messages.push(payload);

            io.to(recipient_id).emit('new message', payload);
        }
    });
});
