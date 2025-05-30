from bson import ObjectId

from endpoints.stats.model import UserStats
from services.mongo import USERS_STATS, USERS_ENTRY_CL

LEVELS = [
    {
        "number": 1,
        "points": 0,
        "label": "Новичок",
    },
    {
        "number": 2,
        "points": 100,
        "label": "Продвинутый",
    },
    {
        "number": 3,
        "points": 500,
        "label": "Мастер",
    },
    {
        "number": 4,
        "points": 1000,
        "label": "Легенда",
    },
]


def get_user_level(points):
    for level in reversed(LEVELS):
        if points >= level["points"]:
            return level["number"]
    return 1


def get_users_stats(users_ids):
    stats = list(USERS_STATS.find({'_id': {'$in': users_ids}}))
    names = list(USERS_ENTRY_CL.find({'_id': {'$in': users_ids}}))

    res = []
    for user_id in users_ids:
        user_stat = next((stat for stat in stats if stat['_id'] == user_id), None)
        user_name = next((name for name in names if name['_id'] == user_id), None)

        avatar = user_name.get('avatar') if user_name else None

        if user_stat and user_name:
            cur_stats = UserStats.from_json(user_stat)
            points = cur_stats.winner_points + cur_stats.score_points
            res.append({
                'id': str(user_id),
                'name': user_name['login'],
                'avatar': avatar,
                'stats': UserStats.from_json(user_stat).to_json(),
                'level': get_user_level(points),
                'points': points
            })
    return res


def update_stats(user_id, inc_predicted_games, inc_winner_points, inc_score_points):
    USERS_STATS.update_one(
        {'_id': ObjectId(user_id)},
        {'$inc': {
            'predicted_games': inc_predicted_games,
            'winner_points': inc_winner_points,
            'score_points': inc_score_points
        }}
    )

def update_follows(user_id, follow_id, subscribe=True):
    increment_value = 1 if subscribe else -1
    USERS_STATS.update_one(
        {'_id': ObjectId(user_id)},
        {'$inc': {
            'following_count': increment_value
        }}
    )
    USERS_STATS.update_one(
        {'_id': ObjectId(follow_id)},
        {'$inc': {
            'followers_count': increment_value
        }}
    )
