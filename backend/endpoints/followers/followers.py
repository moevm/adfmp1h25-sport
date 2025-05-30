from bson import ObjectId
from flask import Blueprint, jsonify, request
from endpoints.stats.services import get_user_level, get_users_stats, update_follows
from services.decorators import get_user_id
from services.mongo import USERS_FOLLOWERS, USERS_ENTRY_CL, USERS_STATS

followers_bp = Blueprint('followers', __name__)


@followers_bp.route('/get_followers', methods=['GET'])
@get_user_id
def get_followers(user_id):
    followers = USERS_FOLLOWERS.find_one({"_id": ObjectId(user_id)})
    if followers:
        users_ids = [ObjectId(temp_user_id) for temp_user_id in followers["followers"]]
        users_ids.append(ObjectId(user_id))
        res = get_users_stats(users_ids)
        return res
    return {"message": "cant find users followers"}, 404


@followers_bp.route('/subscribe', methods=['POST'])
@get_user_id
def subscribe(user_id):
    for_whom_subscribe = request.args.get("user_id")
    is_exist = USERS_ENTRY_CL.find({"_id": ObjectId(for_whom_subscribe)})

    if not is_exist:
        return {"message": "account doesnt exist"}, 400

    followers = USERS_FOLLOWERS.find_one({"_id": ObjectId(user_id)})
    if followers:
        followers_arr = followers["followers"]
        if for_whom_subscribe not in followers_arr:
            followers_arr.append(for_whom_subscribe)
            USERS_FOLLOWERS.update_one(
                {"_id": ObjectId(user_id)},
                {
                    "$set": {
                        "followers": followers_arr
                    }
                }
            )
            update_follows(user_id, for_whom_subscribe, subscribe=True)
            return "ok"
        return {"message": "already subscribed to this user"}, 400
    return {"message": "cant find users followers"}, 404


@followers_bp.route('/unsubscribe', methods=['POST'])
@get_user_id
def unsubscribe(user_id):
    from_whom_unsubscribe = request.args.get("user_id")
    followers = USERS_FOLLOWERS.find_one({"_id": ObjectId(user_id)})

    if followers:
        followers_arr = followers["followers"]
        if from_whom_unsubscribe in followers_arr:
            followers_arr.remove(from_whom_unsubscribe)

            USERS_FOLLOWERS.update_one(
                {"_id": ObjectId(user_id)},
                {
                    "$set": {
                        "followers": followers_arr
                    }
                }
            )
            update_follows(user_id, from_whom_unsubscribe, subscribe=False)
            return "ok"
        return {"message": "not subscribed to this user"}, 400
    return {"message": "cant find users followers"}, 404


@followers_bp.route('/set_avatar', methods=['POST'])
@get_user_id
def set_avatar(user_id):
    avatar = request.args.get("avatar")
    USERS_ENTRY_CL.update_one(
        {"_id": ObjectId(user_id)}, 
        {"$set": {"avatar": avatar}}
    )
    return "ok"


@followers_bp.route('/set_name', methods=['POST'])
@get_user_id
def set_name(user_id):
    name = request.args.get("name")
    USERS_ENTRY_CL.update_one(
        {"_id": ObjectId(user_id)}, 
        {"$set": {"login": name}}
    )
    return "ok"