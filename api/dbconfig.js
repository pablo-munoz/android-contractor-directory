module.exports = {

    contractor_category: {
        table_name: 'contractor_category',
        schema: {
            'id': {
                'type': 'uuid',
                'not_null': true,
                'pk': true,
                'has_default': true
            },
            'name': {
                'type': 'text',
                'not_null': true,
                'has_default': false
            },
            'short_name': {
                'type': 'text',
                'not_null': true,
                'has_default': false
            },
            'img': {
                'type': 'text',
                'not_null': true,
            },
            'date_created': {
                'type': 'date',
                'not_null': true,
                'has_default': true
            },
        }
    }
};
