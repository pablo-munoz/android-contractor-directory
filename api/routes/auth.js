const express = require('express');
const router = express.Router();
const Promise = require('bluebird');

const _ = require('lodash');
const jwt = require('jsonwebtoken');

const constants = require('../constants');
const db = require('../db');
const utils = require('../utils');
const dbconfig = require('../dbconfig');
const route_utils = require('./route_utils');


router.route('/register')
    .post((request, response) => {
        const body = request.body;

        const query = `
INSERT INTO account (email, password) VALUES
  ('${body.email}', crypt('${body.password}', gen_salt('bf', 8)));
`;

        db.raw(query)
            .then((result) => {
                response.status(201).json({
                    message: 'registration successful'
                });
            })
            .catch((error) => {
                utils.debug_log("Error registering user");
                utils.debug_log(request.body);
                response.status(400).end();
            });
    });


router.route('/login')
    .post((request, response) => {
        const body = request.body;

        const query = `
SELECT * FROM account WHERE email = lower('${body.email}') AND
  password = crypt('${body.password}', password);
`;

        utils.debug_log("Login attempt");
        utils.debug_log(body);
        db.raw(query)
            .then((result) => {
                if (result.rows.length == 1) {
                    response.status(200).json({
                        token: jwt.sign({
                            account_id: result.rows[0].id,
                        }, constants.AUTH_SECRET, { expiresIn: '2d' })
                    });
                } else {
                    response.status(403).end();
                }
            })
            .catch((error) => {
                console.error(error);
                response.status(400).json({
                    status: 'failure processing authentication'
                });
            });
    });



module.exports = router;
