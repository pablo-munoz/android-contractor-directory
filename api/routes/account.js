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


// TODO authenticate so only users can update themselves

router.route('/:account_id/favorites/:contractor_id/add')
    .post((request, response) => {
        const body = request.body;

        db.raw(`
INSERT INTO favorites (account_id, contractor_id)
VALUES ('${request.params.account_id}', '${request.params.contractor_id}');
`)
            .then(() => {
                response.status(200).end();
            })
            .catch((error) => {
                console.error(error);
                response.status(400).end();
            });

        console.log(body);
        response.send("Got it");
    });


router.route('/:account_id/favorites')
    .get((request, response) => {
        db.raw(`
SELECT contractor.* FROM contractor_summary AS contractor
JOIN favorites on contractor.id = favorites.contractor_id
WHERE favorites.account_id = '${request.params.account_id}';
`)
            .then((result) => {
                response.json({ data: result.rows });
            })
            .catch((error) => {
                console.error(error);
                response.status(400).send(error);
            });
    });


module.exports = router;
