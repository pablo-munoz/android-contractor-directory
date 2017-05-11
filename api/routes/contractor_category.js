const express = require('express');
const router = express.Router();

const _ = require('lodash');

const dbconfig = require('../dbconfig');
const route_utils = require('./route_utils');
const db = require('../db');

router.route('/')
    .get((request, response) => {
        const query = `
SELECT contractor_category.id, contractor_category.*, count(contractor_category_map.contractor_id) AS count
FROM contractor_category LEFT JOIN contractor_category_MAP
ON contractor_category.id = contractor_category_map.contractor_category_id
GROUP BY contractor_category.id;
`
        db.raw(query)
            .then((result) => {
                const rows = result.rows;

                response.json({
                    meta: {
                        count: rows.length
                    },
                    links: {
                        self: "/api/v1/contractor_category"
                    },
                    data: _.map(rows, (r) => {
                        return {
                            type: "contractor_category",
                            id: r.id,
                            attributes: _.omit(r, 'id')
                        };
                    })
                });
            })
            .catch((error) => {
                console.error(error);
                response.status(400).end();
            });
    })
    // .get(route_utils.make_simple_list_route(dbconfig.contractor_category))
    .post(route_utils.make_simple_create_route(dbconfig.contractor_category));

module.exports = router;
