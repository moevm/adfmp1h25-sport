from datetime import datetime

import requests
from bson import ObjectId
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required

from endpoints.predict.pipelines import get_predicts_pipeline
from endpoints.teams.model import EventData
from services.decorators import get_user_id, token_required
from services.get_env import KHL_URL
from services.mongo import USERS_PREDICTS, USERS_PREDICTS_FOR_DAILY_SERVICE

predict_bp = Blueprint('predict', __name__)


@predict_bp.route('/predict', methods=['GET'])
@get_user_id
def make_predict(id):
    score = request.args.get('score')
    event_id = request.args.get('event')  # ID события

    response = requests.get(f"{KHL_URL}/event_v2.json?id={event_id}")
    try:
        event = EventData.from_json(response.json())
    except:
        return jsonify({'error': 'wrong id'}), 400

    current_timestamp = int(datetime.now().timestamp() * 1000)

    print(current_timestamp)
    print(event.start_at)

    if current_timestamp >= event.start_at:
        return jsonify({'error': 'match already started'}), 400

    is_exist = USERS_PREDICTS.find_one({"_id": ObjectId(id)})

    if is_exist:
        USERS_PREDICTS.update_one(
            {"_id": ObjectId(id)},
            {
                "$set": {
                    f"days.{event.start_at_day}.{event_id}": score
                }
            }
        )
    else:
        return {"message": "cant find users predicts"}, 404

    new_doc = USERS_PREDICTS_FOR_DAILY_SERVICE.find_one({"day": event.start_at_day})
    if new_doc:
        USERS_PREDICTS_FOR_DAILY_SERVICE.update_one(
            {"day": event.start_at_day},
            {"$set": {f"events.{event_id}.{id}": score}}
        )
    else:
        doc = {
            "day": event.start_at_day,
            "events": {
                event_id: {
                    id: score
                }
            }
        }
        USERS_PREDICTS_FOR_DAILY_SERVICE.insert_one(doc)

    return "ok", 200


@predict_bp.route('/get_predicts', methods=['GET'])
@get_user_id
def get_predicts(id):
    user_id = request.args.get('user_id', '')
    start_time = request.args.get('start_time', None)
    end_time = request.args.get('end_time', None)
    
    if user_id == 'current':
        user_id = id

    if not (user_id and start_time and end_time):
        return {"message": "missed parameters"}, 400
    res = list(USERS_PREDICTS.aggregate(get_predicts_pipeline(start_time, end_time, user_id)))
    if len(res) == 0:
        return {}, 200
    return res[0]['days']
