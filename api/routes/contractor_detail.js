const express = require('express');
const router = express.Router();

const _ = require('lodash');
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
FROM contractor
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
                    _.map(rows, r => ({ type: 'contractor_category', id: r.id }));
                response.json(response_obj);
            })
            .catch((error) => {
                utils.debug_log(error);
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



module.exports = router;
