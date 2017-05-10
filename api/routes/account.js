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

router.route('/')
    .patch((request, response) => {
        function handler(error, decoded) {
            if (error) {
                response.status(400).end();
                return;
            }

            const contractor_data = _.omit(request.body, 'password');

            db('contractor')
                .update(contractor_data)
                .where({
                    account_id: decoded.account_id
                })
                .then(() => {
                    response.json({
                        msg: "success"
                    });
                    return;
                })
                .catch((error) => {
                    console.error(error);
                    response.status(400).json({
                        error: "could not update contractor data"
                    });
                    return;
                });
        }

        jwt.verify(request.header('Authorization').split(' ')[1],
                   constants.AUTH_SECRET,
                   handler);
    });

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
                return;
            });
    });


router.route('/:account_id/favorites')
    .get((request, response) => {
        db.raw(`
SELECT contractor.* FROM contractor_summary AS contractor
JOIN favorites on contractor.id = favorites.contractor_id
WHERE favorites.account_id = '${request.params.account_id}';
`)
            .then((result) => {
                response.json({
                    data: _.map(result.rows, row => ({
                        "type": "contractor",
                        "id": row.id,
                        "attributes": _.omit(row, 'id')
                    }))
                });
                return;
            })
            .catch((error) => {
                console.error(error);
                response.status(400).send(error);
                return;
            });
    });


module.exports = router;
