from bson import ObjectId


def get_predicts_pipeline(start_time, end_time):
    return [
        {'$match': {'_id': ObjectId('67b50cac5480bb4fb83cb183')}},
        {'$project': {
            'days': {
                '$objectToArray': '$days'
            }
        }},
        {'$unwind': '$days'},
        {'$match': {
            'days.k': {
                '$gte': str(start_time),
                '$lte': str(end_time)
            }
        }},
        {'$group': {
            '_id': '$_id',
            'days': {
                '$push': {
                    'k': '$days.k',
                    'v': '$days.v'
                }
            }
        }},
        {'$project': {
            'days': {
                '$arrayToObject': '$days'
            }
        }}
    ]
