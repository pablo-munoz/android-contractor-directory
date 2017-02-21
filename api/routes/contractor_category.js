const express = require('express');
const router = express.Router();

const dbconfig = require('../dbconfig');
const route_utils = require('./route_utils');

router.route('/')
    .get(route_utils.make_simple_list_route(dbconfig.contractor_category))
    .post(route_utils.make_simple_create_route(dbconfig.contractor_category));

module.exports = router;
