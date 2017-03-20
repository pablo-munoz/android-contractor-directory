const express = require('express');
const router = express.Router();

const _ = require('lodash');
const jwt = require('jsonwebtoken');

const constants = require('../constants');
const db = require('../db');
const utils = require('../utils');
const dbconfig = require('../dbconfig');
const route_utils = require('./route_utils');


router.route('/:id')

// G E T
    .get((request, response) => {
        const contractor_id = request.params.id;
        const query = `
SELECT *
FROM contractor_summary
WHERE id = '${contractor_id}';
`;

        let response_obj = {};

        db.raw(query)
            .then((result) => {
                const data = result.rows[0];

                response_obj.links = {
                    self: `${constants.api_version}/contractor/${contractor_id}`,
                    all: `${constants.api_version}/contractor`,
                };

                response_obj.data = {
                    type: 'contractor',
                    id: data.id,
                    attributes: _.omit(data, 'id')
                };

                response_obj.relationships = {
                    category: {
                        data: []
                    }
                };

                return response_obj;
            })
            .then(() => {
                const query2 = `
SELECT * FROM contractor_category_map
WHERE contractor_id = '${contractor_id}';
`;
                return db.raw(query2);
            })
            .then((result) => {
                const rows = result.rows;
                response_obj.relationships.category.data =
                    _.map(rows, r => ({ type: 'contractor_category', id: r.contractor_category_id }));
                response.json(response_obj);
            })
            .catch((error) => {
                utils.debug_log(error);
                response.status(400).send(error);
            });
    })

    .put((request, response) => {
        const body = request.body;
        const id = _.get(body, 'data.id', -1);
        let category_relationships = _.get(body, 'relationships.category.data', []);

        // If the relationship is a single object make it the only
        // element in an array so that all cases can be treated
        // as arrays.
        if (_.isPlainObject(category_relationships)) {
            category_relationships = [category_relationships];
        }

        const attributes = _.get(body, 'data.attributes', null);

        let updated_contractor;

        db(dbconfig.contractor.table_name)
            .where({ id: id })
            .update(_.pickBy(attributes, _.identity))
            .returning('*')
            .then(result => {
                updated_contractor = result[0];

                console.log(category_relationships);

                return Promise.all(_.map(category_relationships, (cat) => {
                    return db.insert({
                        contractor_id: id,
                        contractor_category_id: cat.id
                    })
                        .into(dbconfig.contractor_category_map.table_name)
                        .returning('*');
                }));
            })
            .then(() => {
                response.json({
                    data: {
                        type: 'contractor',
                        id: id,
                        attributes: _.omit(updated_contractor, 'id')
                    }
                });
            })
            .catch((error) => {
                console.error(error);
                response.status(400).send(error);
            });
    })

// D E L E T E
    .delete((request, response) => {
        const contractor_id = request.params.id;
        const query = `
UPDATE contractor
SET status = 'inactive'
WHERE contractor.id = '${contractor_id}';
`;

        utils.debug_log("DELETE on /contractor/:id");
        utils.debug_log(`Contractor id: ${contractor_id}`);

        db.raw(query)
            .then((result) => {
                response.status(200).send("Deletion successfull");
            })
            .catch((error) => {
                utils.debug_log(error);
                response.status(400).send("Deletion failed.");
            });
    });


router.route('/:id/rate/:rating')
    .post((request, response) => {
        const rating = parseFloat(request.params.rating);

        function handler(error, decoded) {
            if (error) response.status(400).end();
            if (rating == parseFloat('nan')) {
                console.log(`Rating attempt from ${decoded.account_id} of ${request.params.rating} with non numeric value.`);
                response.status(400).end();
            }

            const query = `
INSERT INTO contractor_rating (account_id, contractor_id, rating)
VALUES ('${decoded.account_id}', '${request.params.id}', ${request.params.rating})
ON CONFLICT (account_id, contractor_id) DO UPDATE
SET rating = ${request.params.rating};
`;

            db.raw(query)
                .then(result => response.status(200).end())
                .catch(error => {
                    console.error(error);
                    response.status(400).send(error)
                });

        }

        jwt.verify(request.header('Authorization').split(' ')[1],
                   constants.AUTH_SECRET,
                   handler);
    });


module.exports = router;
