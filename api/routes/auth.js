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
        const data = body.data;
        const is_contractor = _.has(body, 'relationships.contractor') &&
              _.has(body, 'relationships.contractor_category');

        const query = `
INSERT INTO account (email, password) VALUES
  ('${data.email}', crypt('${data.password}', gen_salt('bf', 8)))
RETURNING *;
`;

        db.raw(query)
            .then((result) => {
                if (!is_contractor) {
                    response.status(200).json({
                        message: 'registration successful'
                    });
                } else {
                    return result.rows[0];
                }
            })
            .then((account) => {
                const contractor_data = body.relationships.contractor;

                return db.raw(`
INSERT INTO contractor (first_name, middle_name, last_names, phone,
    website, account_id)
VALUES
    ('${contractor_data.first_name}', '${contractor_data.middle_name}',
     '${contractor_data.last_names}', '${contractor_data.phone}',
     '${contractor_data.website}', '${account.id}')
RETURNING *;
`);
            })
            .then((result) => {
                const contractor = result.rows[0];
                const contractor_category_id = body.relationships.contractor_category[0];

                return db.raw(`
INSERT INTO contractor_category_map (contractor_id, contractor_category_id)
VALUES ('${contractor.id}', '${contractor_category_id}');
`);
            })
            .then(() => {
                    response.status(200).json({
                        message: 'registration successful'
                    });
            })
            .catch((error) => {
                utils.debug_log("Error registering user");
                utils.debug_log(request.body);
                console.error(error);
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

        const json = {};

        db.raw(query)
            .then((result) => {
                if (result.rows.length == 1) {
                    json.id = result.rows[0].id;
                    json.email = result.rows[0].email;
                    json.token = jwt.sign({
                        account_id: result.rows[0].id,
                    }, constants.AUTH_SECRET, { expiresIn: '2d' });

                    return result.rows[0].id;
                } else {
                    response.status(403).end();
                }
            })
            .then((account_id) => {
                return db.raw(`
SELECT * FROM contractor_summary WHERE account_id = '${account_id}';
`);
            })
            .then((result) => {
                if (result.rows.length) {
                    json.contractor = result.rows[0];
                } else {
                    json.contractor = null
                }
            })
            .then(() => response.json(json))
            .catch((error) => {
                console.error(error);
                response.status(400).json({
                    status: 'failure processing authentication'
                });
            });
    });



module.exports = router;
