module.exports = require('knex')({
    client: 'pg',
    connection: 'postgres://postgres:postgres@localhost:5432/directorio'
});
