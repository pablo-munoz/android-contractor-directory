/*
  route_utils.js

  This module exports a number of mostly unrelated functions
  that are meant to facilitate common functionality
  of the different routes through the api.
*/

// A function to validate json data. It looks at the data
// in the corresponding dbconfig table schema and determines
// if some data is missing / wrong, in which case it returns
// a json with appropiate error info
const _ = require('lodash');
const db = require('../db');
const constants = require('../constants.js');
const utils = require('../utils.js');

function validate_data(data, schema) {
    var result = {
        has_errors: false,
        errors_info: [],
        data: data
    };

    errors_info = result.errors_info;

    _.forIn(schema, function(value, key) {
        if (value.not_null && !value.has_default &&
            !value.default && _.isUndefined(data[key])) {
            result.has_errors = true;
            result.errors_info.push({
                title: "Missing required value",
                source: {
                    pointer: "data/attributes/" + key
                }
            });
        }
    });

    return result;
}



function catch_unknown_error(response, error_msg) {
    response.status(400).send(error_msg);
}



function serialize_resource_list_to_json_api(table, resource_list) {
    return _.map(resource_list, function(resource) {
        return {
            type: table.table_name,
            id: resource.id,
            attributes: _.omit(resource, 'id')
        };
    });
}



function make_simple_list_route(table) {
    return function(request, response) {
        utils.debug_log("LISTING " + table.table_name);

        const queries = _.pick(request.query, _.keys(table.schema));

        var response_obj = {};

        db(table.table_name)
            .count('*')
            .then(function(total) {
                response_obj.meta = {
                    count: total[0].count
                };

                return db(table.table_name)
                    .select('*')
                    .where(queries);
            })
            .then(function(result) {
                response_obj.links = {
                    self: constants.api_version + '/' + table.table_name
                };
                response_obj.data = serialize_resource_list_to_json_api(table, result);
                response.json(response_obj);
            })
            .catch(_.partial(catch_unknown_error, response));
    }
}



function make_simple_create_route(table) {
    return function(request, response) {
        let data = undefined;
        let validated_data = undefined;
        let error_json = undefined;

        data = get_resource_attributes_from_request(request, table.schema);
        validated_data = validate_data(data, table.schema);

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

        db(table.table_name)
            .insert(validated_data.data)
            .returning('*')
            .then(function(result) {
                response.json({
                    links: {
                        self: constants.api_version + '/' + table.table_name + '/' + result[0].id,
                        all: constants.api_version + '/' + table.table_name 
                    },
                    data: {
                        type: table.table_name,
                        id: result[0].id,
                        attributes: _.omit(result[0], 'id')
                    }
                });
            })
            .catch(_.partial(catch_unknown_error, response));
    }
}



function make_simple_detail_route(table) {
    return (request, response)=> {
        utils.debug_log(`GET FOR ${table.table_name} ${request.params.id}`);

        db(table.table_name)
            .select('*')
            .where({ id: request.params.id })
            .then((result) => {

                utils.debug_log("DB responded with:");
                utils.debug_log(result);

                let responseJSON = {
                    links: {
                        self: constants.api_version + table.table_name + '/'
                            + result[0].id,
                        all: constants.api_version + table.table_name 
                    },
                    data: {
                        type: table.table_name,
                        id: result[0].id,
                        attributes: _.omit(result[0], 'id')
                    }
                };

                utils.debug_log("Constructed response:");
                utils.debug_log(responseJSON);

                response.json(responseJSON);
            })
            .catch(_.partial(catch_unknown_error, response));
    }
}


function get_resource_attributes_from_json(json, schema) {
    return _.pick(json, _.keys(schema));
}


function get_resource_attributes_from_request(request, schema) {
    return get_resource_attributes_from_json(
        _.get(request, 'body.data.attributes', {}),
        schema
    );
}


module.exports = {
    catch_unknown_error,
    get_resource_attributes_from_json,
    get_resource_attributes_from_request,
    make_simple_create_route,
    make_simple_detail_route,
    make_simple_list_route,
    serialize_resource_list_to_json_api,
    validate_data,
};
