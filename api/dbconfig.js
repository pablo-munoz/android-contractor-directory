module.exports = {

    contractor_category: {
        table_name: 'contractor_category',
        schema: {
            id: {
                type: 'uuid',
                not_null: true,
                pk: true,
                has_default: true
            },
            name: {
                type: 'text',
                not_null: true,
            },
            short_name: {
                type: 'text',
                not_null: true,
            },
            img: {
                type: 'text',
                not_null: true,
            },
            date_created: {
                type: 'date',
                not_null: true,
                has_default: true
            },
        }
    },

    contractor: {
        table_name: 'contractor',
        schema: {
            id: {
                type: 'uuid',
                not_null: true,
                pk: true,
                has_default: true
            },
            first_name: {
                type: 'text',
                not_null: true,
            },
            middle_name: {
                type: 'text',
            },
            last_names: {
                type: 'text',
                not_null: true,
            },
            phone: {
                type: 'text',
                not_null: true,
            },
            email: {
                type: 'text',
            },
            website: {
                type: 'text',
            },
            description: {
                type: 'text',
            },
            date_created: {
                type: 'date',
                not_null: true,
                has_default: true
            }
        }
    },

    contractor_category_map: {
        table_name: 'contractor_category_map',
        schema: {
            id: {
                type: 'uuid',
                not_null: true,
                pk: true,
                has_default: true
            },
            contractor_category_id: {
                type: 'uuid',
                not_null: true,
            },
            contractor_id: {
                type: 'uuid',
                not_null: true,
            },
            date_created: {
                type: 'date',
                not_null: true,
                has_default: true
            }
        }
    }
};
