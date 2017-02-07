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
        errors_info: {},
        data: data
    };

    errors_info = result.errors_info;

    _.forIn(schema, function(value, key) {
        if (value.not_null && !value.has_default && _.isUndefined(data[key])) {
            result.has_errors = true;
            _.extend(errors_info, {
                [key]: {
                    required: "Value is required"
                }
            });
        }
    });

    return result;
}

function catch_data_validation_error(response, error_info) {
    response.status(400).json(error_info);
}

function catch_unknown_error(response, error_msg) {
    response.status(400).send(error_msg);
}

router.route('/contractor_category')
    .get(function(request, response) {
        knex(dbconfig.contractor_category.table_name)
            .select('*')
            .then(function(result) {
                response.json({
                    data: result
                });
            })
            .catch(_.partial(catch_unknown_error, response));
    })
    .post(function(request, response) {
        var data = request.body;
        var validated_data = validate_data(
            dbconfig.contractor_category.schema, data);

        if (validated_data.has_errors) {
            catch_data_validation_error(validated_data.errors_info, response);
            return;
        }

        knex(dbconfig.contractor_category.table_name)
            .insert(validated_data.data)
            .returning('*')
            .then(function(result) {
                response.json(result[0])
            })
            .catch(_.partial(catch_unknown_error, response));
    });

app.use('/api/v1', router);


http.createServer(app).listen(+process.argv[2] || 8080, function() {
    console.log('App running on 192.168.33.10 or localhost:8080');
});
