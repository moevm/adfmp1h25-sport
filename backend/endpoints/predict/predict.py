from datetime import datetime

import requests
from bson import ObjectId
from flask import Blueprint, request, jsonify

from endpoints.teams.model import EventData
from services.decorators import get_user_id
from services.get_env import KHL_URL
from services.mongo import USERS_PREDICTS

predict = Blueprint('predict', __name__)


@predict.route('/predict', methods=['GET'])
@get_user_id
def predict_f(id):
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
        USERS_PREDICTS.insert_one({
            "_id": ObjectId(id),
            "days": {
                event.start_at_day: {
                    event_id: score
                }
            }
        })

    return "ok", 200
