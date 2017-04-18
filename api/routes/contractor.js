const express = require('express');
const router = express.Router();
const Promise = require('bluebird');

const _ = require('lodash');
const constants = require('../constants');
const db = require('../db');
const utils = require('../utils');
const dbconfig = require('../dbconfig');
const route_utils = require('./route_utils');



router.route('/')

// G E T
    .get(function(request, response) {
        const hasCategoryFilter = _.has(request, 'query.contractor_category');
        const categoryFilter = hasCategoryFilter ?
              `AND map.contractor_category_id = '${request.query.contractor_category}'` :
              '';
        const query = `
SELECT DISTINCT contractor_summary.*
FROM contractor_summary
JOIN contractor_category_map AS map
  ON contractor_summary.id = map.contractor_id
WHERE contractor_summary.status = 'active'
${ categoryFilter };
`;

        db.raw(query)
            .then((result) => {
                // WARNING: The set timeout is only for purposes of
                // demonstrating the loading screen during the
                // presentation and should be removed before release.
                setTimeout(() =>
                           response.json({
                               meta: {
                                   count: result.rows.length
                               },
                               data: route_utils.serialize_resource_list_to_json_api(
                                   dbconfig.contractor, result.rows)
                           }), 3000);
            })
            .catch((error) => {
                console.error(error);
            });
    })

// P O S T
    .post(function(request, response) {
        utils.debug_log("POST request to /contractor");
        utils.debug_log("BODY: ");
        utils.debug_log(request.body);

        console.log(request.body);

        const body = request.body;
        let categoriesAssociated = [];
        let hasCategoriesAssociated = false;

        if (_.has(body, 'relationships.contractor_category.data')) {
            const categoriesRelData = body.relationships.contractor_category.data;
            hasCategoriesAssociated = true;
            if (_.isPlainObject(categoriesRelData)) {
                categoriesAssociated.push(categoriesRelData.id);
            } else if (_.isArray(categoriesRelData)) {
                categoriesAssociated = _.map(categoriesRelData, (cat) => cat.id);
            }
        }

        const data = route_utils.get_resource_attributes_from_request(
            request, dbconfig.contractor.schema);
        utils.debug_log("DATA:");
        utils.debug_log(data);
        console.log(data);

        const validated_data = route_utils.validate_data(data, dbconfig.contractor.schema);
        utils.debug_log("VALIDATED DATA:");
        utils.debug_log(validated_data);

        if (!validated_data || validated_data.has_errors) {
            if (request.body) {
                error_json = request.body;
            } else {
                error_json = {
                    body: {}
                };
            }

            error_json.errors = validated_data.errors_info;
            response.status(400).json(error_json);
            return;
        }

        let new_contractor;

        console.log(validated_data.data);

        db.transaction((trx) => {
            return trx.insert(validated_data.data)
                .into(dbconfig.contractor.table_name)
                .returning('*')
                .then((result) => {
                    new_contractor = result[0];

                    utils.debug_log("INSERTED CONTRACTOR:");
                    utils.debug_log(new_contractor);

                    return Promise.map(categoriesAssociated, (category_id) => {
                        return trx.insert({
                            contractor_id: new_contractor.id,
                            contractor_category_id: category_id
                        }).into(dbconfig.contractor_category_map.table_name);
                    });
                });
        })
            .then((anything) => {
                response.set({
                    location: constants.api_version + `/contractor/${new_contractor.id}`
                });
                response.status(201);
                response.json({
                    type: "contractor",
                    id: new_contractor.id,
                    attributes: _.omit(new_contractor, 'id'),
                    links: {
                        self: constants.api_version + `/contractor/${new_contractor.id}`
                    }
                });
            })
            .catch((error) =>  {
                console.error(error);
                response.status(400).send("Wrong input.")
            });
    });



module.exports = router;
