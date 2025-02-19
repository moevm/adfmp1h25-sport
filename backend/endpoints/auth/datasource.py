from typing import Optional

from endpoints.auth.model import AuthData
from services.mongo import USERS_ENTRY_CL


def find_user(data: AuthData) -> Optional[str]:
    return str(USERS_ENTRY_CL.find_one({"login": data.login})['_id'])


def register_user(data: AuthData) -> Optional[str]:
    if not find_user(data):
        return str(USERS_ENTRY_CL.insert_one(data.to_json()).inserted_id)
    return None
