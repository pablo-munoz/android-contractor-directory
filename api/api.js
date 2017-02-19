const http = require('http');
const express = require('express');
const bodyParser = require('body-parser');
const _ = require('lodash');
const app = express();
const dbconfig = require('./dbconfig.js');

const knex = require('knex')({
    client: 'pg',
    connection: 'postgres://postgres:postgres@localhost:5432/directorio'
});

const api_version = '/api/v1';

app.route('/')
    .get(function(request, response) {
        response.send("Api starts at /api/v1");
    });


const router = express.Router();

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// A function to validate json data. It looks at the data
// in the corresponding dbconfig table schema and determines
// if some data is missing / wrong, in which case it returns
// a json with appropiate error info
function validate_data(schema, data) {
    var result = {
        has_errors: false,
        errors_info: [],
        data: data
    };

    errors_info = result.errors_info;

    _.forIn(schema, function(value, key) {
        if (value.not_null && !value.has_default && _.isUndefined(data[key])) {
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

function serializeResourceListToJSONApi(table, resource_list) {
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
        const queries = _.pick(request.query, _.keys(table.schema));

        var response_obj = {};

        knex(table.table_name)
            .count('*')
            .then(function(total) {
                response_obj.meta = {
                    count: total[0].count
                };

                return knex(table.table_name)
                    .select('*')
                    .where(queries);
            })
            .then(function(result) {
                response_obj.links = {
                    self: api_version + '/' + table.table_name
                };
                response_obj.data = serializeResourceListToJSONApi(table, result);
                response.json(response_obj);
            })
            .catch(_.partial(catch_unknown_error, response));
    }
}

function make_simple_create_route(table) {
    return function(request, response) {
        var data = _.pick(request.body.data.attributes, _.keys(table.schema));
        var validated_data = validate_data(table.schema, data);

        if (validated_data.has_errors) {
            request.body.errors = validated_data.errors_info;
            response.status(400).json(request.body);
            return;
        }

        knex(table.table_name)
            .insert(validated_data.data)
            .returning('*')
            .then(function(result) {
                response.json({
                    links: {
                        self: api_version + '/' + table.table_name + '/' + result[0].id,
                        all: api_version + '/' + table.table_name 
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
    return function(request, response) {
        knex(table.table_name)
            .select('*')
            .where({ id: request.params.id })
            .then(function(result) {
                response.json({
                    links: {
                        self: api_version + '/' + table.table_name + '/' + result[0].id,
                        all: api_version + '/' + table.table_name 
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

router.route('/contractor_category')
    .get(make_simple_list_route(dbconfig.contractor_category))
    .post(make_simple_create_route(dbconfig.contractor_category));

router.route('/contractor_category/:id')
    .get(make_simple_detail_route(dbconfig.contractor_category));

router.route('/contractor')
    .get(function(request, response) {
        var query = [
            'SELECT contractor.*',
            'FROM contractor',
            'JOIN contractor_category_map AS map',
            '  ON contractor.id = map.contractor_id'
        ].join('\n');

        if (request.query.contractor_category) {
            query += '\nWHERE map.contractor_category_id = \'' +
                request.query.contractor_category + '\';';
        } else {
            query += ';'
        }

        console.log(query);

        knex.raw(query)
            .then(function(result) {
                console.log(result);
                response.json({
                    data: serializeResourceListToJSONApi(
                        dbconfig.contractor, result.rows)
                });
            })
            .catch(function(error) {
                console.error(error);
            });
    })
    .post(make_simple_create_route(dbconfig.contractor));

router.route('/contractor/:id')
    .get(make_simple_detail_route(dbconfig.contractor));

app.use(api_version, router);


http.createServer(app).listen(+process.argv[2] || 8080, function() {
    console.log('App running on 192.168.33.10 or localhost:8080');
});
