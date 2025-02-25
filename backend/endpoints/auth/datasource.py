from typing import Optional

from bson import ObjectId

from endpoints.auth.model import AuthData
from endpoints.stats.model import UserStats
from services.mongo import USERS_ENTRY_CL, USERS_PREDICTS, USERS_FOLLOWERS, USERS_STATS


def find_user(data: AuthData) -> Optional[str]:
    exist = USERS_ENTRY_CL.find_one({"login": data.login})
    if not exist:
        return None
    return str(exist['_id'])


def register_user(data: AuthData) -> Optional[str]:
    if not find_user(data):
        inserted_id = str(USERS_ENTRY_CL.insert_one(data.to_json()).inserted_id)
        USERS_PREDICTS.insert_one({"_id": ObjectId(inserted_id), "days": {}})
        USERS_FOLLOWERS.insert_one({"_id": ObjectId(inserted_id), "followers": []})
        USERS_STATS.insert_one(UserStats.from_json({}).to_json() | {"_id": ObjectId(inserted_id)})
        return inserted_id
    return None
