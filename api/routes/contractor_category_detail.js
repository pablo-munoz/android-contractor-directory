const express = require('express');
const router = express.Router();

const dbconfig = require('../dbconfig');
const route_utils = require('./route_utils');

router.route('/:id')
    .get(route_utils.make_simple_detail_route(dbconfig.contractor_category));

module.exports = router;
